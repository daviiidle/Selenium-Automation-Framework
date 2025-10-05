/**
 * Browser Controller for Live Selenium Testing
 * Manages WebDriver instances for debugging and validation
 */

import { Builder, WebDriver, By, until, WebElement } from 'selenium-webdriver';
import chrome from 'selenium-webdriver/chrome.js';
import { promises as fs } from 'fs';
import path from 'path';

export interface BrowserConfig {
  browser?: string;
  headless?: boolean;
  implicitWait?: number;
  pageLoadTimeout?: number;
  scriptTimeout?: number;
}

export interface SelectorInfo {
  type: 'css' | 'xpath' | 'id' | 'name' | 'className' | 'linkText' | 'partialLinkText';
  value: string;
}

export interface ElementValidationResult {
  found: boolean;
  visible?: boolean;
  clickable?: boolean;
  enabled?: boolean;
  text?: string;
  tagName?: string;
  attributes?: Record<string, string>;
  error?: string;
  timeTaken?: number;
}

export class BrowserController {
  private driver: WebDriver | null = null;
  private config: BrowserConfig;
  private screenshotDir: string;

  constructor(config: BrowserConfig = {}, screenshotDir: string = './screenshots') {
    this.config = {
      browser: config.browser || 'chrome',
      headless: config.headless !== false, // default true
      implicitWait: config.implicitWait || 10000,
      pageLoadTimeout: config.pageLoadTimeout || 30000,
      scriptTimeout: config.scriptTimeout || 30000,
    };
    this.screenshotDir = screenshotDir;
  }

  /**
   * Initialize WebDriver instance
   */
  async initialize(): Promise<void> {
    if (this.driver) {
      return; // Already initialized
    }

    const chromeOptions = new chrome.Options();

    if (this.config.headless) {
      chromeOptions.addArguments('--headless=new');
    }

    // Match your framework's Chrome options for consistency
    chromeOptions.addArguments('--no-sandbox');
    chromeOptions.addArguments('--disable-dev-shm-usage');
    chromeOptions.addArguments('--disable-gpu');
    chromeOptions.addArguments('--window-size=1920,1080');
    chromeOptions.addArguments('--disable-extensions');
    chromeOptions.setUserPreferences({
      'useAutomationExtension': false,
    });
    chromeOptions.excludeSwitches('enable-automation');

    this.driver = await new Builder()
      .forBrowser('chrome')
      .setChromeOptions(chromeOptions)
      .build();

    // Set timeouts
    await this.driver.manage().setTimeouts({
      implicit: this.config.implicitWait,
      pageLoad: this.config.pageLoadTimeout,
      script: this.config.scriptTimeout,
    });

    console.error('WebDriver initialized successfully');
  }

  /**
   * Navigate to URL
   */
  async navigateTo(url: string): Promise<void> {
    if (!this.driver) {
      await this.initialize();
    }
    await this.driver!.get(url);
    console.error(`Navigated to: ${url}`);
  }

  /**
   * Find element by selector
   */
  async findElement(selector: SelectorInfo): Promise<WebElement | null> {
    if (!this.driver) {
      throw new Error('WebDriver not initialized');
    }

    try {
      const by = this.selectorToBy(selector);
      const element = await this.driver.findElement(by);
      return element;
    } catch (error) {
      return null;
    }
  }

  /**
   * Validate element with detailed information
   */
  async validateElement(selector: SelectorInfo, waitTime: number = 10000): Promise<ElementValidationResult> {
    const startTime = Date.now();

    try {
      if (!this.driver) {
        await this.initialize();
      }

      const by = this.selectorToBy(selector);

      // Wait for element presence
      const element = await this.driver!.wait(
        until.elementLocated(by),
        waitTime,
        `Element not found: ${selector.type}=${selector.value}`
      );

      const timeTaken = Date.now() - startTime;

      // Gather detailed information
      const isDisplayed = await element.isDisplayed();
      const isEnabled = await element.isEnabled();
      const text = await element.getText().catch(() => '');
      const tagName = await element.getTagName();

      // Get common attributes
      const attributes: Record<string, string> = {};
      const commonAttrs = ['id', 'class', 'name', 'type', 'value', 'placeholder', 'href'];
      for (const attr of commonAttrs) {
        const value = await element.getAttribute(attr);
        if (value) {
          attributes[attr] = value;
        }
      }

      // Check if clickable
      let clickable = false;
      try {
        await this.driver!.wait(until.elementIsVisible(element), 2000);
        clickable = isDisplayed && isEnabled;
      } catch {
        clickable = false;
      }

      return {
        found: true,
        visible: isDisplayed,
        clickable,
        enabled: isEnabled,
        text,
        tagName,
        attributes,
        timeTaken,
      };
    } catch (error) {
      return {
        found: false,
        error: error instanceof Error ? error.message : String(error),
        timeTaken: Date.now() - startTime,
      };
    }
  }

