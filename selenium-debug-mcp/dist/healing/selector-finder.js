/**
 * Intelligent Selector Finder
 * Discovers working selectors on live pages using multiple strategies
 */
export class SelectorFinder {
    browser;
    constructor(browser) {
        this.browser = browser;
    }
    /**
     * Find the best working selector for an element
     */
    async findBestSelector(currentSelector, pageUrl) {
        await this.browser.navigateTo(pageUrl);
        // Try current selector first
        const currentResult = await this.browser.validateElement(currentSelector, 5000);
        if (currentResult.found) {
            return {
                selector: currentSelector,
                score: 100,
                validationResult: currentResult,
                stability: this.assessStability(currentSelector),
                reason: 'Current selector works',
            };
        }
        // Current selector failed, find alternatives
        const candidates = await this.generateCandidates(currentSelector);
        const validatedCandidates = [];
        for (const candidate of candidates) {
            const result = await this.browser.validateElement(candidate.selector, 3000);
            if (result.found) {
                validatedCandidates.push({
                    selector: candidate.selector,
                    score: candidate.score,
                    validationResult: result,
                    stability: candidate.stability,
                    reason: candidate.reason,
                });
            }
        }
        if (validatedCandidates.length === 0) {
            return null;
        }
        // Sort by score (stability + speed)
        validatedCandidates.sort((a, b) => b.score - a.score);
        return validatedCandidates[0];
    }
    /**
     * Generate candidate selectors using multiple strategies
     */
    async generateCandidates(originalSelector) {
        const candidates = [];
        // Strategy 1: Extract from original selector
        if (originalSelector.type === 'css') {
            candidates.push(...this.extractFromCssSelector(originalSelector.value));
        }
        // Strategy 2: Common patterns based on selector value
        candidates.push(...this.generateCommonPatterns(originalSelector.value));
        // Strategy 3: Try variations
        candidates.push(...this.generateVariations(originalSelector));
        return candidates;
    }
    /**
     * Extract alternative selectors from CSS selector
     */
    extractFromCssSelector(cssSelector) {
        const candidates = [];
        // Extract ID if present
        const idMatch = cssSelector.match(/#([\w-]+)/);
        if (idMatch) {
            candidates.push({
                selector: { type: 'id', value: idMatch[1] },
                score: 95,
                validationResult: {},
                stability: 'high',
                reason: 'ID selector is most stable',
            });
        }
        // Extract class names
        const classMatch = cssSelector.match(/\.([\w-]+)/g);
        if (classMatch && classMatch.length > 0) {
            const className = classMatch[0].substring(1);
            candidates.push({
                selector: { type: 'css', value: `.${className}` },
                score: 70,
                validationResult: {},
                stability: 'medium',
                reason: 'Simple class selector',
            });
        }
        // Extract tag name
        const tagMatch = cssSelector.match(/^([a-z]+)/);
        if (tagMatch) {
            const tagName = tagMatch[1];
            // Try tag with specific attributes
            if (cssSelector.includes('[name=')) {
                const nameMatch = cssSelector.match(/\[name=["']?([^"'\]]+)["']?\]/);
                if (nameMatch) {
                    candidates.push({
                        selector: { type: 'name', value: nameMatch[1] },
                        score: 90,
                        validationResult: {},
                        stability: 'high',
                        reason: 'Name attribute is stable',
                    });
                    candidates.push({
                        selector: { type: 'css', value: `${tagName}[name="${nameMatch[1]}"]` },
                        score: 88,
                        validationResult: {},
                        stability: 'high',
                        reason: 'Tag with name attribute',
                    });
                }
            }
            if (cssSelector.includes('[type=')) {
                const typeMatch = cssSelector.match(/\[type=["']?([^"'\]]+)["']?\]/);
                if (typeMatch) {
                    candidates.push({
                        selector: { type: 'css', value: `${tagName}[type="${typeMatch[1]}"]` },
                        score: 75,
                        validationResult: {},
                        stability: 'medium',
                        reason: 'Tag with type attribute',
                    });
                }
            }
        }
        return candidates;
    }
    /**
     * Generate common patterns for form elements
     */
    generateCommonPatterns(originalValue) {
        const candidates = [];
        const lowerValue = originalValue.toLowerCase();
        // Email input patterns
        if (lowerValue.includes('email')) {
            candidates.push({
                selector: { type: 'name', value: 'Email' },
                score: 92,
                validationResult: {},
                stability: 'high',
                reason: 'Common email field name',
            }, {
                selector: { type: 'css', value: 'input[name="Email"]' },
                score: 91,
                validationResult: {},
                stability: 'high',
                reason: 'Email input by name',
            }, {
                selector: { type: 'css', value: 'input[type="email"]' },
                score: 85,
                validationResult: {},
                stability: 'medium',
                reason: 'Email input by type',
            }, {
                selector: { type: 'id', value: 'Email' },
                score: 93,
                validationResult: {},
                stability: 'high',
                reason: 'Email input by ID',
            });
        }
        // Password input patterns
        if (lowerValue.includes('password')) {
            candidates.push({
                selector: { type: 'name', value: 'Password' },
                score: 92,
                validationResult: {},
                stability: 'high',
                reason: 'Common password field name',
            }, {
                selector: { type: 'css', value: 'input[type="password"]' },
                score: 90,
                validationResult: {},
                stability: 'high',
                reason: 'Password input by type',
            });
        }
        // Button patterns
        if (lowerValue.includes('button') || lowerValue.includes('submit')) {
            candidates.push({
                selector: { type: 'css', value: 'button[type="submit"]' },
                score: 85,
                validationResult: {},
                stability: 'medium',
                reason: 'Submit button',
            }, {
                selector: { type: 'css', value: 'input[type="submit"]' },
                score: 84,
                validationResult: {},
                stability: 'medium',
                reason: 'Submit input',
            });
        }
        return candidates;
    }
    /**
     * Generate variations of the original selector
     */
    generateVariations(originalSelector) {
        const candidates = [];
        if (originalSelector.type === 'css') {
            const value = originalSelector.value;
            // Remove nth-child if present (brittle)
            if (value.includes(':nth-child')) {
                const withoutNth = value.replace(/:nth-child\(\d+\)/g, '');
                if (withoutNth !== value) {
                    candidates.push({
                        selector: { type: 'css', value: withoutNth },
                        score: 60,
                        validationResult: {},
                        stability: 'medium',
                        reason: 'Removed brittle nth-child',
                    });
                }
            }
            // Simplify deep selectors
            if (value.includes(' > ')) {
                const parts = value.split(' > ');
                if (parts.length > 2) {
                    const simplified = parts.slice(-2).join(' > ');
                    candidates.push({
                        selector: { type: 'css', value: simplified },
                        score: 65,
                        validationResult: {},
                        stability: 'medium',
                        reason: 'Simplified deep selector',
                    });
                }
            }
        }
        return candidates;
    }
    /**
     * Assess stability of a selector
     */
    assessStability(selector) {
        const value = selector.value;
        // ID and name selectors are most stable
        if (selector.type === 'id' || selector.type === 'name') {
            return 'high';
        }
        // CSS with name/id attributes are stable
        if (value.includes('[name=') || value.includes('[id=')) {
            return 'high';
        }
        // Positional selectors are fragile
        if (value.includes(':nth-child') || value.includes(':nth-of-type')) {
            return 'low';
        }
        // Deep nesting is fragile
        if (value.split(' > ').length > 4) {
            return 'low';
        }
        // Class-based selectors are moderately stable
        if (value.startsWith('.') || value.includes('[class=')) {
            return 'medium';
        }
        return 'medium';
    }
}
//# sourceMappingURL=selector-finder.js.map