/**
 * Fix Suggestion Engine
 * Provides automated fix suggestions based on error analysis
 */
interface FixSuggestion {
    issue: string;
    category: string;
    fix: string;
    codeExample?: string;
    priority: 'high' | 'medium' | 'low';
}
export declare function registerFixSuggestionTools(frameworkRoot: string): ({
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testName: {
                type: string;
                description: string;
            };
            errorMessage?: undefined;
            errorType?: undefined;
            currentWait?: undefined;
            selectorType?: undefined;
            selectorValue?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        message: string;
        suggestions: never[];
        totalFailures?: undefined;
        totalSuggestions?: undefined;
        groupedByCategory?: undefined;
    } | {
        totalFailures: number;
        totalSuggestions: number;
        suggestions: FixSuggestion[];
        groupedByCategory: Record<string, FixSuggestion[]>;
        message?: undefined;
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            errorMessage: {
                type: string;
                description: string;
            };
            errorType: {
                type: string;
                description: string;
            };
            testName?: undefined;
            currentWait?: undefined;
            selectorType?: undefined;
            selectorValue?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<any>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            currentWait: {
                type: string;
                description: string;
            };
            errorMessage: {
                type: string;
                description?: undefined;
            };
            testName?: undefined;
            errorType?: undefined;
            selectorType?: undefined;
            selectorValue?: undefined;
        };
        required?: undefined;
    };
    handler: (args: any) => Promise<{
        currentConfig: Record<string, any>;
        suggestions: ({
            type: string;
            current: any;
            suggested: any;
            location: string;
            codeExample: string;
            description?: undefined;
        } | {
            type: string;
            description: string;
            codeExample: string;
            current?: undefined;
            suggested?: undefined;
            location?: undefined;
        })[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            selectorType: {
                type: string;
            };
            selectorValue: {
                type: string;
            };
            errorMessage: {
                type: string;
                description?: undefined;
            };
            testName?: undefined;
            errorType?: undefined;
            currentWait?: undefined;
        };
        required: string[];
    };
    handler: (args: any) => Promise<{
        currentSelector: any;
        suggestions: {
            issue: string;
            fix: string;
            priority: string;
            example: string;
        }[];
    }>;
} | {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: {
            testName?: undefined;
            errorMessage?: undefined;
            errorType?: undefined;
            currentWait?: undefined;
            selectorType?: undefined;
            selectorValue?: undefined;
        };
        required?: undefined;
    };
    handler: () => Promise<{
        parallelConfig: {
            currentThreads: number;
            suggestedThreads: number;
        };
        suggestions: ({
            issue: string;
            fix: string;
            priority: "high";
            location: string;
            currentImplementation: string;
            status: string;
            codeExample?: undefined;
        } | {
            issue: string;
            fix: string;
            priority: "medium";
            location: string;
            codeExample: string;
            currentImplementation?: undefined;
            status?: undefined;
        })[];
    }>;
})[];
export {};
//# sourceMappingURL=fix-suggestions.d.ts.map