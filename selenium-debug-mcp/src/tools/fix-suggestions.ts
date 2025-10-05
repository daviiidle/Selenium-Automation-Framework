/**
 * Fix Suggestion Engine
 * Provides automated fix suggestions based on error analysis
 */

import { TestNGParser } from '../analyzers/testng-parser.js';
import { FrameworkReader } from '../utils/framework-reader.js';

interface FixSuggestion {
  issue: string;
  category: string;
  fix: string;
  codeExample?: string;
  priority: 'high' | 'medium' | 'low';
}

export function registerFixSuggestionTools(frameworkRoot: string) {
  const testngParser = new TestNGParser(frameworkRoot);
  const reader = new FrameworkReader(frameworkRoot);

  return [
    {
      name: 'suggest-fixes',
      description: 'Analyze failures and suggest automated fixes',
      inputSchema: {
        type: 'object',
        properties: {
          testName: {
            type: 'string',
            description: 'Specific test to analyze (optional, analyzes all failures if not provided)',
          },
        },
      },
      handler: async (args: any) => {
        const failedTests = await testngParser.getFailedTests();

        if (failedTests.length === 0) {
          return {
            message: 'No test failures found!',
            suggestions: [],
          };
        }

        const testsToAnalyze = args.testName
          ? failedTests.filter(t => t.testMethod === args.testName || `${t.testClass}.${t.testMethod}` === args.testName)
          : failedTests;

        const suggestions: FixSuggestion[] = [];

        for (const test of testsToAnalyze) {
          const testSuggestions = generateSuggestions(test);
          suggestions.push(...testSuggestions);
        }

        // Group by category
        const grouped = suggestions.reduce((acc, s) => {
          if (!acc[s.category]) acc[s.category] = [];
          acc[s.category].push(s);
          return acc;
        }, {} as Record<string, FixSuggestion[]>);

        return {
          totalFailures: testsToAnalyze.length,
          totalSuggestions: suggestions.length,
          suggestions,
          groupedByCategory: grouped,
        };
      },
    },

    {
      name: 'diagnose-error',
      description: 'Diagnose a specific error message and provide detailed fix recommendations',
      inputSchema: {
        type: 'object',
        properties: {
          errorMessage: {
            type: 'string',
            description: 'Error message to diagnose',
          },
          errorType: {
            type: 'string',
            description: 'Error type/class (e.g., NoSuchElementException)',
          },
        },
        required: ['errorMessage'],
      },
      handler: async (args: any) => {
        const diagnosis = diagnoseError(args.errorMessage, args.errorType);
        return diagnosis;
      },
    },

    {
      name: 'suggest-wait-fix',
      description: 'Suggest wait strategy fixes for timeout issues',
      inputSchema: {
        type: 'object',
        properties: {
          currentWait: {
            type: 'number',
            description: 'Current wait time in milliseconds',
          },
          errorMessage: {
            type: 'string',
          },
        },
      },
      handler: async (args: any) => {
        const suggestions = [];

        // Get framework config
        const config = await reader.getBaseTestConfig();
        const currentImplicit = config.implicitWait || 10000;

        suggestions.push({
          type: 'increase-implicit-wait',
          current: currentImplicit,
          suggested: currentImplicit + 5000,
          location: 'WebDriverFactory.java configureDriver() method',
          codeExample: `driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(${(currentImplicit + 5000) / 1000}));`,
        });

        suggestions.push({
          type: 'add-explicit-wait',
          description: 'Add explicit wait for specific element',
          codeExample: `WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.elementToBeClickable(element));`,
        });

        suggestions.push({
          type: 'use-fluent-wait',
          description: 'Use fluent wait with polling',
          codeExample: `FluentWait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class);`,
        });

        return {
          currentConfig: config,
          suggestions,
        };
      },
    },

    {
      name: 'suggest-selector-fix',
      description: 'Suggest selector improvements based on common issues',
      inputSchema: {
        type: 'object',
        properties: {
          selectorType: {
            type: 'string',
          },
          selectorValue: {
            type: 'string',
          },
          errorMessage: {
            type: 'string',
          },
        },
        required: ['selectorValue'],
      },
      handler: async (args: any) => {
        const suggestions = [];

        // Check for brittle selectors
        if (args.selectorValue.includes('nth-child') || args.selectorValue.includes('nth-of-type')) {
          suggestions.push({
            issue: 'Brittle positional selector',
            fix: 'Use attribute-based selectors instead',
            priority: 'high',
            example: 'Replace nth-child with [data-testid] or [name] attribute',
          });
        }

        // Check for overly complex CSS selectors
        if (args.selectorValue.split(' > ').length > 4) {
          suggestions.push({
            issue: 'Overly complex selector chain',
            fix: 'Simplify selector to reduce brittleness',
            priority: 'medium',
            example: 'Use direct ID or class name instead of deep nesting',
          });
        }

        // Suggest ID-based if possible
        suggestions.push({
          issue: 'General best practice',
          fix: 'Prefer ID selectors for stability',
          priority: 'low',
          example: 'If element has ID, use By.id() instead of CSS',
        });

        return {
          currentSelector: args.selectorValue,
          suggestions,
        };
      },
    },

    {
      name: 'suggest-parallel-fixes',
      description: 'Suggest fixes for parallel execution issues',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const patterns = await testngParser.analyzeFailurePatterns();

        // Check for thread-safety issues
        const suggestions = [
          {
            issue: 'WebDriver thread-safety',
            fix: 'Ensure WebDriver is ThreadLocal',
            priority: 'high' as const,
            location: 'WebDriverFactory.java',
            currentImplementation: 'Already using ThreadLocal<WebDriver>',
            status: 'OK',
          },
          {
            issue: 'Reduce thread count for stability',
            fix: 'Lower thread count from 12 to 8',
            priority: 'medium' as const,
            location: 'pom.xml or testng.xml',
            codeExample: '<threadCount>8</threadCount>',
          },
          {
            issue: 'Enable retry for flaky tests',
            fix: 'Use RetryAnalyzer for unstable tests',
            priority: 'medium' as const,
            location: '@Test annotation',
            codeExample: '@Test(retryAnalyzer = RetryAnalyzer.class)',
          },
        ];

        return {
          parallelConfig: {
            currentThreads: 8,
            suggestedThreads: 6,
          },
          suggestions,
        };
      },
    },
  ];
}

