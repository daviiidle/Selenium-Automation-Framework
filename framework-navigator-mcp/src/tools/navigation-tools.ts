/**
 * Navigation Tools
 * Tools for navigating framework structure
 */

import { FrameworkIndex } from '../indexer/FrameworkIndex.js';
import { JavaIndexer } from '../indexer/JavaIndexer.js';
import { SelectorIndexer } from '../indexer/SelectorIndexer.js';

// Singleton index
let frameworkIndex: FrameworkIndex | null = null;

async function ensureIndexed(frameworkRoot: string): Promise<FrameworkIndex> {
  if (!frameworkIndex || !frameworkIndex.isIndexed()) {
    frameworkIndex = new FrameworkIndex();

    // Index Java files
    const javaIndexer = new JavaIndexer(frameworkRoot, frameworkIndex);
    await javaIndexer.indexAll();

    // Index selector JSONs
    const selectorIndexer = new SelectorIndexer(frameworkRoot, frameworkIndex);
    await selectorIndexer.indexAll();

    frameworkIndex.markIndexed();
  }

  return frameworkIndex;
}

export function registerNavigationTools(frameworkRoot: string) {
  return [
    {
      name: 'list-page-objects',
      description: 'List all Page Object classes with file paths',
      inputSchema: {
        type: 'object',
        properties: {
          detailed: {
            type: 'boolean',
            default: false,
            description: 'Include method counts and extends info',
          },
        },
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const pageObjects = index.getPageObjects();

        if (args.detailed) {
          return {
            count: pageObjects.length,
            pageObjects: pageObjects.map(po => ({
              name: po.classInfo.className,
              path: po.filePath.replace(frameworkRoot, ''),
              extends: po.classInfo.extendsClass,
              methods: po.classInfo.methods.length,
              selectors: index.getSelectorsForPageObject(po.classInfo.className).length,
            })),
          };
        } else {
          return {
            count: pageObjects.length,
            pageObjects: pageObjects.map(po => ({
              name: po.classInfo.className,
              path: po.filePath.replace(frameworkRoot, ''),
            })),
          };
        }
      },
    },

    {
      name: 'list-test-classes',
      description: 'List all test classes organized by category',
      inputSchema: {
        type: 'object',
        properties: {
          category: {
            type: 'string',
            description: 'Filter by category (authentication, cart, checkout, products, etc.)',
          },
        },
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const testClasses = index.getTestClasses();

        // Organize by category (extract from file path)
        const categorized: Record<string, any[]> = {};

        testClasses.forEach(tc => {
          const pathMatch = tc.filePath.match(/tests[/\\](\w+)[/\\]/);
          const category = pathMatch ? pathMatch[1] : 'other';

          if (!categorized[category]) {
            categorized[category] = [];
          }

          categorized[category].push({
            name: tc.classInfo.className,
            path: tc.filePath.replace(frameworkRoot, ''),
            testMethods: tc.classInfo.methods.filter(m => m.annotations.includes('Test')).length,
          });
        });

        // Filter if category specified
        if (args.category) {
          const filtered = categorized[args.category] || [];
          return {
            category: args.category,
            count: filtered.length,
            tests: filtered,
          };
        }

        return {
          totalTests: testClasses.length,
          categories: Object.keys(categorized).sort(),
          tests: categorized,
        };
      },
    },

    {
      name: 'find-class',
      description: 'Find class by name or pattern',
      inputSchema: {
        type: 'object',
        properties: {
          className: {
            type: 'string',
            description: 'Class name or regex pattern',
          },
        },
        required: ['className'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);

        // Try exact match first
        const exactMatch = index.getClass(args.className);
        if (exactMatch) {
          return {
            found: true,
            class: {
              name: exactMatch.classInfo.className,
              path: exactMatch.filePath.replace(frameworkRoot, ''),
              package: exactMatch.classInfo.packageName,
              category: exactMatch.category,
              extends: exactMatch.classInfo.extendsClass,
              methods: exactMatch.classInfo.methods.map(m => `${m.name}(${m.parameters})`),
              lineCount: exactMatch.classInfo.lineCount,
            },
          };
        }

        // Try pattern match
        const matches = index.findClasses(args.className);
        if (matches.length === 0) {
          return {
            found: false,
            message: `No class found matching: ${args.className}`,
          };
        }

        return {
          found: true,
          matchCount: matches.length,
          matches: matches.map(m => ({
            name: m.classInfo.className,
            path: m.filePath.replace(frameworkRoot, ''),
            category: m.category,
          })),
        };
      },
    },

    {
      name: 'find-method',
      description: 'Find method by name across all classes',
      inputSchema: {
        type: 'object',
        properties: {
          methodName: {
            type: 'string',
            description: 'Method name or regex pattern',
          },
        },
        required: ['methodName'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);

        // Try exact match
        const exactMatches = index.findMethods(args.methodName);

        if (exactMatches.length === 0) {
          // Try pattern match
          const patternMatches = index.findMethodsByPattern(args.methodName);

          if (patternMatches.length === 0) {
            return {
              found: false,
              message: `No method found matching: ${args.methodName}`,
            };
          }

          return {
            found: true,
            matchCount: patternMatches.length,
            methods: patternMatches.map(m => ({
              name: m.methodInfo.name,
              class: m.className,
              path: m.filePath.replace(frameworkRoot, ''),
              lineNumber: m.methodInfo.lineNumber,
              signature: `${m.methodInfo.returnType} ${m.methodInfo.name}(${m.methodInfo.parameters})`,
              annotations: m.methodInfo.annotations,
            })),
          };
        }

        return {
          found: true,
          matchCount: exactMatches.length,
          methods: exactMatches.map(m => ({
            name: m.methodInfo.name,
            class: m.className,
            path: m.filePath.replace(frameworkRoot, ''),
            lineNumber: m.methodInfo.lineNumber,
            signature: `${m.methodInfo.returnType} ${m.methodInfo.name}(${m.methodInfo.parameters})`,
            annotations: m.methodInfo.annotations,
          })),
        };
      },
    },

    {
      name: 'get-class-info',
      description: 'Get detailed class information without reading full file',
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

        return {
          found: true,
          class: {
            name: classInfo.classInfo.className,
            package: classInfo.classInfo.packageName,
            path: classInfo.filePath.replace(frameworkRoot, ''),
            category: classInfo.category,
            extends: classInfo.classInfo.extendsClass,
            implements: classInfo.classInfo.implementsInterfaces,
            annotations: classInfo.classInfo.annotations,
            isAbstract: classInfo.classInfo.isAbstract,
            lineCount: classInfo.classInfo.lineCount,
            methods: classInfo.classInfo.methods.map(m => ({
              name: m.name,
              returnType: m.returnType,
              parameters: m.parameters,
              line: m.lineNumber,
              annotations: m.annotations,
              isPublic: m.isPublic,
              isStatic: m.isStatic,
            })),
            fields: classInfo.classInfo.fields.map(f => ({
              name: f.name,
              type: f.type,
              line: f.lineNumber,
              isPrivate: f.isPrivate,
              isStatic: f.isStatic,
            })),
          },
        };
      },
    },

    {
      name: 'get-framework-stats',
      description: 'Get framework statistics and overview',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        const index = await ensureIndexed(frameworkRoot);
        return index.getStats();
      },
    },

    {
      name: 'list-all-classes',
      description: 'List all classes in the framework by category',
      inputSchema: {
        type: 'object',
        properties: {
          category: {
            type: 'string',
            enum: ['page-object', 'test', 'util', 'base', 'other'],
            description: 'Filter by category',
          },
        },
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        // Filter by category if specified
        const filtered = args.category
          ? allClasses.filter(c => c.category === args.category)
          : allClasses;

        // Organize by category
        const organized: Record<string, any[]> = {};

        filtered.forEach(c => {
          if (!organized[c.category]) {
            organized[c.category] = [];
          }
          organized[c.category].push({
            name: c.classInfo.className,
            path: c.filePath.replace(frameworkRoot, ''),
            package: c.classInfo.packageName,
          });
        });

        return {
          totalClasses: filtered.length,
          byCategory: organized,
        };
      },
    },

    {
      name: 'refresh-index',
      description: 'Refresh the framework index (reindex all files)',
      inputSchema: {
        type: 'object',
        properties: {},
      },
      handler: async () => {
        if (frameworkIndex) {
          frameworkIndex.clear();
        }
        frameworkIndex = null;

        const index = await ensureIndexed(frameworkRoot);
        return {
          success: true,
          stats: index.getStats(),
        };
      },
    },
  ];
}
