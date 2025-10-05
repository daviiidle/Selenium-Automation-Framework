/**
 * Java Parser Utilities
 * Simple regex-based Java parsing for extracting class/method information
 */
export interface MethodInfo {
    name: string;
    returnType: string;
    parameters: string;
    lineNumber: number;
    isPublic: boolean;
    isStatic: boolean;
    annotations: string[];
}
export interface FieldInfo {
    name: string;
    type: string;
    lineNumber: number;
    isPrivate: boolean;
    isStatic: boolean;
    value?: string;
}
export interface ClassInfo {
    className: string;
    packageName: string;
    extendsClass?: string;
    implementsInterfaces: string[];
    imports: string[];
    methods: MethodInfo[];
    fields: FieldInfo[];
    annotations: string[];
    isAbstract: boolean;
    lineCount: number;
}
export declare class JavaParser {
    /**
     * Parse Java file content and extract class information
     */
    static parseJavaFile(content: string, filePath: string): ClassInfo | null;
    /**
     * Extract imports from Java content
     */
    private static extractImports;
    /**
     * Extract class-level annotations
     */
    private static extractClassAnnotations;
    /**
     * Extract methods from Java file
     */
    private static extractMethods;
    /**
     * Extract fields from Java file
     */
    private static extractFields;
    /**
     * Extract selector definitions from Page Object
     */
    static extractSelectors(content: string): Array<{
        name: string;
        selector: string;
        type: string;
        lineNumber: number;
    }>;
    /**
     * Check if file is a Page Object
     */
    static isPageObject(classInfo: ClassInfo): boolean;
    /**
     * Check if file is a Test class
     */
    static isTestClass(classInfo: ClassInfo): boolean;
}
//# sourceMappingURL=java-parser.d.ts.map