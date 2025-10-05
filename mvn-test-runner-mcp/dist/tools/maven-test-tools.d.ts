/**
 * Maven test execution tools for MCP server
 */
export declare function registerMavenTestTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            parallel: {
                type: string;
                description: string;
                default: boolean;
            };
            threadCount: {
                type: string;
                description: string;
                default: number;
            };
            timeout: {
                type: string;
                description: string;
                default: number;
            };
            testClass?: undefined;
            testMethod?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        success: boolean;
        summary: import("../types/test-results.js").TestSummary;
        failures: import("../types/test-results.js").TestFailure[];
        successes: string[];
        skipped: import("../types/test-results.js").TestSkipped[];
        fixSuggestions: import("../types/test-results.js").FixSuggestion[];
        patterns: string[];
    }>;
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
            timeout: {
                type: string;
                description: string;
                default: number;
            };
            parallel?: undefined;
            threadCount?: undefined;
            testMethod?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        testClass: any;
        summary: import("../types/test-results.js").TestSummary;
        failures: import("../types/test-results.js").TestFailure[];
        fixSuggestions: import("../types/test-results.js").FixSuggestion[];
        stdout: string;
        stderr: string;
        exitCode: number;
    }>;
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
            timeout: {
                type: string;
                description: string;
                default: number;
            };
            parallel?: undefined;
            threadCount?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        testClass: any;
        testMethod: any;
        summary: import("../types/test-results.js").TestSummary;
        failures: import("../types/test-results.js").TestFailure[];
        fixSuggestions: import("../types/test-results.js").FixSuggestion[];
        stdout: string;
        stderr: string;
        exitCode: number;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            parallel?: undefined;
            threadCount?: undefined;
            timeout?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        summary: import("../types/test-results.js").TestSummary;
        failuresByCategory: {
            [k: string]: {
                test: string;
                error: string;
            }[];
        };
        patterns: string[];
        criticalFixes: import("../types/test-results.js").FixSuggestion[];
    }>;
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
            parallel?: undefined;
            threadCount?: undefined;
            timeout?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        message: string;
        failure?: undefined;
        fixSuggestion?: undefined;
        relatedTools?: undefined;
    } | {
        failure: {
            testClass: string;
            testMethod: string;
            errorType: string;
            errorMessage: string;
            category: "ASSERTION" | "SELECTOR" | "TIMEOUT" | "CONFIGURATION" | "UNKNOWN";
            stackTrace: string;
        };
        fixSuggestion: import("../types/test-results.js").FixSuggestion;
        relatedTools: string[];
        success?: undefined;
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            parallel?: undefined;
            threadCount?: undefined;
            timeout?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        success: boolean;
        output: string;
        errors: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            parallel?: undefined;
            threadCount?: undefined;
            timeout?: undefined;
            testClass?: undefined;
            testMethod?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        totalFailures: number;
        patterns: string[];
        categoryBreakdown: {
            [k: string]: number;
        };
        recommendations: string[];
    }>;
})[];
//# sourceMappingURL=maven-test-tools.d.ts.map