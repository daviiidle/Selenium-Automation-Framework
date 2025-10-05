/**
 * Intelligent Selector Finder
 * Discovers working selectors on live pages using multiple strategies
 */
import { BrowserController, SelectorInfo, ElementValidationResult } from '../selenium/browser-controller.js';
export interface SelectorCandidate {
    selector: SelectorInfo;
    score: number;
    validationResult: ElementValidationResult;
    stability: 'high' | 'medium' | 'low';
    reason: string;
}
export declare class SelectorFinder {
    private browser;
    constructor(browser: BrowserController);
    /**
     * Find the best working selector for an element
     */
    findBestSelector(currentSelector: SelectorInfo, pageUrl: string): Promise<SelectorCandidate | null>;
    /**
     * Generate candidate selectors using multiple strategies
     */
    private generateCandidates;
    /**
     * Extract alternative selectors from CSS selector
     */
    private extractFromCssSelector;
    /**
     * Generate common patterns for form elements
     */
    private generateCommonPatterns;
    /**
     * Generate variations of the original selector
     */
    private generateVariations;
    /**
     * Assess stability of a selector
     */
    private assessStability;
}
//# sourceMappingURL=selector-finder.d.ts.map