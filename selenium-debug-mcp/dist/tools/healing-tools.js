/**
 * Self-Healing Tools for Claude to Use
 * Tools that enable AI-driven test healing workflow
 */
import { TestWatcher } from '../watchers/test-watcher.js';
import { BrowserController } from '../selenium/browser-controller.js';
import { SelectorFinder } from '../healing/selector-finder.js';
import { CodeModifier } from '../healing/code-modifier.js';
import { RetryManager } from '../healing/retry-manager.js';
import { FrameworkReader } from '../utils/framework-reader.js';
const BASE_URL = process.env.BASE_URL || 'https://demowebshop.tricentis.com';
// Shared instances for persistent state across tool calls
let retryManager = null;
let testWatcher = null;
export function registerHealingTools(frameworkRoot) {
    const codeModifier = new CodeModifier(frameworkRoot);
    const reader = new FrameworkReader(frameworkRoot);
    // Initialize retry manager
    if (!retryManager) {
        retryManager = new RetryManager(frameworkRoot);
    }
    return [
        {
            name: 'stream-test-execution',
            description: 'Stream mvn test execution in real-time and emit events for failures (non-blocking)',
            inputSchema: {
                type: 'object',
                properties: {
                    testFilter: {
                        type: 'string',
                        description: 'Optional test filter (e.g., "LoginTest" or "LoginTest#testValidLogin")',
                    },
                    duration: {
                        type: 'number',
                        description: 'How long to monitor in seconds (0 = until completion)',
                        default: 0,
                    },
                },
            },
            handler: async (args) => {
                // Stop existing watcher if any
                if (testWatcher) {
                    testWatcher.stop();
                }
                testWatcher = new TestWatcher(frameworkRoot);
                const events = [];
                // Collect events
                testWatcher.on('test-start', (data) => {
                    events.push({ type: 'test-start', ...data, timestamp: new Date() });
                });
                testWatcher.on('test-pass', (data) => {
                    events.push({ type: 'test-pass', ...data, timestamp: new Date() });
                });
                testWatcher.on('test-fail', (data) => {
                    events.push({ type: 'test-fail', ...data, timestamp: new Date() });
                });
                testWatcher.on('selector-not-found', (data) => {
                    events.push({ type: 'selector-not-found', ...data, timestamp: new Date() });
                });
                testWatcher.on('timeout-error', (data) => {
                    events.push({ type: 'timeout-error', ...data, timestamp: new Date() });
                });
                // Start watching
                await testWatcher.startWatching(args.testFilter);
                // Wait for specified duration or completion
                if (args.duration > 0) {
                    await new Promise(resolve => setTimeout(resolve, args.duration * 1000));
                }
                else {
                    await testWatcher.waitForCompletion();
                }
                return {
                    message: 'Test execution monitoring started',
                    eventsCollected: events.length,
                    events: events.slice(0, 20), // Return first 20 events
                    status: 'completed',
                };
            },
        },
        {
            name: 'find-working-selector',
            description: 'Navigate to page and find a working selector to replace failing one',
            inputSchema: {
                type: 'object',
                properties: {
                    page: {
                        type: 'string',
                        description: 'Page path (e.g., "/login", "/register")',
                    },
                    currentSelectorType: {
                        type: 'string',
                        enum: ['css', 'xpath', 'id', 'name', 'className'],
                    },
                    currentSelectorValue: {
                        type: 'string',
                    },
                },
                required: ['page', 'currentSelectorType', 'currentSelectorValue'],
            },
            handler: async (args) => {
                const browser = new BrowserController();
                const finder = new SelectorFinder(browser);
                try {
                    const url = BASE_URL + args.page;
                    const currentSelector = {
                        type: args.currentSelectorType,
                        value: args.currentSelectorValue,
                    };
                    const bestSelector = await finder.findBestSelector(currentSelector, url);
                    if (!bestSelector) {
                        return {
                            success: false,
                            message: 'No working selector found',
                            currentSelector,
                            pageUrl: url,
                        };
                    }
                    return {
                        success: true,
                        currentSelector,
                        bestSelector: {
                            type: bestSelector.selector.type,
                            value: bestSelector.selector.value,
                            score: bestSelector.score,
                            stability: bestSelector.stability,
                            reason: bestSelector.reason,
                        },
                        pageUrl: url,
                        recommendation: `Replace with: ${bestSelector.selector.type} = "${bestSelector.selector.value}"`,
                    };
                }
                finally {
                    await browser.quit();
                }
            },
        },
        {
            name: 'update-selector-file',
            description: 'Update a selector in a JSON selector file',
            inputSchema: {
                type: 'object',
                properties: {
                    selectorFile: {
                        type: 'string',
                        description: 'Selector JSON file name (e.g., "login-selectors.json")',
                    },
                    selectorName: {
                        type: 'string',
                        description: 'Name/key of the selector to update',
                    },
                    newType: {
                        type: 'string',
                        description: 'New selector type (css, xpath, id, etc.)',
                    },
                    newValue: {
                        type: 'string',
                        description: 'New selector value',
                    },
                },
                required: ['selectorFile', 'selectorName', 'newType', 'newValue'],
            },
            handler: async (args) => {
                const result = await codeModifier.updateSelectorJson(args.selectorFile, args.selectorName, { type: args.newType, value: args.newValue });
                return result;
            },
        },
        {
            name: 'update-page-object-selector',
            description: 'Update a selector in a Java Page Object class',
            inputSchema: {
                type: 'object',
                properties: {
                    pageClassName: {
                        type: 'string',
                        description: 'Page Object class name (e.g., "HomePage", "LoginPage")',
                    },
                    selectorVariable: {
                        type: 'string',
                        description: 'Variable name in Java (e.g., "emailInput", "submitButton")',
                    },
                    newType: {
                        type: 'string',
                        description: 'Selenium By method (cssSelector, id, name, xpath)',
                    },
                    newValue: {
                        type: 'string',
                        description: 'New selector value',
                    },
                },
                required: ['pageClassName', 'selectorVariable', 'newType', 'newValue'],
            },
            handler: async (args) => {
                const result = await codeModifier.updatePageObjectSelector(args.pageClassName, args.selectorVariable, args.newType, args.newValue);
                return result;
            },
        },
        {
            name: 'update-wait-time',
            description: 'Update wait time in Java code',
            inputSchema: {
                type: 'object',
                properties: {
                    javaFile: {
                        type: 'string',
                        description: 'Java file name without extension (e.g., "BaseTest", "LoginPage")',
                    },
                    currentSeconds: {
                        type: 'number',
                    },
                    newSeconds: {
                        type: 'number',
                    },
                },
                required: ['javaFile', 'currentSeconds', 'newSeconds'],
            },
            handler: async (args) => {
                const result = await codeModifier.updateWaitTime(args.javaFile, args.currentSeconds, args.newSeconds);
                return result;
            },
        },
        {
            name: 'add-to-retry-queue',
            description: 'Add a failed test to the retry queue for later re-execution',
            inputSchema: {
                type: 'object',
                properties: {
                    testClass: {
                        type: 'string',
                        description: 'Test class name (e.g., "LoginTest")',
                    },
                    testMethod: {
                        type: 'string',
                        description: 'Test method name (e.g., "testValidLogin")',
                    },
                },
                required: ['testClass', 'testMethod'],
            },
            handler: async (args) => {
                retryManager.addToRetryQueue(args.testClass, args.testMethod);
                return {
                    success: true,
                    testAdded: `${args.testClass}#${args.testMethod}`,
                    queueSize: retryManager.getStats().queueSize,
                    currentQueue: retryManager.getStats().tests,
                };
            },
        },
        {
            name: 'retry-single-test',
            description: 'Retry a single specific test after applying fixes',
            inputSchema: {
                type: 'object',
                properties: {
                    testClass: {
                        type: 'string',
                    },
                    testMethod: {
                        type: 'string',
                    },
                },
                required: ['testClass', 'testMethod'],
            },
            handler: async (args) => {
                const result = await retryManager.retrySingleTest(args.testClass, args.testMethod);
                return {
                    ...result,
                    status: result.passed ? 'PASS' : 'FAIL',
                };
            },
        },
        {
            name: 'retry-all-queued',
            description: 'Retry all tests in the retry queue',
            inputSchema: {
                type: 'object',
                properties: {},
            },
            handler: async () => {
                const queueBefore = retryManager.getStats();
                const results = await retryManager.retryAllQueued();
                const queueAfter = retryManager.getStats();
                const passed = results.filter(r => r.passed).length;
                const failed = results.filter(r => !r.passed).length;
                return {
                    testsRetried: results.length,
                    passed,
                    failed,
                    queueBefore: queueBefore.queueSize,
                    queueAfter: queueAfter.queueSize,
                    results,
                };
            },
        },
        {
            name: 'get-retry-queue',
            description: 'Get current retry queue status',
            inputSchema: {
                type: 'object',
                properties: {},
            },
            handler: async () => {
                return retryManager.getStats();
            },
        },
        {
            name: 'clear-retry-queue',
            description: 'Clear the retry queue',
            inputSchema: {
                type: 'object',
                properties: {},
            },
            handler: async () => {
                const beforeSize = retryManager.getStats().queueSize;
                retryManager.clearRetryQueue();
                return {
                    success: true,
                    clearedTests: beforeSize,
                    queueSize: 0,
                };
            },
        },
        {
            name: 'list-backups',
            description: 'List all backup files created during healing',
            inputSchema: {
                type: 'object',
                properties: {},
            },
            handler: async () => {
                const backups = await codeModifier.listBackups();
                return {
                    backupCount: backups.length,
                    backups: backups.slice(0, 20), // Most recent 20
                };
            },
        },
        {
            name: 'restore-backup',
            description: 'Restore a file from backup',
            inputSchema: {
                type: 'object',
                properties: {
                    backupFile: {
                        type: 'string',
                        description: 'Backup file name',
                    },
                    targetFile: {
                        type: 'string',
                        description: 'Target file path to restore to',
                    },
                },
                required: ['backupFile', 'targetFile'],
            },
            handler: async (args) => {
                const backupPath = `${frameworkRoot}/.mcp-backups/${args.backupFile}`;
                const success = await codeModifier.restoreFromBackup(backupPath, args.targetFile);
                return {
                    success,
                    backupFile: args.backupFile,
                    restoredTo: args.targetFile,
                };
            },
        },
    ];
}
//# sourceMappingURL=healing-tools.js.map