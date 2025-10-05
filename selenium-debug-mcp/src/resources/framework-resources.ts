/**
 * Framework Resources
 * Expose framework files and data as MCP resources
 */

import { FrameworkReader } from '../utils/framework-reader.js';
import { TestNGParser } from '../analyzers/testng-parser.js';
import { promises as fs } from 'fs';
import path from 'path';

export function registerFrameworkResources(frameworkRoot: string) {
  const reader = new FrameworkReader(frameworkRoot);
  const testngParser = new TestNGParser(frameworkRoot);

  return [
    {
      uri: 'selenium://framework/pom.xml',
      name: 'pom.xml',
      description: 'Maven project configuration',
      mimeType: 'application/xml',
      handler: async () => {
        return await reader.readPomXml();
      },
    },

    {
      uri: 'selenium://framework/testng-config',
      name: 'TestNG Configuration',
      description: 'TestNG XML configuration file',
      mimeType: 'application/xml',
      handler: async () => {
        return await reader.readTestNGConfig();
      },
    },

    {
      uri: 'selenium://framework/test-results',
      name: 'Test Results',
      description: 'Latest test execution results',
      mimeType: 'application/json',
      handler: async () => {
        const hasResults = await testngParser.hasTestResults();
        if (!hasResults) {
          return JSON.stringify({ error: 'No test results available' });
        }

        const summary = await testngParser.getSummary();
        const failedTests = await testngParser.getFailedTests();
        const patterns = await testngParser.analyzeFailurePatterns();

        return JSON.stringify({
          summary,
          failedTests: failedTests.slice(0, 10), // Limit to 10 most recent
          failurePatterns: patterns,
        }, null, 2);
      },
    },

    {
      uri: 'selenium://framework/selectors',
      name: 'All Selectors',
      description: 'All selector JSON files combined',
      mimeType: 'application/json',
      handler: async () => {
        const files = await reader.getAllSelectorFiles();
        const allSelectors: Record<string, any> = {};

        for (const file of files) {
          try {
            const selectors = await reader.readSelectorFile(file);
            allSelectors[file] = selectors;
          } catch {
            // Skip files that can't be read
          }
        }

        return JSON.stringify(allSelectors, null, 2);
      },
    },

    {
      uri: 'selenium://framework/base-test',
      name: 'BaseTest Configuration',
      description: 'Base test class configuration and setup',
      mimeType: 'application/json',
      handler: async () => {
        const config = await reader.getBaseTestConfig();
        return JSON.stringify(config, null, 2);
      },
    },
  ];
}
