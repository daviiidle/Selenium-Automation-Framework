/**
 * TestNG and Surefire Report Parser
 * Analyzes test results and failures
 */
import { promises as fs } from 'fs';
import path from 'path';
import { XMLParser } from 'fast-xml-parser';
export class TestNGParser {
    frameworkRoot;
    parser;
    constructor(frameworkRoot) {
        this.frameworkRoot = frameworkRoot;
        this.parser = new XMLParser({
            ignoreAttributes: false,
            attributeNamePrefix: '@_',
        });
    }
    /**
     * Parse TestNG results XML
     */
    async parseTestNGResults(resultsFile = 'testng-results.xml') {
        const resultsPath = path.join(this.frameworkRoot, 'target/surefire-reports', resultsFile);
        try {
            const xmlContent = await fs.readFile(resultsPath, 'utf-8');
            const parsed = this.parser.parse(xmlContent);
            const testResults = [];
            const testngResults = parsed['testng-results'];
            if (!testngResults) {
                throw new Error('Invalid TestNG results format');
            }
            // Extract suite information
            const suite = testngResults.suite;
            let tests = 0;
            let failures = 0;
            let skipped = 0;
            // Parse test cases
            if (suite && suite.test) {
                const testElements = Array.isArray(suite.test) ? suite.test : [suite.test];
                for (const test of testElements) {
                    if (test.class) {
                        const classes = Array.isArray(test.class) ? test.class : [test.class];
                        for (const cls of classes) {
                            const className = cls['@_name'];
                            if (cls['test-method']) {
                                const methods = Array.isArray(cls['test-method']) ? cls['test-method'] : [cls['test-method']];
                                for (const method of methods) {
                                    if (method['@_is-config'] === 'true')
                                        continue; // Skip config methods
                                    tests++;
                                    const status = method['@_status'];
                                    const duration = parseFloat(method['@_duration-ms'] || '0');
                                    let errorMessage;
                                    let stackTrace;
                                    let errorType;
                                    if (status === 'FAIL') {
                                        failures++;
                                        if (method.exception) {
                                            errorMessage = method.exception['@_class'] || method.exception.message;
                                            stackTrace = method.exception['full-stacktrace'] || '';
                                            errorType = this.categorizeError(errorMessage, stackTrace);
                                        }
                                    }
                                    else if (status === 'SKIP') {
                                        skipped++;
                                    }
                                    testResults.push({
                                        testClass: className,
                                        testMethod: method['@_name'],
                                        status: status,
                                        duration,
                                        errorMessage,
                                        stackTrace,
                                        errorType,
                                    });
                                }
                            }
                        }
                    }
                }
            }
            return {
                name: suite?.['@_name'] || 'Test Suite',
                tests,
                failures,
                skipped,
                time: parseFloat(suite?.['@_duration-ms'] || '0'),
                testResults,
            };
        }
        catch (error) {
            throw new Error(`Failed to parse TestNG results: ${error}`);
        }
    }
    /**
     * Get failed tests only
     */
    async getFailedTests() {
        const results = await this.parseTestNGResults();
        return results.testResults.filter(t => t.status === 'FAIL');
    }
    /**
     * Analyze failure patterns
     */
    async analyzeFailurePatterns() {
        const failedTests = await this.getFailedTests();
        const patterns = new Map();
        for (const test of failedTests) {
            if (!test.errorMessage)
                continue;
            const category = this.categorizeError(test.errorMessage, test.stackTrace);
            const pattern = this.extractErrorPattern(test.errorMessage);
            const key = `${category}:${pattern}`;
            if (!patterns.has(key)) {
                patterns.set(key, {
                    pattern,
                    count: 0,
                    affectedTests: [],
                    category,
                });
            }
            const p = patterns.get(key);
            p.count++;
            p.affectedTests.push(`${test.testClass}.${test.testMethod}`);
        }
        return Array.from(patterns.values()).sort((a, b) => b.count - a.count);
    }
    /**
     * Categorize error type
     */
    categorizeError(errorMessage, stackTrace) {
        const msg = errorMessage.toLowerCase();
        const trace = (stackTrace || '').toLowerCase();
        if (msg.includes('nosuchelementexception') || msg.includes('element not found')) {
            return 'selector';
        }
        if (msg.includes('timeout') || msg.includes('wait')) {
            return 'timeout';
        }
        if (msg.includes('staleelementreferenceexception') || msg.includes('stale element')) {
            return 'stale-element';
        }
        if (msg.includes('connection') || msg.includes('network') || msg.includes('unreachable')) {
            return 'network';
        }
        if (msg.includes('assert') || msg.includes('expected')) {
            return 'assertion';
        }
        return 'unknown';
    }
    /**
     * Extract error pattern for grouping
     */
    extractErrorPattern(errorMessage) {
        // Remove specific values/numbers to create a pattern
        let pattern = errorMessage
            .replace(/\d+/g, 'N')
            .replace(/"[^"]+"/g, '"..."')
            .replace(/\([^)]+\)/g, '(...)')
            .substring(0, 100);
        return pattern;
    }
    /**
     * Get test execution summary
     */
    async getSummary() {
        const results = await this.parseTestNGResults();
        const passed = results.tests - results.failures - results.skipped;
        const passRate = results.tests > 0 ? (passed / results.tests) * 100 : 0;
        return {
            total: results.tests,
            passed,
            failed: results.failures,
            skipped: results.skipped,
            passRate: Math.round(passRate * 100) / 100,
            totalTime: results.time,
        };
    }
    /**
     * Check if test results exist
     */
    async hasTestResults() {
        const resultsPath = path.join(this.frameworkRoot, 'target/surefire-reports/testng-results.xml');
        try {
            await fs.access(resultsPath);
            return true;
        }
        catch {
            return false;
        }
    }
}
//# sourceMappingURL=testng-parser.js.map