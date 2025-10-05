/**
 * Maven test execution tools for MCP server
 */

import { MavenExecutor } from '../utils/maven-executor.js';
import { TestParser } from '../utils/test-parser.js';
import { FixAnalyzer } from '../utils/fix-analyzer.js';

export function registerMavenTestTools(frameworkRoot: string) {
  const mavenExecutor = new MavenExecutor(frameworkRoot);
  const testParser = new TestParser(frameworkRoot);
  const fixAnalyzer = new FixAnalyzer();

  return [
    {
      name: 'run-all-tests',
      description: 'Execute all Maven tests and extract failure details',
      inputSchema: {
        type: 'object',
        properties: {
          parallel: {
            type: 'boolean',
            description: 'Run tests in parallel',
            default: false,
          },
          threadCount: {
            type: 'number',
            description: 'Number of parallel threads',
            default: 4,
          },
          timeout: {
            type: 'number',
            description: 'Test execution timeout in milliseconds',
            default: 600000,
          },
        },
      },
      handler: async (args: any) => {
        const { parallel, threadCount, timeout } = args;
        const result = await mavenExecutor.runTests({ parallel, threadCount, timeout });
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        return {
          success: result.exitCode === 0,
          summary: parsedResults.summary,
          failures: parsedResults.failures,
          successes: parsedResults.successes.map(s => `${s.testClass}.${s.testMethod}`),
          skipped: parsedResults.skipped,
          fixSuggestions: fixAnalyzer.analyzeFailures(parsedResults.failures),
          patterns: fixAnalyzer.identifyPatterns(parsedResults.failures),
        };
      },
    },
    {
      name: 'run-test-class',
      description: 'Run specific test class and get detailed results',
      inputSchema: {
        type: 'object',
        properties: {
          testClass: {
            type: 'string',
            description: 'Test class name (e.g., ComprehensiveLoginTests)',
          },
          timeout: {
            type: 'number',
            description: 'Timeout in milliseconds',
            default: 300000,
          },
        },
        required: ['testClass'],
      },
      handler: async (args: any) => {
        const { testClass, timeout } = args;
        const result = await mavenExecutor.runTestClass(testClass, timeout);
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        return {
          success: result.exitCode === 0,
          testClass,
          summary: parsedResults.summary,
          failures: parsedResults.failures,
          fixSuggestions: fixAnalyzer.analyzeFailures(parsedResults.failures),
          stdout: result.stdout.slice(-2000), // Last 2000 chars
          stderr: result.stderr.slice(-2000), // Last 2000 chars
          exitCode: result.exitCode,
        };
      },
    },
    {
      name: 'run-test-method',
      description: 'Run specific test method and get detailed results',
      inputSchema: {
        type: 'object',
        properties: {
          testClass: {
            type: 'string',
            description: 'Test class name',
          },
          testMethod: {
            type: 'string',
            description: 'Test method name',
          },
          timeout: {
            type: 'number',
            description: 'Timeout in milliseconds',
            default: 120000,
          },
        },
        required: ['testClass', 'testMethod'],
      },
      handler: async (args: any) => {
        const { testClass, testMethod, timeout } = args;
        const result = await mavenExecutor.runTestMethod(testClass, testMethod, timeout);
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        return {
          success: result.exitCode === 0,
          testClass,
          testMethod,
          summary: parsedResults.summary,
          failures: parsedResults.failures,
          fixSuggestions: fixAnalyzer.analyzeFailures(parsedResults.failures),
          stdout: result.stdout.slice(-2000), // Last 2000 chars
          stderr: result.stderr.slice(-2000), // Last 2000 chars
          exitCode: result.exitCode,
        };
      },
    },
    {
      name: 'get-failure-summary',
      description: 'Get summary of test failures with fix suggestions',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        // Run tests to get latest results
        const result = await mavenExecutor.runTests({});
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        const groupedFailures = fixAnalyzer.groupFailuresByCategory(parsedResults.failures);
        const patterns = fixAnalyzer.identifyPatterns(parsedResults.failures);

        return {
          summary: parsedResults.summary,
          failuresByCategory: Object.fromEntries(
            Array.from(groupedFailures.entries()).map(([cat, failures]) => [
              cat,
              failures.map(f => ({
                test: `${f.testClass}.${f.testMethod}`,
                error: f.errorMessage,
              })),
            ])
          ),
          patterns,
          criticalFixes: fixAnalyzer
            .analyzeFailures(parsedResults.failures)
            .filter(f => f.priority === 'HIGH'),
        };
      },
    },
    {
      name: 'analyze-failure',
      description: 'Analyze specific test failure and get detailed fix suggestions',
      inputSchema: {
        type: 'object',
        properties: {
          testClass: {
            type: 'string',
            description: 'Test class name',
          },
          testMethod: {
            type: 'string',
            description: 'Test method name',
          },
        },
        required: ['testClass', 'testMethod'],
      },
      handler: async (args: any) => {
        const { testClass, testMethod } = args;
        const result = await mavenExecutor.runTestMethod(testClass, testMethod);
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        const failure = parsedResults.failures.find(
          f => f.testClass === testClass && f.testMethod === testMethod
        );

        if (!failure) {
          return {
            success: true,
            message: 'Test passed or not found',
          };
        }

        const fixSuggestion = fixAnalyzer.analyzeFailures([failure])[0];

        return {
          failure: {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            errorType: failure.errorType,
            errorMessage: failure.errorMessage,
            category: failure.category,
            stackTrace: failure.stackTrace,
          },
          fixSuggestion,
          relatedTools: getRelatedTools(failure.category),
        };
      },
    },
    {
      name: 'compile-tests',
      description: 'Compile tests without running them',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const result = await mavenExecutor.compileTests();
        return {
          success: !result.stderr.includes('ERROR'),
          output: result.stdout,
          errors: result.stderr,
        };
      },
    },
    {
      name: 'get-test-patterns',
      description: 'Identify common failure patterns across all tests',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const result = await mavenExecutor.runTests({});
        const parsedResults = await testParser.parseTestResults(result.stdout + result.stderr);

        const patterns = fixAnalyzer.identifyPatterns(parsedResults.failures);
        const groupedFailures = fixAnalyzer.groupFailuresByCategory(parsedResults.failures);

        return {
          totalFailures: parsedResults.failures.length,
          patterns,
          categoryBreakdown: Object.fromEntries(
            Array.from(groupedFailures.entries()).map(([cat, failures]) => [
              cat,
              failures.length,
            ])
          ),
          recommendations: getPatternRecommendations(patterns, groupedFailures),
        };
      },
    },
  ];

  function getRelatedTools(category: string): string[] {
    const toolMap: Record<string, string[]> = {
      SELECTOR: [
        'validate-selector-live',
        'find-alternative-selectors',
        'update-page-object-selector',
        'update-selector-file',
      ],
      TIMEOUT: ['test-wait-strategies', 'update-wait-time'],
      CONFIGURATION: ['check-testng-config'],
      ASSERTION: [],
      UNKNOWN: [],
    };
    return toolMap[category] || [];
  }

  function getPatternRecommendations(
    patterns: string[],
    groupedFailures: Map<string, any[]>
  ): string[] {
    const recommendations: string[] = [];

    const selectorCount = groupedFailures.get('SELECTOR')?.length || 0;
    const timeoutCount = groupedFailures.get('TIMEOUT')?.length || 0;

    if (selectorCount > 5) {
      recommendations.push(
        'Multiple selector failures detected - consider reviewing page structure changes or updating all selectors at once'
      );
    }

    if (timeoutCount > 3) {
      recommendations.push(
        'Multiple timeout failures - consider increasing global wait times or checking application performance'
      );
    }

    if (patterns.some(p => p.includes('NoSuchElementException'))) {
      recommendations.push(
        'NoSuchElementException is common - use validate-selector-json to check all selectors on live site'
      );
    }

    return recommendations;
  }
}
