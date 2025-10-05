/**
 * Search Tools
 * Tools for searching across the framework
 */
export declare function registerSearchTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorName: {
                type: string;
                description: string;
            };
            pageObject: {
                type: string;
                description: string;
            };
            annotation?: undefined;
            detailed?: undefined;
            fileName?: undefined;
            importPattern?: undefined;
            providerName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        count?: undefined;
        selectors?: undefined;
    } | {
        found: boolean;
        count: number;
        selectors: {
            name: string;
            selector: string;
            type: string;
            pageObject: string;
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
            annotation: {
                type: string;
                description: string;
            };
            selectorName?: undefined;
            pageObject?: undefined;
            detailed?: undefined;
            fileName?: undefined;
            importPattern?: undefined;
            providerName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        count: number;
        annotation: any;
        methods: any[];
    }>;
} | {
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
            selectorName?: undefined;
            pageObject?: undefined;
            annotation?: undefined;
            fileName?: undefined;
            importPattern?: undefined;
            providerName?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        count: number;
        files: {
            name: string;
            selectors: number;
        }[];
    } | {
        count: number;
        files: string[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            fileName: {
                type: string;
                description: string;
            };
            selectorName?: undefined;
            pageObject?: undefined;
            annotation?: undefined;
            detailed?: undefined;
            importPattern?: undefined;
            providerName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        fileName?: undefined;
        count?: undefined;
        selectors?: undefined;
    } | {
        found: boolean;
        fileName: any;
        count: number;
        selectors: {
            name: string;
            selector: string;
            type: string;
        }[];
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            importPattern: {
                type: string;
                description: string;
            };
            selectorName?: undefined;
            pageObject?: undefined;
            annotation?: undefined;
            detailed?: undefined;
            fileName?: undefined;
            providerName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        count: number;
        pattern: any;
        classes: any[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            providerName: {
                type: string;
                description: string;
            };
            selectorName?: undefined;
            pageObject?: undefined;
            annotation?: undefined;
            detailed?: undefined;
            fileName?: undefined;
            importPattern?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        found: boolean;
        count: number;
        dataProviders: any[];
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
            selectorName?: undefined;
            annotation?: undefined;
            detailed?: undefined;
            fileName?: undefined;
            importPattern?: undefined;
            providerName?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        found: boolean;
        message: string;
        pageObject?: undefined;
        count?: undefined;
        selectors?: undefined;
    } | {
        found: boolean;
        pageObject: any;
        count: number;
        selectors: {
            name: string;
            selector: string;
            type: string;
            line: number;
        }[];
        message?: undefined;
    }>;
})[];
//# sourceMappingURL=search-tools.d.ts.map