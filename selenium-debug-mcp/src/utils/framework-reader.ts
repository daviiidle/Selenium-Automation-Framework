/**
 * Framework Reader Utilities
 * Reads and parses Java Selenium framework files
 */

import { promises as fs } from 'fs';
import path from 'path';

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

export class FrameworkReader {
  constructor(private frameworkRoot: string) {}

  /**
   * Read selector JSON file
   */
  async readSelectorFile(selectorFile: string): Promise<Record<string, any>> {
    const selectorPath = path.join(this.frameworkRoot, 'src/main/resources/selectors', selectorFile);

    try {
      const content = await fs.readFile(selectorPath, 'utf-8');
      return JSON.parse(content);
    } catch (error) {
      throw new Error(`Failed to read selector file ${selectorFile}: ${error}`);
    }
  }

  /**
   * Get all selector files
   */
  async getAllSelectorFiles(): Promise<string[]> {
    const selectorDir = path.join(this.frameworkRoot, 'src/main/resources/selectors');

    try {
      const files = await fs.readdir(selectorDir);
      return files.filter(f => f.endsWith('.json'));
    } catch (error) {
      return [];
    }
  }

  /**
   * Read pom.xml
   */
  async readPomXml(): Promise<string> {
    const pomPath = path.join(this.frameworkRoot, 'pom.xml');
    return await fs.readFile(pomPath, 'utf-8');
  }

  /**
   * Read TestNG configuration
   */
  async readTestNGConfig(configFile: string = 'testng-powerhouse.xml'): Promise<string> {
    const testngPath = path.join(this.frameworkRoot, 'src/test/resources/config', configFile);
    return await fs.readFile(testngPath, 'utf-8');
  }

  /**
   * Read test class file
   */
  async readTestClass(testClassName: string): Promise<string> {
    // Search in test directories
    const testDirs = [
      'src/test/java/tests/authentication',
      'src/test/java/tests/navigation',
      'src/test/java/tests/products',
      'src/test/java/tests/cart',
      'src/test/java/tests/checkout',
      'src/test/java/tests/account',
      'src/test/java/tests/error',
    ];

    for (const dir of testDirs) {
      const testPath = path.join(this.frameworkRoot, dir, `${testClassName}.java`);
      try {
        const content = await fs.readFile(testPath, 'utf-8');
        return content;
      } catch {
        continue;
      }
    }

    throw new Error(`Test class ${testClassName} not found`);
  }

  /**
   * Read Page Object class
   */
  async readPageObject(pageClassName: string): Promise<string> {
    const pagesDir = path.join(this.frameworkRoot, 'src/main/java/com/demowebshop/automation/pages');
    const pagePath = path.join(pagesDir, `${pageClassName}.java`);

    try {
      return await fs.readFile(pagePath, 'utf-8');
    } catch (error) {
      // Try common subdirectory
      const commonPath = path.join(pagesDir, 'common', `${pageClassName}.java`);
      return await fs.readFile(commonPath, 'utf-8');
    }
  }

  /**
   * Extract test methods from test class content
   */
  extractTestMethods(testClassContent: string): TestInfo[] {
    const tests: TestInfo[] = [];
    const lines = testClassContent.split('\n');

    let className = '';
    const classMatch = testClassContent.match(/class\s+(\w+)/);
    if (classMatch) {
      className = classMatch[1];
    }

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      if (line.includes('@Test')) {
        // Look for the method name in the next few lines
        for (let j = i + 1; j < Math.min(i + 5, lines.length); j++) {
          const methodMatch = lines[j].match(/\s+(public|private|protected)?\s+void\s+(\w+)\s*\(/);
          if (methodMatch) {
            tests.push({
              className,
              methodName: methodMatch[2],
              filePath: '',
              lineNumber: j + 1,
            });
            break;
          }
        }
      }
    }

    return tests;
  }

  /**
   * Parse selector from Page Object content
   */
  extractSelectorsFromPageObject(pageObjectContent: string): SelectorDefinition[] {
    const selectors: SelectorDefinition[] = [];
    const lines = pageObjectContent.split('\n');

    for (const line of lines) {
      // Match By.cssSelector, By.id, By.xpath, etc.
      const cssMatch = line.match(/By\.cssSelector\s*\(\s*"([^"]+)"\s*\)/);
      if (cssMatch) {
        const varMatch = line.match(/(\w+)\s*=/);
        selectors.push({
          name: varMatch ? varMatch[1] : 'unknown',
          selector: cssMatch[1],
          type: 'css',
        });
      }

      const xpathMatch = line.match(/By\.xpath\s*\(\s*"([^"]+)"\s*\)/);
      if (xpathMatch) {
        const varMatch = line.match(/(\w+)\s*=/);
        selectors.push({
          name: varMatch ? varMatch[1] : 'unknown',
          selector: xpathMatch[1],
          type: 'xpath',
        });
      }

      const idMatch = line.match(/By\.id\s*\(\s*"([^"]+)"\s*\)/);
      if (idMatch) {
        const varMatch = line.match(/(\w+)\s*=/);
        selectors.push({
          name: varMatch ? varMatch[1] : 'unknown',
          selector: idMatch[1],
          type: 'id',
        });
      }
    }

    return selectors;
  }

  /**
   * Get BaseTest configuration
   */
  async getBaseTestConfig(): Promise<Record<string, any>> {
    const baseTestPath = path.join(this.frameworkRoot, 'src/test/java/base/BaseTest.java');
    const content = await fs.readFile(baseTestPath, 'utf-8');

    const config: Record<string, any> = {};

    // Extract timeout values
    const timeoutMatch = content.match(/implicitlyWait\s*\(\s*Duration\.ofSeconds\s*\(\s*(\d+)\s*\)/);
    if (timeoutMatch) {
      config.implicitWait = parseInt(timeoutMatch[1]) * 1000;
    }

    const pageLoadMatch = content.match(/pageLoadTimeout\s*\(\s*Duration\.ofSeconds\s*\(\s*(\d+)\s*\)/);
    if (pageLoadMatch) {
      config.pageLoadTimeout = parseInt(pageLoadMatch[1]) * 1000;
    }

    return config;
  }

  /**
   * Check if file exists
   */
  async fileExists(relativePath: string): Promise<boolean> {
    try {
      const fullPath = path.join(this.frameworkRoot, relativePath);
      await fs.access(fullPath);
      return true;
    } catch {
      return false;
    }
  }
}
