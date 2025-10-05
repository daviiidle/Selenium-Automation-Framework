/**
 * Live Testing Tools
 * Real-time Selenium test execution and debugging
 */
import { SelectorInfo } from '../selenium/browser-controller.js';
export declare function registerLiveTestingTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorType: {
                type: string;
                enum: string[];
                description: string;
            };
            selectorValue: {
                type: string;
                description: string;
            };
            page: {
                type: string;
                description: string;
                default: string;
            };
            waitTime: {
                type: string;
                description: string;
                default: number;
            };
            pages?: undefined;
            takeScreenshots?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        success: boolean;
        result: import("../selenium/browser-controller.js").ElementValidationResult;
        pageUrl: string;
        suggestion: string;
    } | {
        success: boolean;
        result: import("../selenium/browser-controller.js").ElementValidationResult;
        pageUrl: string;
        suggestion?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorType: {
                type: string;
                enum: string[];
                description?: undefined;
            };
            selectorValue: {
                type: string;
                description?: undefined;
            };
            page: {
                type: string;
                default: string;
                description?: undefined;
            };
            waitTime?: undefined;
            pages?: undefined;
            takeScreenshots?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        strategies: Record<string, import("../selenium/browser-controller.js").ElementValidationResult>;
        workingStrategies: {
            strategy: string;
            timeTaken: number | undefined;
        }[];
        recommendation: string;
        pageUrl: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorType: {
                type: string;
                enum: string[];
                description?: undefined;
            };
            selectorValue: {
                type: string;
                description?: undefined;
            };
            page: {
                type: string;
                default: string;
                description?: undefined;
            };
            waitTime?: undefined;
            pages?: undefined;
            takeScreenshots?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        original: SelectorInfo;
        alternatives: {
            selector: SelectorInfo;
            works: boolean;
            timeTaken: number | undefined;
        }[];
        bestAlternative: {
            selector: SelectorInfo;
            works: boolean;
            timeTaken: number | undefined;
        } | undefined;
        pageUrl: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            pages: {
                type: string;
                items: {
                    type: string;
                };
                description: string;
            };
            takeScreenshots: {
                type: string;
                default: boolean;
            };
            selectorType?: undefined;
            selectorValue?: undefined;
            page?: undefined;
            waitTime?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        totalPages: any;
        successful: number;
        failed: number;
        results: ({
            page: any;
            url: string;
            actualUrl: string;
            success: boolean;
            loadTime: number;
            screenshot: string | undefined;
            error?: undefined;
        } | {
            page: any;
            url: string;
            success: boolean;
            error: string;
            loadTime: number;
            actualUrl?: undefined;
            screenshot?: undefined;
        })[];
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
            page: {
                type: string;
                default: string;
                description?: undefined;
            };
            selectorType?: undefined;
            selectorValue?: undefined;
            waitTime?: undefined;
            pages?: undefined;
            takeScreenshots?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        selectorFile: any;
        pageUrl: string;
        total: number;
        working: number;
        failing: number;
        results: any[];
    }>;
})[];
//# sourceMappingURL=live-testing.d.ts.map