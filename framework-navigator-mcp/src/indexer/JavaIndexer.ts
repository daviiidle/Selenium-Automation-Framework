/**
 * Java Indexer
 * Scans and indexes all Java files in the framework
 */

import { promises as fs } from 'fs';
import path from 'path';
import { JavaParser } from '../utils/java-parser.js';
import { FrameworkIndex, IndexedClass } from './FrameworkIndex.js';

export class JavaIndexer {
  private frameworkRoot: string;
  private index: FrameworkIndex;

  constructor(frameworkRoot: string, index: FrameworkIndex) {
    this.frameworkRoot = frameworkRoot;
    this.index = index;
  }

  /**
   * Index all Java files in the framework
   */
  async indexAll(): Promise<void> {
    console.error('Starting Java indexing...');

    const javaFiles = await this.findAllJavaFiles();
    console.error(`Found ${javaFiles.length} Java files`);

    for (const filePath of javaFiles) {
      await this.indexFile(filePath);
    }

    console.error('Java indexing complete');
  }

  /**
   * Find all Java files
   */
  private async findAllJavaFiles(): Promise<string[]> {
    const javaFiles: string[] = [];

    // Scan main source
    const mainPath = path.join(this.frameworkRoot, 'src/main/java');
    if (await this.directoryExists(mainPath)) {
      await this.scanDirectory(mainPath, javaFiles);
    }

    // Scan test source
    const testPath = path.join(this.frameworkRoot, 'src/test/java');
    if (await this.directoryExists(testPath)) {
      await this.scanDirectory(testPath, javaFiles);
    }

    return javaFiles;
  }

  /**
   * Recursively scan directory for Java files
   */
  private async scanDirectory(dir: string, files: string[]): Promise<void> {
    try {
      const entries = await fs.readdir(dir, { withFileTypes: true });

      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);

        if (entry.isDirectory()) {
          await this.scanDirectory(fullPath, files);
        } else if (entry.isFile() && entry.name.endsWith('.java')) {
          files.push(fullPath);
        }
      }
    } catch (error) {
      console.error(`Error scanning directory ${dir}:`, error);
    }
  }

  /**
   * Index a single Java file
   */
  private async indexFile(filePath: string): Promise<void> {
    try {
      const content = await fs.readFile(filePath, 'utf-8');
      const classInfo = JavaParser.parseJavaFile(content, filePath);

      if (!classInfo) {
        return; // Skip non-class files (interfaces, enums, etc.)
      }

      // Determine category
      const category = this.categorizeClass(classInfo, filePath);

      // Add to index
      this.index.addClass(classInfo.className, classInfo, filePath, category);

      // If it's a Page Object, extract selectors
      if (category === 'page-object') {
        const selectors = JavaParser.extractSelectors(content);
        selectors.forEach(selector => {
          this.index.addSelector({
            name: selector.name,
            selector: selector.selector,
            type: selector.type,
            pageObject: classInfo.className,
            filePath,
            lineNumber: selector.lineNumber,
          });
        });
      }

      // If it's a test, extract dependencies
      if (category === 'test') {
        const dependencies = this.extractTestDependencies(classInfo);
        this.index.addTestDependency(classInfo.className, dependencies);
      }
    } catch (error) {
      console.error(`Error indexing file ${filePath}:`, error);
    }
  }

  /**
   * Categorize a class
   */
  private categorizeClass(classInfo: any, filePath: string): IndexedClass['category'] {
    const className = classInfo.className;
    const extendsClass = classInfo.extendsClass || '';

    // Base classes
    if (className === 'BasePage' || className === 'BaseTest') {
      return 'base';
    }

    // Page Objects
    if (JavaParser.isPageObject(classInfo)) {
      return 'page-object';
    }

    // Test classes
    if (JavaParser.isTestClass(classInfo)) {
      return 'test';
    }

    // Utilities
    if (filePath.includes('/utils/') ||
        filePath.includes('/factories/') ||
        filePath.includes('/listeners/') ||
        filePath.includes('/config/')) {
      return 'util';
    }

    return 'other';
  }

  /**
   * Extract test dependencies (Page Objects used by test)
   */
  private extractTestDependencies(classInfo: any): any {
    const pageObjects: string[] = [];
    const dataProviders: string[] = [];

    // Look for Page Object fields
    classInfo.fields.forEach((field: any) => {
      if (field.type.endsWith('Page')) {
        pageObjects.push(field.type);
      }
    });

    // Look for DataProvider annotations
    classInfo.methods.forEach((method: any) => {
      if (method.annotations.includes('DataProvider')) {
        dataProviders.push(method.name);
      }
    });

    return {
      testClass: classInfo.className,
      pageObjects: [...new Set(pageObjects)], // Remove duplicates
      dataProviders,
    };
  }

  /**
   * Check if directory exists
   */
  private async directoryExists(dir: string): Promise<boolean> {
    try {
      const stat = await fs.stat(dir);
      return stat.isDirectory();
    } catch {
      return false;
    }
  }
}
