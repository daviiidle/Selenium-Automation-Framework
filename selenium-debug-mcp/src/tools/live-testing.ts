/**
 * Live Testing Tools
 * Real-time Selenium test execution and debugging
 */

import { BrowserController, SelectorInfo } from '../selenium/browser-controller.js';
import { FrameworkReader } from '../utils/framework-reader.js';
import { z } from 'zod';

const BASE_URL = process.env.BASE_URL || 'https://demowebshop.tricentis.com';

export function registerLiveTestingTools(frameworkRoot: string) {
  const reader = new FrameworkReader(frameworkRoot);

  return [
    {
      name: 'validate-selector-live',
      description: 'Test a selector on the live website to verify it works and get detailed element information',
      inputSchema: {
        type: 'object',
        properties: {
          selectorType: {
            type: 'string',
            enum: ['css', 'xpath', 'id', 'name', 'className'],
            description: 'Type of selector',
          },
          selectorValue: {
            type: 'string',
            description: 'Selector value',
          },
          page: {
            type: 'string',
            description: 'Page to navigate to (e.g., "/login", "/register")',
            default: '/',
          },
          waitTime: {
            type: 'number',
            description: 'Wait time in milliseconds',
            default: 10000,
          },
        },
        required: ['selectorType', 'selectorValue'],
      },
      handler: async (args: any) => {
        const browser = new BrowserController();
        try {
          const url = BASE_URL + (args.page || '/');
          await browser.navigateTo(url);

          const selector: SelectorInfo = {
            type: args.selectorType,
            value: args.selectorValue,
          };

          const result = await browser.validateElement(selector, args.waitTime || 10000);

          if (!result.found) {
            // Try to find alternatives
            const pageSource = await browser.getPageSource();
            return {
              success: false,
              result,
              pageUrl: url,
              suggestion: 'Element not found. Try using find-alternative-selectors tool.',
            };
          }

          return {
            success: true,
            result,
            pageUrl: url,
          };
        } finally {
          await browser.quit();
        }
      },
    },

    {
      name: 'test-wait-strategies',
      description: 'Test different wait strategies for a selector to find optimal timing',
      inputSchema: {
        type: 'object',
        properties: {
          selectorType: {
            type: 'string',
            enum: ['css', 'xpath', 'id', 'name', 'className'],
          },
          selectorValue: {
            type: 'string',
          },
          page: {
            type: 'string',
            default: '/',
          },
        },
        required: ['selectorType', 'selectorValue'],
      },
      handler: async (args: any) => {
        const browser = new BrowserController();
        try {
          const url = BASE_URL + (args.page || '/');
          await browser.navigateTo(url);

          const selector: SelectorInfo = {
            type: args.selectorType,
            value: args.selectorValue,
          };

          const strategies = await browser.testWaitStrategies(selector);

          // Analyze results
          const workingStrategies = Object.entries(strategies)
            .filter(([_, result]) => result.found)
            .map(([name, result]) => ({ strategy: name, timeTaken: result.timeTaken }));

          const recommendation = workingStrategies.length > 0
            ? `Recommend: ${workingStrategies[0].strategy} (${workingStrategies[0].timeTaken}ms)`
            : 'No working strategy found - element may not exist';

          return {
            strategies,
            workingStrategies,
            recommendation,
            pageUrl: url,
          };
        } finally {
          await browser.quit();
        }
      },
    },

    {
      name: 'find-alternative-selectors',
      description: 'Find alternative selectors for an element if current one is failing',
      inputSchema: {
        type: 'object',
        properties: {
          selectorType: {
            type: 'string',
            enum: ['css', 'xpath', 'id', 'name', 'className'],
          },
          selectorValue: {
            type: 'string',
          },
          page: {
            type: 'string',
            default: '/',
          },
        },
        required: ['selectorType', 'selectorValue'],
      },
      handler: async (args: any) => {
        const browser = new BrowserController();
        try {
          const url = BASE_URL + (args.page || '/');
          await browser.navigateTo(url);

          const selector: SelectorInfo = {
            type: args.selectorType,
            value: args.selectorValue,
          };

          const alternatives = await browser.findAlternativeSelectors(selector);

          // Test each alternative
          const testedAlternatives = [];
          for (const alt of alternatives) {
            const result = await browser.validateElement(alt, 5000);
            testedAlternatives.push({
              selector: alt,
              works: result.found,
              timeTaken: result.timeTaken,
            });
          }

          return {
            original: selector,
            alternatives: testedAlternatives,
            bestAlternative: testedAlternatives.find(a => a.works),
            pageUrl: url,
          };
        } finally {
          await browser.quit();
        }
      },
    },

    {
      name: 'test-page-navigation',
      description: 'Navigate through a page flow and check for issues',
      inputSchema: {
        type: 'object',
        properties: {
          pages: {
            type: 'array',
            items: { type: 'string' },
            description: 'Array of page paths to navigate through',
          },
          takeScreenshots: {
            type: 'boolean',
            default: false,
          },
        },
        required: ['pages'],
      },
      handler: async (args: any) => {
        const browser = new BrowserController();
        const results = [];

        try {
          for (const page of args.pages) {
            const url = BASE_URL + page;
            const startTime = Date.now();

            try {
              await browser.navigateTo(url);
              const actualUrl = await browser.getCurrentUrl();
              const loadTime = Date.now() - startTime;

              let screenshot;
              if (args.takeScreenshots) {
                screenshot = await browser.takeScreenshot(`nav-${page.replace(/\//g, '_')}`);
              }

              results.push({
                page,
                url,
                actualUrl,
                success: true,
                loadTime,
                screenshot,
              });
            } catch (error) {
              results.push({
                page,
                url,
                success: false,
                error: error instanceof Error ? error.message : String(error),
                loadTime: Date.now() - startTime,
              });
            }
          }

          return {
            totalPages: args.pages.length,
            successful: results.filter(r => r.success).length,
            failed: results.filter(r => !r.success).length,
            results,
          };
        } finally {
          await browser.quit();
        }
      },
    },

    {
      name: 'validate-selector-json',
      description: 'Load and validate all selectors from a selector JSON file on the live website',
      inputSchema: {
        type: 'object',
        properties: {
          selectorFile: {
            type: 'string',
            description: 'Selector JSON file name (e.g., "homepage-selectors.json")',
          },
          page: {
            type: 'string',
            default: '/',
          },
        },
        required: ['selectorFile'],
      },
      handler: async (args: any) => {
        const browser = new BrowserController();
        try {
          const selectors = await reader.readSelectorFile(args.selectorFile);
          const url = BASE_URL + (args.page || '/');
          await browser.navigateTo(url);

          const results: any[] = [];

          for (const [name, selectorDef] of Object.entries(selectors)) {
            if (typeof selectorDef !== 'object' || !selectorDef) continue;

            const def = selectorDef as any;
            const selector: SelectorInfo = {
              type: def.type || 'css',
              value: def.selector || def.value,
            };

            const result = await browser.validateElement(selector, 5000);
            results.push({
              name,
              selector,
              ...result,
            });
          }

          const working = results.filter(r => r.found).length;
          const failing = results.filter(r => !r.found).length;

          return {
            selectorFile: args.selectorFile,
            pageUrl: url,
            total: results.length,
            working,
            failing,
            results,
          };
        } finally {
          await browser.quit();
        }
      },
    },
  ];
}
