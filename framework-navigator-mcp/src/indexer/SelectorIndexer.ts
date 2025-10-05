/**
 * Selector Indexer
 * Indexes selector JSON files
 */

import { promises as fs } from 'fs';
import path from 'path';
import { FrameworkIndex } from './FrameworkIndex.js';

export class SelectorIndexer {
  private frameworkRoot: string;
  private index: FrameworkIndex;

  constructor(frameworkRoot: string, index: FrameworkIndex) {
    this.frameworkRoot = frameworkRoot;
    this.index = index;
  }

  /**
   * Index all selector JSON files
   */
  async indexAll(): Promise<void> {
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
    } catch (error) {
      console.error('Error indexing selector JSONs:', error);
    }
  }

  /**
   * Index a single selector JSON file
   */
  private async indexFile(fileName: string, selectorDir: string): Promise<void> {
    try {
      const filePath = path.join(selectorDir, fileName);
      const content = await fs.readFile(filePath, 'utf-8');
      const json = JSON.parse(content);

      const entries: Array<{name: string; selector: string; type: string; fileName: string}> = [];

      // Parse selector JSON structure
      this.parseSelectors(json, '', entries, fileName);

      this.index.addSelectorJson(fileName, entries);
    } catch (error) {
      console.error(`Error indexing selector file ${fileName}:`, error);
    }
  }

  /**
   * Recursively parse selectors from JSON
   */
  private parseSelectors(
    obj: any,
    prefix: string,
    entries: Array<{name: string; selector: string; type: string; fileName: string}>,
    fileName: string
  ): void {
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
            selector: value.selector as string,
            type: value.type as string,
            fileName,
          });
        } else {
          // Recurse into nested object
          this.parseSelectors(value, fullKey, entries, fileName);
        }
      }
    }
  }
}