  /**
   * Test different wait strategies for an element
   */
  async testWaitStrategies(selector: SelectorInfo): Promise<Record<string, ElementValidationResult>> {
    const strategies = {
      'implicit-5s': 5000,
      'implicit-10s': 10000,
      'implicit-15s': 15000,
      'explicit-20s': 20000,
    };

    const results: Record<string, ElementValidationResult> = {};

    for (const [strategyName, timeout] of Object.entries(strategies)) {
      results[strategyName] = await this.validateElement(selector, timeout);
    }

    return results;
  }

  /**
   * Try alternative selectors for an element
   */
  async findAlternativeSelectors(currentSelector: SelectorInfo, context?: string): Promise<SelectorInfo[]> {
    const alternatives: SelectorInfo[] = [];

    try {
      if (!this.driver) {
        await this.initialize();
      }

      // Try to find the element first
      const element = await this.findElement(currentSelector);
      if (!element) {
        return alternatives;
      }

      // Generate alternative selectors
      const id = await element.getAttribute('id');
      if (id) {
        alternatives.push({ type: 'id', value: id });
        alternatives.push({ type: 'css', value: `#${id}` });
      }

      const className = await element.getAttribute('class');
      if (className) {
        const classes = className.split(' ').filter(c => c);
        if (classes.length > 0) {
          alternatives.push({ type: 'css', value: `.${classes.join('.')}` });
        }
      }

      const name = await element.getAttribute('name');
      if (name) {
        alternatives.push({ type: 'name', value: name });
        alternatives.push({ type: 'css', value: `[name="${name}"]` });
      }

      const tagName = await element.getTagName();
      alternatives.push({ type: 'css', value: tagName });

    } catch (error) {
      console.error('Error finding alternatives:', error);
    }

    return alternatives;
  }

  /**
   * Take screenshot
   */
  async takeScreenshot(filename: string): Promise<string> {
    if (!this.driver) {
      throw new Error('WebDriver not initialized');
    }

    await fs.mkdir(this.screenshotDir, { recursive: true });
    const filepath = path.join(this.screenshotDir, `${filename}.png`);
    const screenshot = await this.driver.takeScreenshot();
    await fs.writeFile(filepath, screenshot, 'base64');

    return filepath;
  }

  /**
   * Get current page source
   */
  async getPageSource(): Promise<string> {
    if (!this.driver) {
      throw new Error('WebDriver not initialized');
    }
    return await this.driver.getPageSource();
  }

  /**
   * Get current URL
   */
  async getCurrentUrl(): Promise<string> {
    if (!this.driver) {
      throw new Error('WebDriver not initialized');
    }
    return await this.driver.getCurrentUrl();
  }

  /**
   * Execute JavaScript
   */
  async executeScript(script: string, ...args: any[]): Promise<any> {
    if (!this.driver) {
      throw new Error('WebDriver not initialized');
    }
    return await this.driver.executeScript(script, ...args);
  }

  /**
   * Quit WebDriver
   */
  async quit(): Promise<void> {
    if (this.driver) {
      await this.driver.quit();
      this.driver = null;
      console.error('WebDriver quit successfully');
    }
  }

  /**
   * Convert SelectorInfo to Selenium By locator
   */
  private selectorToBy(selector: SelectorInfo): By {
    switch (selector.type) {
      case 'css':
        return By.css(selector.value);
      case 'xpath':
        return By.xpath(selector.value);
      case 'id':
        return By.id(selector.value);
      case 'name':
        return By.name(selector.value);
      case 'className':
        return By.className(selector.value);
      case 'linkText':
        return By.linkText(selector.value);
      case 'partialLinkText':
        return By.partialLinkText(selector.value);
      default:
        throw new Error(`Unsupported selector type: ${selector.type}`);
    }
  }
}
