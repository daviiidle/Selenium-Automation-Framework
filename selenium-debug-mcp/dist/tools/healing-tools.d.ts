/**
 * Self-Healing Tools for Claude to Use
 * Tools that enable AI-driven test healing workflow
 */
export declare function registerHealingTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testFilter: {
                type: string;
                description: string;
            };
            duration: {
                type: string;
                description: string;
                default: number;
            };
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        message: string;
        eventsCollected: number;
        events: any[];
        status: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            page: {
                type: string;
                description: string;
            };
            currentSelectorType: {
                type: string;
                enum: string[];
            };
            currentSelectorValue: {
                type: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        message: string;
        currentSelector: {
            type: any;
            value: any;
        };
        pageUrl: string;
        bestSelector?: undefined;
        recommendation?: undefined;
    } | {
        success: boolean;
        currentSelector: {
            type: any;
            value: any;
        };
        bestSelector: {
            type: "css" | "xpath" | "id" | "name" | "className" | "linkText" | "partialLinkText";
            value: string;
            score: number;
            stability: "high" | "medium" | "low";
            reason: string;
        };
        pageUrl: string;
        recommendation: string;
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorFile: {
                type: string;
                description: string;
            };
            selectorName: {
                type: string;
                description: string;
            };
            newType: {
                type: string;
                description: string;
            };
            newValue: {
                type: string;
                description: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<import("../healing/code-modifier.js").ModificationResult>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            pageClassName: {
                type: string;
                description: string;
            };
            selectorVariable: {
                type: string;
                description: string;
            };
            newType: {
                type: string;
                description: string;
            };
            newValue: {
                type: string;
                description: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<import("../healing/code-modifier.js").ModificationResult>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            javaFile: {
                type: string;
                description: string;
            };
            currentSeconds: {
                type: string;
            };
            newSeconds: {
                type: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<import("../healing/code-modifier.js").ModificationResult>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testClass: {
                type: string;
                description: string;
            };
            testMethod: {
                type: string;
                description: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        testAdded: string;
        queueSize: number;
        currentQueue: string[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testClass: {
                type: string;
                description?: undefined;
            };
            testMethod: {
                type: string;
                description?: undefined;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        status: string;
        testName: string;
        passed: boolean;
        duration: number;
        error?: string;
        output: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        testsRetried: number;
        passed: number;
        failed: number;
        queueBefore: number;
        queueAfter: number;
        results: import("../healing/retry-manager.js").RetryResult[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        queueSize: number;
        tests: string[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        success: boolean;
        clearedTests: number;
        queueSize: number;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
            backupFile?: undefined;
            targetFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        backupCount: number;
        backups: string[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            backupFile: {
                type: string;
                description: string;
            };
            targetFile: {
                type: string;
                description: string;
            };
            testFilter?: undefined;
            duration?: undefined;
            page?: undefined;
            currentSelectorType?: undefined;
            currentSelectorValue?: undefined;
            selectorFile?: undefined;
            selectorName?: undefined;
            newType?: undefined;
            newValue?: undefined;
            pageClassName?: undefined;
            selectorVariable?: undefined;
            javaFile?: undefined;
            currentSeconds?: undefined;
            newSeconds?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        backupFile: any;
        restoredTo: any;
    }>;
})[];
//# sourceMappingURL=healing-tools.d.ts.map