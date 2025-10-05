/**
 * Analyze test failures and suggest fixes
 */
export class FixAnalyzer {
    /**
     * Analyze failures and generate fix suggestions
     */
    analyzeFailures(failures) {
        return failures.map(failure => this.analyzeFailure(failure));
    }
    /**
     * Analyze single failure
     */
    analyzeFailure(failure) {
        switch (failure.category) {
            case 'SELECTOR':
                return this.analyzeSelectorFailure(failure);
            case 'TIMEOUT':
                return this.analyzeTimeoutFailure(failure);
            case 'ASSERTION':
                return this.analyzeAssertionFailure(failure);
            case 'CONFIGURATION':
                return this.analyzeConfigurationFailure(failure);
            default:
                return this.analyzeUnknownFailure(failure);
        }
    }
    /**
     * Analyze selector-related failures
     */
    analyzeSelectorFailure(failure) {
        const elementMatch = failure.errorMessage.match(/Unable to locate element.*?:(.*?)(?:\n|$)/);
        const selector = elementMatch ? elementMatch[1].trim() : 'unknown';
        return {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            issue: `Element not found: ${selector}`,
            suggestedFix: `
1. Verify the selector is correct on the live page
2. Check if element is inside iframe or shadow DOM
3. Add explicit wait for element visibility
4. Update selector in page object or selector JSON file

Recommended tools:
- validate-selector-live: Test selector on live page
- find-alternative-selectors: Find working alternatives
- update-page-object-selector: Update Page Object selector
- update-selector-file: Update JSON selector file
      `.trim(),
            priority: 'HIGH',
            autoFixable: true,
        };
    }
    /**
     * Analyze timeout failures
     */
    analyzeTimeoutFailure(failure) {
        const timeoutMatch = failure.errorMessage.match(/timeout.*?(\d+)/i);
        const currentTimeout = timeoutMatch ? parseInt(timeoutMatch[1]) : 10;
        return {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            issue: `Timeout waiting for element (current: ${currentTimeout}s)`,
            suggestedFix: `
1. Increase wait timeout (recommend ${currentTimeout + 10}s)
2. Add explicit wait conditions
3. Check if page load is slow
4. Verify network conditions

Recommended tools:
- test-wait-strategies: Test different wait approaches
- update-wait-time: Update wait timeout in code
      `.trim(),
            priority: 'MEDIUM',
            autoFixable: true,
        };
    }
    /**
     * Analyze assertion failures
     */
    analyzeAssertionFailure(failure) {
        const expectedMatch = failure.errorMessage.match(/expected:?\s*\[(.*?)\]/i);
        const actualMatch = failure.errorMessage.match(/but was:?\s*\[(.*?)\]/i);
        const expected = expectedMatch ? expectedMatch[1] : 'unknown';
        const actual = actualMatch ? actualMatch[1] : 'unknown';
        return {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            issue: `Assertion failed - Expected: [${expected}], Actual: [${actual}]`,
            suggestedFix: `
1. Verify expected value is correct for current application state
2. Check if application behavior changed
3. Update assertion to match current behavior
4. Add additional validation steps

This requires manual review to determine if:
- Test expectation needs update
- Application has regression
      `.trim(),
            priority: 'HIGH',
            autoFixable: false,
        };
    }
    /**
     * Analyze configuration failures
     */
    analyzeConfigurationFailure(failure) {
        return {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            issue: 'Test configuration error (BeforeMethod/AfterMethod)',
            suggestedFix: `
1. Check WebDriver initialization in BaseTest
2. Verify TestNG configuration (testng.xml)
3. Check dependencies in pom.xml
4. Review test class annotations

Recommended tools:
- check-testng-config: Validate TestNG XML
      `.trim(),
            priority: 'HIGH',
            autoFixable: false,
        };
    }
    /**
     * Analyze unknown failures
     */
    analyzeUnknownFailure(failure) {
        return {
            testClass: failure.testClass,
            testMethod: failure.testMethod,
            issue: `Unknown error: ${failure.errorType}`,
            suggestedFix: `
Full error: ${failure.errorMessage}

Manual investigation required:
1. Review full stack trace
2. Check application logs
3. Verify test environment
4. Review recent code changes
      `.trim(),
            priority: 'MEDIUM',
            autoFixable: false,
        };
    }
    /**
     * Group failures by category
     */
    groupFailuresByCategory(failures) {
        const grouped = new Map();
        for (const failure of failures) {
            const category = failure.category;
            if (!grouped.has(category)) {
                grouped.set(category, []);
            }
            grouped.get(category).push(failure);
        }
        return grouped;
    }
    /**
     * Identify patterns across failures
     */
    identifyPatterns(failures) {
        const patterns = [];
        const errorCounts = new Map();
        // Count error types
        for (const failure of failures) {
            const count = errorCounts.get(failure.errorType) || 0;
            errorCounts.set(failure.errorType, count + 1);
        }
        // Identify common patterns
        for (const [errorType, count] of errorCounts) {
            if (count > 1) {
                patterns.push(`${errorType} occurs ${count} times`);
            }
        }
        // Check for selector patterns
        const selectorFailures = failures.filter(f => f.category === 'SELECTOR');
        if (selectorFailures.length > 3) {
            patterns.push(`Multiple selector failures (${selectorFailures.length}) - may indicate page structure changes`);
        }
        // Check for timeout patterns
        const timeoutFailures = failures.filter(f => f.category === 'TIMEOUT');
        if (timeoutFailures.length > 2) {
            patterns.push(`Multiple timeout failures (${timeoutFailures.length}) - may indicate performance issues`);
        }
        return patterns;
    }
}
//# sourceMappingURL=fix-analyzer.js.map