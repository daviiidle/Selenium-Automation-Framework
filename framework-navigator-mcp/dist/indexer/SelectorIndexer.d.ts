/**
 * Selector Indexer
 * Indexes selector JSON files
 */
import { FrameworkIndex } from './FrameworkIndex.js';
export declare class SelectorIndexer {
    private frameworkRoot;
    private index;
    constructor(frameworkRoot: string, index: FrameworkIndex);
    /**
     * Index all selector JSON files
     */
    indexAll(): Promise<void>;
    /**
     * Index a single selector JSON file
     */
    private indexFile;
    /**
     * Recursively parse selectors from JSON
     */
    private parseSelectors;
}
//# sourceMappingURL=SelectorIndexer.d.ts.map