/**
 * Generate suggestions for a specific test failure
 */
function generateSuggestions(test: any): FixSuggestion[] {
  const suggestions: FixSuggestion[] = [];
  const errorMsg = (test.errorMessage || '').toLowerCase();

  if (errorMsg.includes('nosuchelementexception')) {
    suggestions.push({
      issue: `Element not found in ${test.testMethod}`,
      category: 'selector',
      fix: 'Increase wait time or fix selector',
      codeExample: 'WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));',
      priority: 'high',
    });
  }

  if (errorMsg.includes('timeout')) {
    suggestions.push({
      issue: `Timeout in ${test.testMethod}`,
      category: 'timeout',
      fix: 'Increase timeout or optimize page load',
      codeExample: 'driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));',
      priority: 'high',
    });
  }

  if (errorMsg.includes('staleelementreferenceexception')) {
    suggestions.push({
      issue: `Stale element in ${test.testMethod}`,
      category: 'stale-element',
      fix: 'Re-find element before interaction',
      codeExample: 'element = driver.findElement(locator); // Refresh element reference',
      priority: 'medium',
    });
  }

  return suggestions;
}

/**
 * Diagnose specific error
 */
function diagnoseError(errorMessage: string, errorType?: string) {
  const msg = errorMessage.toLowerCase();

  const diagnosis: any = {
    errorMessage,
    errorType,
    category: 'unknown',
    likelyCause: '',
    suggestedFixes: [],
  };

  if (msg.includes('nosuchelementexception') || msg.includes('element not found')) {
    diagnosis.category = 'selector';
    diagnosis.likelyCause = 'Element selector is incorrect, element not yet loaded, or element is in iframe';
    diagnosis.suggestedFixes = [
      'Verify selector is correct using browser DevTools',
      'Add explicit wait: WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15))',
      'Check if element is inside an iframe and switch context',
      'Ensure element is not hidden by CSS (display:none)',
    ];
  } else if (msg.includes('timeout')) {
    diagnosis.category = 'timeout';
    diagnosis.likelyCause = 'Page taking too long to load or element never appears';
    diagnosis.suggestedFixes = [
      'Increase page load timeout in WebDriverFactory',
      'Add explicit wait for specific condition',
      'Check network connectivity and page performance',
      'Verify element actually exists on the page',
    ];
  } else if (msg.includes('staleelementreferenceexception')) {
    diagnosis.category = 'stale-element';
    diagnosis.likelyCause = 'DOM was refreshed/updated after element was found';
    diagnosis.suggestedFixes = [
      'Re-find element before each interaction',
      'Use findElement() instead of storing element reference',
      'Add retry logic for stale elements',
      'Wait for AJAX/JavaScript to complete before interaction',
    ];
  }

  return diagnosis;
}
