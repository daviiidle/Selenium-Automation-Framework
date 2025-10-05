/**
 * Maven test output parser
 * Extracts critical test information from mvn test output
 */

import { XMLParser } from 'fast-xml-parser';
import * as fs from 'fs';
import * as path from 'path';
import {
  TestFailure,
  TestSuccess,
  TestSkipped,
  TestSummary,
  MavenTestResult,
} from '../types/test-results.js';

export class TestParser {
  private frameworkRoot: string;

  constructor(frameworkRoot: string) {
    this.frameworkRoot = frameworkRoot;
  }

  /**
   * Parse Maven test output and extract all important details
   */
  async parseTestResults(rawOutput: string): Promise<MavenTestResult> {
    const summary = this.extractSummary(rawOutput);
    const failures = await this.extractFailures(rawOutput);
    const successes = await this.extractSuccesses();
    const skipped = await this.extractSkipped();

    return {
      summary,
      failures,
      successes,
      skipped,
      rawOutput,
    };
  }

  /**
   * Extract test summary from Maven output
   */
  private extractSummary(output: string): TestSummary {
    const summaryMatch = output.match(/Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)/);
    const timeMatch = output.match(/Total time:\s+(\d+:\d+\s+min|\d+\.\d+\s+s)/);

    const totalTests = summaryMatch ? parseInt(summaryMatch[1]) : 0;
    const failures = summaryMatch ? parseInt(summaryMatch[2]) : 0;
    const errors = summaryMatch ? parseInt(summaryMatch[3]) : 0;
    const skipped = summaryMatch ? parseInt(summaryMatch[4]) : 0;
    const passed = totalTests - failures - errors - skipped;

