/**
 * Relationship Tools
 * Tools for mapping relationships between framework components
 */

import { FrameworkIndex } from '../indexer/FrameworkIndex.js';
import { JavaIndexer } from '../indexer/JavaIndexer.js';
import { SelectorIndexer } from '../indexer/SelectorIndexer.js';

// Singleton index
let frameworkIndex: FrameworkIndex | null = null;

async function ensureIndexed(frameworkRoot: string): Promise<FrameworkIndex> {
  if (!frameworkIndex || !frameworkIndex.isIndexed()) {
    frameworkIndex = new FrameworkIndex();

    const javaIndexer = new JavaIndexer(frameworkRoot, frameworkIndex);
    await javaIndexer.indexAll();

    const selectorIndexer = new SelectorIndexer(frameworkRoot, frameworkIndex);
    await selectorIndexer.indexAll();

    frameworkIndex.markIndexed();
  }

  return frameworkIndex;
}

export function registerRelationshipTools(frameworkRoot: string) {
  return [
    {
      name: 'get-test-dependencies',
      description: 'Get Page Objects and DataProviders used by a test class',
      inputSchema: {
        type: 'object',
        properties: {
          testClass: {
            type: 'string',
            description: 'Test class name',
          },
        },
        required: ['testClass'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const dependencies = index.getTestDependencies(args.testClass);

        if (!dependencies) {
          return {
            found: false,
            message: `Test class not found: ${args.testClass}`,
          };
        }

        return {
          found: true,
          testClass: args.testClass,
          dependencies: {
            pageObjects: dependencies.pageObjects,
            dataProviders: dependencies.dataProviders,
          },
        };
      },
    },

    {
      name: 'get-selector-usage',
      description: 'Find which Page Object uses a specific selector',
      inputSchema: {
        type: 'object',
        properties: {
          selectorName: {
            type: 'string',
            description: 'Selector variable name',
          },
        },
        required: ['selectorName'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const selectors = index.findSelectors(args.selectorName);

        if (selectors.length === 0) {
          return {
            found: false,
            message: `Selector not found: ${args.selectorName}`,
          };
        }

        return {
          found: true,
          selectorName: args.selectorName,
          usage: selectors.map(s => ({
            pageObject: s.pageObject,
            selector: s.selector,
            type: s.type,
            path: s.filePath.replace(frameworkRoot, ''),
            line: s.lineNumber,
          })),
        };
      },
    },

    {
      name: 'map-test-to-selectors',
      description: 'Map complete chain: Test → Page Objects → Selectors',
      inputSchema: {
        type: 'object',
        properties: {
          testClass: {
            type: 'string',
            description: 'Test class name',
          },
        },
        required: ['testClass'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const dependencies = index.getTestDependencies(args.testClass);

        if (!dependencies) {
          return {
            found: false,
            message: `Test class not found: ${args.testClass}`,
          };
        }

        // For each Page Object, get its selectors
        const pageObjectDetails = dependencies.pageObjects.map(po => {
          const selectors = index.getSelectorsForPageObject(po);
          return {
            pageObject: po,
            selectorCount: selectors.length,
            selectors: selectors.map(s => ({
              name: s.name,
              selector: s.selector,
              type: s.type,
            })),
          };
        });

        return {
          found: true,
          testClass: args.testClass,
          chain: {
            pageObjects: pageObjectDetails,
            totalSelectors: pageObjectDetails.reduce((sum, po) => sum + po.selectorCount, 0),
          },
        };
      },
    },

    {
      name: 'find-page-object-usage',
      description: 'Find which tests use a specific Page Object',
      inputSchema: {
        type: 'object',
        properties: {
          pageObject: {
            type: 'string',
            description: 'Page Object class name',
          },
        },
        required: ['pageObject'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const testClasses = index.getTestClasses();

        const usages: any[] = [];

        testClasses.forEach(tc => {
          const deps = index.getTestDependencies(tc.classInfo.className);
          if (deps && deps.pageObjects.includes(args.pageObject)) {
            usages.push({
              testClass: tc.classInfo.className,
              path: tc.filePath.replace(frameworkRoot, ''),
            });
          }
        });

        if (usages.length === 0) {
          return {
            found: false,
            message: `No tests use Page Object: ${args.pageObject}`,
          };
        }

        return {
          found: true,
          pageObject: args.pageObject,
          usedBy: usages,
          count: usages.length,
        };
      },
    },

    {
      name: 'get-class-hierarchy',
      description: 'Get inheritance hierarchy for a class',
      inputSchema: {
        type: 'object',
        properties: {
          className: {
            type: 'string',
            description: 'Class name',
          },
        },
        required: ['className'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const classInfo = index.getClass(args.className);

        if (!classInfo) {
          return {
            found: false,
            message: `Class not found: ${args.className}`,
          };
        }

        // Build hierarchy
        const hierarchy: string[] = [classInfo.classInfo.className];
        let current = classInfo.classInfo.extendsClass;

        while (current) {
          hierarchy.push(current);
          const parentClass = index.getClass(current);
          if (!parentClass) break;
          current = parentClass.classInfo.extendsClass;
        }

        return {
          found: true,
          className: args.className,
          hierarchy,
          implements: classInfo.classInfo.implementsInterfaces,
        };
      },
    },

    {
      name: 'find-subclasses',
      description: 'Find all classes that extend a specific class',
      inputSchema: {
        type: 'object',
        properties: {
          baseClass: {
            type: 'string',
            description: 'Base class name',
          },
        },
        required: ['baseClass'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        const subclasses = allClasses.filter(c =>
          c.classInfo.extendsClass === args.baseClass
        );

        return {
          found: subclasses.length > 0,
          baseClass: args.baseClass,
          count: subclasses.length,
          subclasses: subclasses.map(c => ({
            name: c.classInfo.className,
            path: c.filePath.replace(frameworkRoot, ''),
            category: c.category,
          })),
        };
      },
    },

    {
      name: 'get-package-classes',
      description: 'Get all classes in a specific package',
      inputSchema: {
        type: 'object',
        properties: {
          packageName: {
            type: 'string',
            description: 'Package name (e.g., "com.demowebshop.automation.pages")',
          },
        },
        required: ['packageName'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        const packageClasses = allClasses.filter(c =>
          c.classInfo.packageName === args.packageName ||
          c.classInfo.packageName.startsWith(args.packageName + '.')
        );

        return {
          found: packageClasses.length > 0,
          package: args.packageName,
          count: packageClasses.length,
          classes: packageClasses.map(c => ({
            name: c.classInfo.className,
            fullPackage: c.classInfo.packageName,
            path: c.filePath.replace(frameworkRoot, ''),
            category: c.category,
          })),
        };
      },
    },
  ];
}
