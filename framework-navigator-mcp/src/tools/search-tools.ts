/**
 * Search Tools
 * Tools for searching across the framework
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

export function registerSearchTools(frameworkRoot: string, mode: string = 'full') {
  // Essential tools (basic mode) - most commonly used
  const basicTools = [
    {
      name: 'search-selectors',
      description: 'Search for selectors by name or pattern',
      inputSchema: {
        type: 'object',
        properties: {
          selectorName: {
            type: 'string',
            description: 'Selector name or pattern',
          },
          pageObject: {
            type: 'string',
            description: 'Filter by specific Page Object',
          },
        },
        required: ['selectorName'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);

        // Find selectors
        const selectors = index.findSelectors(args.selectorName);

        // Filter by page object if specified
        const filtered = args.pageObject
          ? selectors.filter(s => s.pageObject === args.pageObject)
          : selectors;

        if (filtered.length === 0) {
          return {
            found: false,
            message: `No selector found: ${args.selectorName}`,
          };
        }

        return {
          found: true,
          count: filtered.length,
          selectors: filtered.map(s => ({
            name: s.name,
            selector: s.selector,
            type: s.type,
            pageObject: s.pageObject,
            path: s.filePath.replace(frameworkRoot, ''),
            line: s.lineNumber,
          })),
        };
      },
    },

    {
      name: 'search-annotations',
      description: 'Search for methods with specific annotations',
      inputSchema: {
        type: 'object',
        properties: {
          annotation: {
            type: 'string',
            description: 'Annotation name (e.g., Test, DataProvider, BeforeMethod)',
          },
        },
        required: ['annotation'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        const results: any[] = [];

        allClasses.forEach(classInfo => {
          classInfo.classInfo.methods.forEach(method => {
            if (method.annotations.includes(args.annotation)) {
              results.push({
                class: classInfo.classInfo.className,
                method: method.name,
                path: classInfo.filePath.replace(frameworkRoot, ''),
                line: method.lineNumber,
                signature: `${method.returnType} ${method.name}(${method.parameters})`,
              });
            }
          });
        });

        return {
          found: results.length > 0,
          count: results.length,
          annotation: args.annotation,
          methods: results,
        };
      },
    },

    {
      name: 'list-selector-jsons',
      description: 'List all selector JSON files',
      inputSchema: {
        type: 'object',
        properties: {
          detailed: {
            type: 'boolean',
            default: false,
            description: 'Include selector counts',
          },
        },
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const jsonFiles = index.getAllSelectorJsons();

        if (args.detailed) {
          return {
            count: jsonFiles.length,
            files: jsonFiles.map(file => ({
              name: file,
              selectors: index.getSelectorJson(file)?.length || 0,
            })),
          };
        }

        return {
          count: jsonFiles.length,
          files: jsonFiles,
        };
      },
    },

    {
      name: 'get-selector-json',
      description: 'Get selectors from a specific JSON file',
      inputSchema: {
        type: 'object',
        properties: {
          fileName: {
            type: 'string',
            description: 'Selector JSON file name',
          },
        },
        required: ['fileName'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const selectors = index.getSelectorJson(args.fileName);

        if (!selectors) {
          return {
            found: false,
            message: `Selector JSON file not found: ${args.fileName}`,
          };
        }

        return {
          found: true,
          fileName: args.fileName,
          count: selectors.length,
          selectors: selectors.map(s => ({
            name: s.name,
            selector: s.selector,
            type: s.type,
          })),
        };
      },
    },
  ]; // End of basic tools

  // Advanced tools (full mode only) - rarely used, token-heavy
  const advancedTools = [
    {
      name: 'search-imports',
      description: 'Find classes that import a specific package or class',
      inputSchema: {
        type: 'object',
        properties: {
          importPattern: {
            type: 'string',
            description: 'Import pattern to search for (e.g., "org.openqa.selenium")',
          },
        },
        required: ['importPattern'],
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        const results: any[] = [];
        const pattern = new RegExp(args.importPattern, 'i');

        allClasses.forEach(classInfo => {
          const matchingImports = classInfo.classInfo.imports.filter(imp =>
            pattern.test(imp)
          );

          if (matchingImports.length > 0) {
            results.push({
              class: classInfo.classInfo.className,
              path: classInfo.filePath.replace(frameworkRoot, ''),
              imports: matchingImports,
            });
          }
        });

        return {
          found: results.length > 0,
          count: results.length,
          pattern: args.importPattern,
          classes: results,
        };
      },
    },

    {
      name: 'find-data-providers',
      description: 'Find all DataProvider methods in the framework',
      inputSchema: {
        type: 'object',
        properties: {
          providerName: {
            type: 'string',
            description: 'Filter by specific provider name',
          },
        },
      },
      handler: async (args: any) => {
        const index = await ensureIndexed(frameworkRoot);
        const allClasses = index.getAllClasses();

        const results: any[] = [];

        allClasses.forEach(classInfo => {
          classInfo.classInfo.methods.forEach(method => {
            if (method.annotations.includes('DataProvider')) {
              if (!args.providerName || method.name === args.providerName) {
                results.push({
                  name: method.name,
                  class: classInfo.classInfo.className,
                  path: classInfo.filePath.replace(frameworkRoot, ''),
                  line: method.lineNumber,
                  returnType: method.returnType,
                });
              }
            }
          });
        });

        return {
          found: results.length > 0,
          count: results.length,
          dataProviders: results,
        };
      },
    },

    {
      name: 'get-page-object-selectors',
      description: 'Get all selectors for a specific Page Object',
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
        const selectors = index.getSelectorsForPageObject(args.pageObject);

        if (selectors.length === 0) {
          return {
            found: false,
            message: `No selectors found for Page Object: ${args.pageObject}`,
          };
        }

        return {
          found: true,
          pageObject: args.pageObject,
          count: selectors.length,
          selectors: selectors.map(s => ({
            name: s.name,
            selector: s.selector,
            type: s.type,
            line: s.lineNumber,
          })),
        };
      },
    },
  ];

  // Return tools based on mode
  if (mode === 'basic') {
    return basicTools;
  } else {
    return [...basicTools, ...advancedTools];
  }
}
