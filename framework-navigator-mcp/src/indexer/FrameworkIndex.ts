/**
 * Framework Index
 * In-memory cache of framework structure for fast lookups
 */

import { ClassInfo, MethodInfo, FieldInfo } from '../utils/java-parser.js';

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

export class FrameworkIndex {
  private classes: Map<string, IndexedClass> = new Map();
  private methods: Map<string, IndexedMethod[]> = new Map();
  private selectors: Map<string, IndexedSelector[]> = new Map();
  private selectorJsons: Map<string, SelectorJsonEntry[]> = new Map();
  private testDependencies: Map<string, TestDependency> = new Map();

  // Category maps for quick filtering
  private pageObjects: Set<string> = new Set();
  private testClasses: Set<string> = new Set();
  private baseClasses: Set<string> = new Set();

  // Index status
  private indexed: boolean = false;
  private indexTime: Date | null = null;

  /**
   * Add a class to the index
   */
  addClass(className: string, classInfo: ClassInfo, filePath: string, category: IndexedClass['category']) {
    this.classes.set(className, { classInfo, filePath, category });

    // Update category sets
    if (category === 'page-object') {
      this.pageObjects.add(className);
    } else if (category === 'test') {
      this.testClasses.add(className);
    } else if (category === 'base') {
      this.baseClasses.add(className);
    }

    // Index methods
    classInfo.methods.forEach(method => {
      const methodName = method.name;
      if (!this.methods.has(methodName)) {
        this.methods.set(methodName, []);
      }
      this.methods.get(methodName)!.push({
        className,
        methodInfo: method,
        filePath,
      });
    });
  }

  /**
   * Add a selector to the index
   */
  addSelector(selector: IndexedSelector) {
    const selectorName = selector.name;
    if (!this.selectors.has(selectorName)) {
      this.selectors.set(selectorName, []);
    }
    this.selectors.get(selectorName)!.push(selector);
  }

  /**
   * Add selector JSON entries
   */
  addSelectorJson(fileName: string, entries: SelectorJsonEntry[]) {
    this.selectorJsons.set(fileName, entries);
  }

  /**
   * Add test dependencies
   */
  addTestDependency(testClass: string, dependency: TestDependency) {
    this.testDependencies.set(testClass, dependency);
  }

  /**
   * Get class by name
   */
  getClass(className: string): IndexedClass | undefined {
    return this.classes.get(className);
  }

  /**
   * Find classes by pattern
   */
  findClasses(pattern: string): IndexedClass[] {
    const regex = new RegExp(pattern, 'i');
    return Array.from(this.classes.values()).filter(c =>
      regex.test(c.classInfo.className)
    );
  }

  /**
   * Get all Page Objects
   */
  getPageObjects(): IndexedClass[] {
    return Array.from(this.pageObjects).map(name => this.classes.get(name)!);
  }

  /**
   * Get all Test classes
   */
  getTestClasses(): IndexedClass[] {
    return Array.from(this.testClasses).map(name => this.classes.get(name)!);
  }

  /**
   * Get all classes
   */
  getAllClasses(): IndexedClass[] {
    return Array.from(this.classes.values());
  }

  /**
   * Find methods by name
   */
  findMethods(methodName: string): IndexedMethod[] {
    return this.methods.get(methodName) || [];
  }

  /**
   * Find methods by pattern
   */
  findMethodsByPattern(pattern: string): IndexedMethod[] {
    const regex = new RegExp(pattern, 'i');
    const results: IndexedMethod[] = [];

    this.methods.forEach((methods) => {
      methods.forEach(method => {
        if (regex.test(method.methodInfo.name)) {
          results.push(method);
        }
      });
    });

    return results;
  }

  /**
   * Find selectors
   */
  findSelectors(selectorName: string): IndexedSelector[] {
    return this.selectors.get(selectorName) || [];
  }

  /**
   * Get all selectors for a Page Object
   */
  getSelectorsForPageObject(pageObjectName: string): IndexedSelector[] {
    const allSelectors: IndexedSelector[] = [];
    this.selectors.forEach(selectorList => {
      selectorList.forEach(selector => {
        if (selector.pageObject === pageObjectName) {
          allSelectors.push(selector);
        }
      });
    });
    return allSelectors;
  }

  /**
   * Get selector JSON entries
   */
  getSelectorJson(fileName: string): SelectorJsonEntry[] | undefined {
    return this.selectorJsons.get(fileName);
  }

  /**
   * Get all selector JSON files
   */
  getAllSelectorJsons(): string[] {
    return Array.from(this.selectorJsons.keys());
  }

  /**
   * Get test dependencies
   */
  getTestDependencies(testClass: string): TestDependency | undefined {
    return this.testDependencies.get(testClass);
  }

  /**
   * Get framework statistics
   */
  getStats() {
    return {
      totalClasses: this.classes.size,
      pageObjects: this.pageObjects.size,
      testClasses: this.testClasses.size,
      baseClasses: this.baseClasses.size,
      totalMethods: Array.from(this.methods.values()).reduce((sum, m) => sum + m.length, 0),
      totalSelectors: Array.from(this.selectors.values()).reduce((sum, s) => sum + s.length, 0),
      selectorJsonFiles: this.selectorJsons.size,
      indexed: this.indexed,
      indexTime: this.indexTime?.toISOString(),
    };
  }

  /**
   * Mark index as complete
   */
  markIndexed() {
    this.indexed = true;
    this.indexTime = new Date();
  }

  /**
   * Check if indexed
   */
  isIndexed(): boolean {
    return this.indexed;
  }

  /**
   * Clear index
   */
  clear() {
    this.classes.clear();
    this.methods.clear();
    this.selectors.clear();
    this.selectorJsons.clear();
    this.testDependencies.clear();
    this.pageObjects.clear();
    this.testClasses.clear();
    this.baseClasses.clear();
    this.indexed = false;
    this.indexTime = null;
  }
}
