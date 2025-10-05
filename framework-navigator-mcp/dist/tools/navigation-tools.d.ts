/**
 * Navigation Tools
 * Tools for navigating framework structure
 */
export declare function registerNavigationTools(frameworkRoot: string): ({
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
            category?: undefined;
            className?: undefined;
            methodName?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        count: number;
        pageObjects: {
            name: string;
            path: string;
        }[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            category: {
                type: string;
                description: string;
                enum?: undefined;
            };
            detailed?: undefined;
            className?: undefined;
            methodName?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        category: any;
        count: number;
        tests: any[];
        totalTests?: undefined;
        categories?: undefined;
    } | {
        totalTests: number;
        categories: string[];
        tests: Record<string, any[]>;
        category?: undefined;
        count?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            className: {
                type: string;
                description: string;
            };
            detailed?: undefined;
            category?: undefined;
            methodName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        class: {
            name: string;
            path: string;
            package: string;
            category: "page-object" | "test" | "util" | "base" | "other";
            extends: string | undefined;
            methods: string[];
            lineCount: number;
        };
        message?: undefined;
        matchCount?: undefined;
        matches?: undefined;
    } | {
        found: boolean;
        message: string;
        class?: undefined;
        matchCount?: undefined;
        matches?: undefined;
    } | {
        found: boolean;
        matchCount: number;
        matches: {
            name: string;
            path: string;
            category: "page-object" | "test" | "util" | "base" | "other";
        }[];
        class?: undefined;
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            methodName: {
                type: string;
                description: string;
            };
            detailed?: undefined;
            category?: undefined;
            className?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        matchCount?: undefined;
        methods?: undefined;
    } | {
        found: boolean;
        matchCount: number;
        methods: {
            name: string;
            class: string;
            path: string;
            lineNumber: number;
            signature: string;
            annotations: string[];
        }[];
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            className: {
                type: string;
                description: string;
            };
            detailed?: undefined;
            category?: undefined;
            methodName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        class?: undefined;
    } | {
        found: boolean;
        class: {
            name: string;
            package: string;
            path: string;
            category: "page-object" | "test" | "util" | "base" | "other";
            extends: string | undefined;
            implements: string[];
            annotations: string[];
            isAbstract: boolean;
            lineCount: number;
            methods: {
                name: string;
                returnType: string;
                parameters: string;
                line: number;
                annotations: string[];
                isPublic: boolean;
                isStatic: boolean;
            }[];
            fields: {
                name: string;
                type: string;
                line: number;
                isPrivate: boolean;
                isStatic: boolean;
            }[];
        };
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed?: undefined;
            category?: undefined;
            className?: undefined;
            methodName?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        totalClasses: number;
        pageObjects: number;
        testClasses: number;
        baseClasses: number;
        totalMethods: number;
        totalSelectors: number;
        selectorJsonFiles: number;
        indexed: boolean;
        indexTime: string | undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            category: {
                type: string;
                enum: string[];
                description: string;
            };
            detailed?: undefined;
            className?: undefined;
            methodName?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        totalClasses: number;
        byCategory: Record<string, any[]>;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            detailed?: undefined;
            category?: undefined;
            className?: undefined;
            methodName?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        success: boolean;
        stats: {
            totalClasses: number;
            pageObjects: number;
            testClasses: number;
            baseClasses: number;
            totalMethods: number;
            totalSelectors: number;
            selectorJsonFiles: number;
            indexed: boolean;
            indexTime: string | undefined;
        };
    }>;
})[];
//# sourceMappingURL=navigation-tools.d.ts.map