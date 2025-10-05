/**
 * Static Analysis Tools
 * Analyze test results, logs, and configuration without running tests
 */
export declare function registerStaticAnalysisTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed: {
                type: string;
                default: boolean;
                description: string;
            };
            configFile?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<any>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed?: undefined;
            configFile?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        total: number;
        passed: number;
        failed: number;
        skipped: number;
        passRate: number;
        totalTime: number;
    } | {
        error: string;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed?: undefined;
            configFile?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        totalPatterns: number;
        categorized: {
            selector: import("../analyzers/testng-parser.js").FailurePattern[];
            timeout: import("../analyzers/testng-parser.js").FailurePattern[];
            staleElement: import("../analyzers/testng-parser.js").FailurePattern[];
            network: import("../analyzers/testng-parser.js").FailurePattern[];
            assertion: import("../analyzers/testng-parser.js").FailurePattern[];
            unknown: import("../analyzers/testng-parser.js").FailurePattern[];
        };
        topPatterns: import("../analyzers/testng-parser.js").FailurePattern[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            configFile: {
                type: string;
                default: string;
            };
            detailed?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        configFile: any;
        issues: {
            type: string;
            message: string;
            suggestion: string;
        }[];
        recommendations: ({
            type: string;
            message: string;
            suggestion?: undefined;
        } | {
            type: string;
            message: string;
            suggestion: string;
        })[];
        config: string;
        error?: undefined;
    } | {
        error: string;
        configFile?: undefined;
        issues?: undefined;
        recommendations?: undefined;
        config?: undefined;
    }>;
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
            detailed?: undefined;
            configFile?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        className: any;
        selectorsFound: number;
        selectors: import("../utils/framework-reader.js").SelectorDefinition[];
        codePreview: string;
        error?: undefined;
    } | {
        error: string;
        className?: undefined;
        selectorsFound?: undefined;
        selectors?: undefined;
        codePreview?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testClassName: {
                type: string;
                description: string;
            };
            detailed?: undefined;
            configFile?: undefined;
            pageClassName?: undefined;
            selectorFile?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        className: any;
        testsFound: number;
        testMethods: import("../utils/framework-reader.js").TestInfo[];
        codePreview: string;
        error?: undefined;
    } | {
        error: string;
        className?: undefined;
        testsFound?: undefined;
        testMethods?: undefined;
        codePreview?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed?: undefined;
            configFile?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
            selectorFile?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        selectorFiles: string[];
        count: number;
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
            detailed?: undefined;
            configFile?: undefined;
            pageClassName?: undefined;
            testClassName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        file: any;
        selectors: Record<string, any>;
        count: number;
        error?: undefined;
    } | {
        error: string;
        file?: undefined;
        selectors?: undefined;
        count?: undefined;
    }>;
})[];
//# sourceMappingURL=static-analysis.d.ts.map