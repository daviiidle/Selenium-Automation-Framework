/**
 * Framework Resources
 * Expose framework files and data as MCP resources
 */
export declare function registerFrameworkResources(frameworkRoot: string): {
    uri: string;
    name: string;
    description: string;
    mimeType: string;
    handler: () => Promise<string>;
}[];
//# sourceMappingURL=framework-resources.d.ts.map