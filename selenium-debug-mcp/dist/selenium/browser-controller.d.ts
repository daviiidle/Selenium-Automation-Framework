/**
 * Browser Controller for Live Selenium Testing
 * Manages WebDriver instances for debugging and validation
 */
import { WebElement } from 'selenium-webdriver';
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
export declare class BrowserController {
    private driver;
    private config;
    private screenshotDir;
    constructor(config?: BrowserConfig, screenshotDir?: string);
    /**
     * Initialize WebDriver instance
     */
    initialize(): Promise<void>;
    /**
     * Navigate to URL
     */
    navigateTo(url: string): Promise<void>;
    /**
     * Find element by selector
     */
    findElement(selector: SelectorInfo): Promise<WebElement | null>;
    /**
     * Validate element with detailed information
     */
    validateElement(selector: SelectorInfo, waitTime?: number): Promise<ElementValidationResult>;
    /**
     * Test different wait strategies for an element
     */
    testWaitStrategies(selector: SelectorInfo): Promise<Record<string, ElementValidationResult>>;
    /**
     * Try alternative selectors for an element
     */
    findAlternativeSelectors(currentSelector: SelectorInfo, context?: string): Promise<SelectorInfo[]>;
    /**
     * Take screenshot
     */
    takeScreenshot(filename: string): Promise<string>;
    /**
     * Get current page source
     */
    getPageSource(): Promise<string>;
    /**
     * Get current URL
     */
    getCurrentUrl(): Promise<string>;
    /**
     * Execute JavaScript
     */
    executeScript(script: string, ...args: any[]): Promise<any>;
    /**
     * Quit WebDriver
     */
    quit(): Promise<void>;
    /**
     * Convert SelectorInfo to Selenium By locator
     */
    private selectorToBy;
}
//# sourceMappingURL=browser-controller.d.ts.map