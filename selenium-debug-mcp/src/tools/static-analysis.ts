/**
 * Static Analysis Tools
 * Analyze test results, logs, and configuration without running tests
 */

import { TestNGParser } from '../analyzers/testng-parser.js';
import { FrameworkReader } from '../utils/framework-reader.js';
import { promises as fs } from 'fs';
import path from 'path';

export function registerStaticAnalysisTools(frameworkRoot: string) {
  const testngParser = new TestNGParser(frameworkRoot);
  const reader = new FrameworkReader(frameworkRoot);

  return [
    {
      name: 'analyze-test-failures',
      description: 'Analyze test failures from the last test run',
      inputSchema: {
        type: 'object',
        properties: {
          detailed: {
            type: 'boolean',
            default: false,
            description: 'Include detailed stack traces',
          },
        },
      },
      handler: async (args: any) => {
        const hasResults = await testngParser.hasTestResults();
        if (!hasResults) {
          return {
            error: 'No test results found. Run mvn test first.',
          };
        }

        const failedTests = await testngParser.getFailedTests();
        const patterns = await testngParser.analyzeFailurePatterns();
        const summary = await testngParser.getSummary();

        const response: any = {
          summary,
          totalFailures: failedTests.length,
          failurePatterns: patterns,
          failedTests: failedTests.map(t => ({
            test: `${t.testClass}.${t.testMethod}`,
            error: t.errorMessage,
            category: t.errorType,
            duration: t.duration,
          })),
        };

        if (args.detailed) {
          response.detailedFailures = failedTests.map(t => ({
            test: `${t.testClass}.${t.testMethod}`,
            error: t.errorMessage,
            stackTrace: t.stackTrace,
          }));
        }

        return response;
      },
    },

    {
      name: 'get-test-summary',
      description: 'Get summary of last test execution',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const hasResults = await testngParser.hasTestResults();
        if (!hasResults) {
          return {
            error: 'No test results found. Run mvn test first.',
          };
        }

        const summary = await testngParser.getSummary();
        return summary;
      },
    },

    {
      name: 'analyze-failure-patterns',
      description: 'Identify common failure patterns across tests',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const patterns = await testngParser.analyzeFailurePatterns();

        const categorized = {
          selector: patterns.filter(p => p.category === 'selector'),
          timeout: patterns.filter(p => p.category === 'timeout'),
          staleElement: patterns.filter(p => p.category === 'stale-element'),
          network: patterns.filter(p => p.category === 'network'),
          assertion: patterns.filter(p => p.category === 'assertion'),
          unknown: patterns.filter(p => p.category === 'unknown'),
        };

        return {
          totalPatterns: patterns.length,
          categorized,
          topPatterns: patterns.slice(0, 5),
        };
      },
    },

    {
      name: 'check-testng-config',
      description: 'Validate TestNG configuration for issues',
      inputSchema: {
        type: 'object',
        properties: {
          configFile: {
            type: 'string',
            default: 'testng-powerhouse.xml',
          },
        },
      },
      handler: async (args: any) => {
        try {
          const config = await reader.readTestNGConfig(args.configFile);

          const issues = [];
          const recommendations = [];

          // Check parallel execution
          if (config.includes('parallel="methods"')) {
            recommendations.push({
              type: 'parallel',
              message: 'Parallel execution at method level detected. Ensure tests are thread-safe.',
            });
          }

          // Check thread count
          const threadMatch = config.match(/thread-count="(\d+)"/);
          if (threadMatch) {
            const threadCount = parseInt(threadMatch[1]);
            if (threadCount > 12) {
              recommendations.push({
                type: 'thread-count',
                message: `High thread count (${threadCount}). May cause resource contention.`,
                suggestion: 'Consider reducing to 8-12 threads for stability.',
              });
            }
          }

          // Check timeout
          const timeoutMatch = config.match(/time-out="(\d+)"/);
          if (timeoutMatch) {
            const timeout = parseInt(timeoutMatch[1]);
            if (timeout < 60000) {
              issues.push({
                type: 'timeout',
                message: `Low timeout value (${timeout}ms). May cause premature test failures.`,
                suggestion: 'Consider increasing to at least 120000ms (2 minutes).',
              });
            }
          }

          return {
            configFile: args.configFile,
            issues,
            recommendations,
            config: config.substring(0, 500) + '...',
          };
        } catch (error) {
          return {
            error: `Failed to read TestNG config: ${error}`,
          };
        }
      },
    },

    {
      name: 'inspect-page-object',
      description: 'Inspect a Page Object class and extract selector information',
      inputSchema: {
        type: 'object',
        properties: {
          pageClassName: {
            type: 'string',
            description: 'Page Object class name (e.g., "HomePage", "LoginPage")',
          },
        },
        required: ['pageClassName'],
      },
      handler: async (args: any) => {
        try {
          const content = await reader.readPageObject(args.pageClassName);
          const selectors = reader.extractSelectorsFromPageObject(content);

          return {
            className: args.pageClassName,
            selectorsFound: selectors.length,
            selectors,
            codePreview: content.substring(0, 500) + '...',
          };
        } catch (error) {
          return {
            error: `Failed to read Page Object: ${error}`,
          };
        }
      },
    },

    {
      name: 'inspect-test-class',
      description: 'Inspect a test class and extract test methods',
      inputSchema: {
        type: 'object',
        properties: {
          testClassName: {
            type: 'string',
            description: 'Test class name (e.g., "ComprehensiveLoginTests")',
          },
        },
        required: ['testClassName'],
      },
      handler: async (args: any) => {
        try {
          const content = await reader.readTestClass(args.testClassName);
          const testMethods = reader.extractTestMethods(content);

          return {
            className: args.testClassName,
            testsFound: testMethods.length,
            testMethods,
            codePreview: content.substring(0, 500) + '...',
          };
        } catch (error) {
          return {
            error: `Failed to read test class: ${error}`,
          };
        }
      },
    },

    {
      name: 'list-selector-files',
      description: 'List all available selector JSON files',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const files = await reader.getAllSelectorFiles();
        return {
          selectorFiles: files,
          count: files.length,
        };
      },
    },

    {
      name: 'read-selector-file',
      description: 'Read and display a selector JSON file',
      inputSchema: {
        type: 'object',
        properties: {
          selectorFile: {
            type: 'string',
            description: 'Selector file name (e.g., "homepage-selectors.json")',
          },
        },
        required: ['selectorFile'],
      },
      handler: async (args: any) => {
        try {
          const selectors = await reader.readSelectorFile(args.selectorFile);
          return {
            file: args.selectorFile,
            selectors,
            count: Object.keys(selectors).length,
          };
        } catch (error) {
          return {
            error: `Failed to read selector file: ${error}`,
          };
        }
      },
    },
  ];
}
