/**
 * Framework Index
 * In-memory cache of framework structure for fast lookups
 */
import { ClassInfo, MethodInfo } from '../utils/java-parser.js';
export interface IndexedClass {
    classInfo: ClassInfo;
    filePath: string;
    category: 'page-object' | 'test' | 'util' | 'base' | 'other';
}
export interface IndexedMethod {
    className: string;
    methodInfo: MethodInfo;
    filePath: string;
}
export interface IndexedSelector {
    name: string;
    selector: string;
    type: string;
    pageObject: string;
    filePath: string;
    lineNumber: number;
}
export interface SelectorJsonEntry {
    name: string;
    selector: string;
    type: string;
    fileName: string;
}
export interface TestDependency {
    testClass: string;
    pageObjects: string[];
    dataProviders: string[];
}
export declare class FrameworkIndex {
    private classes;
    private methods;
    private selectors;
    private selectorJsons;
    private testDependencies;
    private pageObjects;
    private testClasses;
    private baseClasses;
    private indexed;
    private indexTime;
    /**
     * Add a class to the index
     */
    addClass(className: string, classInfo: ClassInfo, filePath: string, category: IndexedClass['category']): void;
    /**
     * Add a selector to the index
     */
    addSelector(selector: IndexedSelector): void;
    /**
     * Add selector JSON entries
     */
    addSelectorJson(fileName: string, entries: SelectorJsonEntry[]): void;
    /**
     * Add test dependencies
     */
    addTestDependency(testClass: string, dependency: TestDependency): void;
    /**
     * Get class by name
     */
    getClass(className: string): IndexedClass | undefined;
    /**
     * Find classes by pattern
     */
    findClasses(pattern: string): IndexedClass[];
    /**
     * Get all Page Objects
     */
    getPageObjects(): IndexedClass[];
    /**
     * Get all Test classes
     */
    getTestClasses(): IndexedClass[];
    /**
     * Get all classes
     */
    getAllClasses(): IndexedClass[];
    /**
     * Find methods by name
     */
    findMethods(methodName: string): IndexedMethod[];
    /**
     * Find methods by pattern
     */
    findMethodsByPattern(pattern: string): IndexedMethod[];
    /**
     * Find selectors
     */
    findSelectors(selectorName: string): IndexedSelector[];
    /**
     * Get all selectors for a Page Object
     */
    getSelectorsForPageObject(pageObjectName: string): IndexedSelector[];
    /**
     * Get selector JSON entries
     */
    getSelectorJson(fileName: string): SelectorJsonEntry[] | undefined;
    /**
     * Get all selector JSON files
     */
    getAllSelectorJsons(): string[];
    /**
     * Get test dependencies
     */
    getTestDependencies(testClass: string): TestDependency | undefined;
    /**
     * Get framework statistics
     */
    getStats(): {
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
    /**
     * Mark index as complete
     */
    markIndexed(): void;
    /**
     * Check if indexed
     */
    isIndexed(): boolean;
    /**
     * Clear index
     */
    clear(): void;
}
//# sourceMappingURL=FrameworkIndex.d.ts.map