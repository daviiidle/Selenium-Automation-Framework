/**
 * Selector Indexer
 * Indexes selector JSON files
 */
import { promises as fs } from 'fs';
import path from 'path';
export class SelectorIndexer {
    frameworkRoot;
    index;
    constructor(frameworkRoot, index) {
        this.frameworkRoot = frameworkRoot;
        this.index = index;
    }
    /**
     * Index all selector JSON files
     */
    async indexAll() {
        console.error('Starting selector JSON indexing...');
        const selectorDir = path.join(this.frameworkRoot, 'src/main/resources/selectors');
        try {
            const files = await fs.readdir(selectorDir);
            const jsonFiles = files.filter(f => f.endsWith('.json'));
            console.error(`Found ${jsonFiles.length} selector JSON files`);
            for (const file of jsonFiles) {
                await this.indexFile(file, selectorDir);
            }
            console.error('Selector JSON indexing complete');
        }
        catch (error) {
            console.error('Error indexing selector JSONs:', error);
        }
    }
    /**
     * Index a single selector JSON file
     */
    async indexFile(fileName, selectorDir) {
        try {
            const filePath = path.join(selectorDir, fileName);
            const content = await fs.readFile(filePath, 'utf-8');
            const json = JSON.parse(content);
            const entries = [];
            // Parse selector JSON structure
            this.parseSelectors(json, '', entries, fileName);
            this.index.addSelectorJson(fileName, entries);
        }
        catch (error) {
            console.error(`Error indexing selector file ${fileName}:`, error);
        }
    }
    /**
     * Recursively parse selectors from JSON
     */
    parseSelectors(obj, prefix, entries, fileName) {
        if (typeof obj !== 'object' || obj === null) {
            return;
        }
        for (const [key, value] of Object.entries(obj)) {
            const fullKey = prefix ? `${prefix}.${key}` : key;
            if (typeof value === 'object' && value !== null) {
                // Check if this is a selector object (has 'selector' and 'type' properties)
                if ('selector' in value && 'type' in value) {
                    entries.push({
                        name: fullKey,
                        selector: value.selector,
                        type: value.type,
                        fileName,
                    });
                }
                else {
                    // Recurse into nested object
                    this.parseSelectors(value, fullKey, entries, fileName);
                }
            }
        }
    }
}
//# sourceMappingURL=SelectorIndexer.js.map