    return {
      totalTests,
      passed,
      failed: failures + errors,
      skipped,
      executionTime: this.parseExecutionTime(timeMatch ? timeMatch[1] : '0s'),
      timestamp: new Date().toISOString(),
    };
  }

  /**
   * Extract test failures from surefire reports
   */
  private async extractFailures(output: string): Promise<TestFailure[]> {
    const failures: TestFailure[] = [];
    const surefireDir = path.join(this.frameworkRoot, 'target', 'surefire-reports');

    try {
      if (!fs.existsSync(surefireDir)) {
        return this.extractFailuresFromOutput(output);
      }

      const files = fs.readdirSync(surefireDir);
      const xmlFiles = files.filter(f => f.endsWith('.xml'));

      for (const file of xmlFiles) {
        const filePath = path.join(surefireDir, file);
        const xmlContent = fs.readFileSync(filePath, 'utf-8');
        const parsed = this.parseXmlReport(xmlContent);
        failures.push(...parsed);
      }
    } catch (error) {
      console.error('Error parsing surefire reports:', error);
      return this.extractFailuresFromOutput(output);
    }

    return failures;
  }

  /**
   * Parse XML surefire report
   */
  private parseXmlReport(xmlContent: string): TestFailure[] {
    const failures: TestFailure[] = [];
    const parser = new XMLParser({
      ignoreAttributes: false,
      attributeNamePrefix: '@_',
    });

    try {
      const result = parser.parse(xmlContent);
      const testsuite = result.testsuite;

      if (!testsuite) return failures;

      const testcases = Array.isArray(testsuite.testcase)
        ? testsuite.testcase
        : [testsuite.testcase];

      for (const testcase of testcases) {
        if (testcase?.failure || testcase?.error) {
          const failure = testcase.failure || testcase.error;
          const errorMessage = failure['@_message'] || failure['#text'] || 'Unknown error';
          const stackTrace = failure['#text'] || '';

          failures.push({
            testClass: testcase['@_classname'] || 'Unknown',
            testMethod: testcase['@_name'] || 'Unknown',
            errorType: failure['@_type'] || 'Unknown',
            errorMessage,
            stackTrace,
            executionTime: parseFloat(testcase['@_time'] || '0'),
            category: this.categorizeError(errorMessage, stackTrace),
          });
        }
      }
    } catch (error) {
      console.error('XML parsing error:', error);
    }

    return failures;
  }

  /**
   * Extract failures from raw output as fallback
   */
  private extractFailuresFromOutput(output: string): TestFailure[] {
    const failures: TestFailure[] = [];
    const failurePattern = /(\w+\.\w+\.\w+)\.(\w+)\s*Time elapsed:.*?<<<\s*FAILURE!([\s\S]*?)(?=\n\n|\nTests run:)/g;

    let match;
    while ((match = failurePattern.exec(output)) !== null) {
      const [, testClass, testMethod, errorDetails] = match;
      const errorLines = errorDetails.split('\n');
      const errorMessage = errorLines[0]?.trim() || 'Unknown error';
      const stackTrace = errorDetails.trim();

      failures.push({
        testClass,
        testMethod,
        errorType: this.extractErrorType(errorMessage),
        errorMessage,
        stackTrace,
        executionTime: 0,
        category: this.categorizeError(errorMessage, stackTrace),
      });
    }

    return failures;
  }

  /**
   * Extract successful tests
   */
  private async extractSuccesses(): Promise<TestSuccess[]> {
    const successes: TestSuccess[] = [];
    const surefireDir = path.join(this.frameworkRoot, 'target', 'surefire-reports');

    try {
      if (!fs.existsSync(surefireDir)) return successes;

      const files = fs.readdirSync(surefireDir);
      const xmlFiles = files.filter(f => f.endsWith('.xml'));

      for (const file of xmlFiles) {
        const filePath = path.join(surefireDir, file);
        const xmlContent = fs.readFileSync(filePath, 'utf-8');
        const parser = new XMLParser({ ignoreAttributes: false, attributeNamePrefix: '@_' });
        const result = parser.parse(xmlContent);
        const testsuite = result.testsuite;

        if (!testsuite) continue;

        const testcases = Array.isArray(testsuite.testcase)
          ? testsuite.testcase
          : [testsuite.testcase];

        for (const testcase of testcases) {
          if (!testcase?.failure && !testcase?.error && !testcase?.skipped) {
            successes.push({
              testClass: testcase['@_classname'] || 'Unknown',
              testMethod: testcase['@_name'] || 'Unknown',
              executionTime: parseFloat(testcase['@_time'] || '0'),
            });
          }
        }
      }
    } catch (error) {
      console.error('Error extracting successes:', error);
    }

    return successes;
  }

  /**
   * Extract skipped tests
   */
  private async extractSkipped(): Promise<TestSkipped[]> {
    const skipped: TestSkipped[] = [];
    const surefireDir = path.join(this.frameworkRoot, 'target', 'surefire-reports');

    try {
      if (!fs.existsSync(surefireDir)) return skipped;

      const files = fs.readdirSync(surefireDir);
      const xmlFiles = files.filter(f => f.endsWith('.xml'));

      for (const file of xmlFiles) {
        const filePath = path.join(surefireDir, file);
        const xmlContent = fs.readFileSync(filePath, 'utf-8');
        const parser = new XMLParser({ ignoreAttributes: false, attributeNamePrefix: '@_' });
        const result = parser.parse(xmlContent);
        const testsuite = result.testsuite;

        if (!testsuite) continue;

        const testcases = Array.isArray(testsuite.testcase)
          ? testsuite.testcase
          : [testsuite.testcase];

        for (const testcase of testcases) {
          if (testcase?.skipped) {
            skipped.push({
              testClass: testcase['@_classname'] || 'Unknown',
              testMethod: testcase['@_name'] || 'Unknown',
              reason: testcase.skipped['@_message'] || 'Unknown reason',
            });
          }
        }
      }
    } catch (error) {
      console.error('Error extracting skipped tests:', error);
    }

    return skipped;
  }

  /**
   * Categorize error type
   */
  private categorizeError(errorMessage: string, stackTrace: string): TestFailure['category'] {
    const message = errorMessage.toLowerCase();
    const trace = stackTrace.toLowerCase();

    if (message.includes('assertion') || message.includes('expected') || message.includes('assert')) {
      return 'ASSERTION';
    }
    if (message.includes('nosuchelement') || message.includes('element not found') || message.includes('unable to locate')) {
      return 'SELECTOR';
    }
    if (message.includes('timeout') || message.includes('timed out')) {
      return 'TIMEOUT';
    }
    if (message.includes('configuration') || message.includes('beforemethod') || message.includes('aftermethod')) {
      return 'CONFIGURATION';
    }
    return 'UNKNOWN';
  }

  /**
   * Extract error type from message
   */
  private extractErrorType(errorMessage: string): string {
    const match = errorMessage.match(/^(\w+(?:\.\w+)*Exception|\w+(?:\.\w+)*Error):/);
    return match ? match[1] : 'Unknown';
  }

  /**
   * Parse execution time string to seconds
   */
  private parseExecutionTime(timeStr: string): number {
    if (timeStr.includes('min')) {
      const match = timeStr.match(/(\d+):(\d+)/);
      if (match) {
        return parseInt(match[1]) * 60 + parseInt(match[2]);
      }
    }
    const match = timeStr.match(/(\d+\.\d+)/);
    return match ? parseFloat(match[1]) : 0;
  }
}
