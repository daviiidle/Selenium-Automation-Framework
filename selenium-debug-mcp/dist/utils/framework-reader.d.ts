/**
 * Framework Reader Utilities
 * Reads and parses Java Selenium framework files
 */
export interface SelectorDefinition {
    name: string;
    selector: string;
    type: 'css' | 'xpath' | 'id' | 'name' | 'className';
}
export interface PageObjectInfo {
    className: string;
    filePath: string;
    selectors: SelectorDefinition[];
}
export interface TestInfo {
    className: string;
    methodName: string;
    filePath: string;
    lineNumber?: number;
}
export declare class FrameworkReader {
    private frameworkRoot;
    constructor(frameworkRoot: string);
    /**
     * Read selector JSON file
     */
    readSelectorFile(selectorFile: string): Promise<Record<string, any>>;
    /**
     * Get all selector files
     */
    getAllSelectorFiles(): Promise<string[]>;
    /**
     * Read pom.xml
     */
    readPomXml(): Promise<string>;
    /**
     * Read TestNG configuration
     */
    readTestNGConfig(configFile?: string): Promise<string>;
    /**
     * Read test class file
     */
    readTestClass(testClassName: string): Promise<string>;
    /**
     * Read Page Object class
     */
    readPageObject(pageClassName: string): Promise<string>;
    /**
     * Extract test methods from test class content
     */
    extractTestMethods(testClassContent: string): TestInfo[];
    /**
     * Parse selector from Page Object content
     */
    extractSelectorsFromPageObject(pageObjectContent: string): SelectorDefinition[];
    /**
     * Get BaseTest configuration
     */
    getBaseTestConfig(): Promise<Record<string, any>>;
    /**
     * Check if file exists
     */
    fileExists(relativePath: string): Promise<boolean>;
}
//# sourceMappingURL=framework-reader.d.ts.map