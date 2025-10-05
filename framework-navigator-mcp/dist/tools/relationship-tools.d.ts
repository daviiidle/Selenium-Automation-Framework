/**
 * Relationship Tools
 * Tools for mapping relationships between framework components
 */
export declare function registerRelationshipTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testClass: {
                type: string;
                description: string;
            };
            selectorName?: undefined;
            pageObject?: undefined;
            className?: undefined;
            baseClass?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        testClass?: undefined;
        dependencies?: undefined;
    } | {
        found: boolean;
        testClass: any;
        dependencies: {
            pageObjects: string[];
            dataProviders: string[];
        };
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorName: {
                type: string;
                description: string;
            };
            testClass?: undefined;
            pageObject?: undefined;
            className?: undefined;
            baseClass?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        selectorName?: undefined;
        usage?: undefined;
    } | {
        found: boolean;
        selectorName: any;
        usage: {
            pageObject: string;
            selector: string;
            type: string;
            path: string;
            line: number;
        }[];
        message?: undefined;
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
            selectorName?: undefined;
            pageObject?: undefined;
            className?: undefined;
            baseClass?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        testClass?: undefined;
        chain?: undefined;
    } | {
        found: boolean;
        testClass: any;
        chain: {
            pageObjects: {
                pageObject: string;
                selectorCount: number;
                selectors: {
                    name: string;
                    selector: string;
                    type: string;
                }[];
            }[];
            totalSelectors: number;
        };
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            pageObject: {
                type: string;
                description: string;
            };
            testClass?: undefined;
            selectorName?: undefined;
            className?: undefined;
            baseClass?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        pageObject?: undefined;
        usedBy?: undefined;
        count?: undefined;
    } | {
        found: boolean;
        pageObject: any;
        usedBy: any[];
        count: number;
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
            testClass?: undefined;
            selectorName?: undefined;
            pageObject?: undefined;
            baseClass?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        className?: undefined;
        hierarchy?: undefined;
        implements?: undefined;
    } | {
        found: boolean;
        className: any;
        hierarchy: string[];
        implements: string[];
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            baseClass: {
                type: string;
                description: string;
            };
            testClass?: undefined;
            selectorName?: undefined;
            pageObject?: undefined;
            className?: undefined;
            packageName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        baseClass: any;
        count: number;
        subclasses: {
            name: string;
            path: string;
            category: "page-object" | "test" | "util" | "base" | "other";
        }[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            packageName: {
                type: string;
                description: string;
            };
            testClass?: undefined;
            selectorName?: undefined;
            pageObject?: undefined;
            className?: undefined;
            baseClass?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        package: any;
        count: number;
        classes: {
            name: string;
            fullPackage: string;
            path: string;
            category: "page-object" | "test" | "util" | "base" | "other";
        }[];
    }>;
})[];
//# sourceMappingURL=relationship-tools.d.ts.map