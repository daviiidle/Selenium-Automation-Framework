/**
 * Java Indexer
 * Scans and indexes all Java files in the framework
 */
import { FrameworkIndex } from './FrameworkIndex.js';
export declare class JavaIndexer {
    private frameworkRoot;
    private index;
    constructor(frameworkRoot: string, index: FrameworkIndex);
    /**
     * Index all Java files in the framework
     */
    indexAll(): Promise<void>;
    /**
     * Find all Java files
     */
    private findAllJavaFiles;
    /**
     * Recursively scan directory for Java files
     */
    private scanDirectory;
    /**
     * Index a single Java file
     */
    private indexFile;
    /**
     * Categorize a class
     */
    private categorizeClass;
    /**
     * Extract test dependencies (Page Objects used by test)
     */
    private extractTestDependencies;
    /**
     * Check if directory exists
     */
    private directoryExists;
}
//# sourceMappingURL=JavaIndexer.d.ts.map