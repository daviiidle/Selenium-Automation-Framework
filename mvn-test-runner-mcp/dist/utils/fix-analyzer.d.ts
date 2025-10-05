/**
 * Analyze test failures and suggest fixes
 */
import { TestFailure, FixSuggestion } from '../types/test-results.js';
export declare class FixAnalyzer {
    /**
     * Analyze failures and generate fix suggestions
     */
    analyzeFailures(failures: TestFailure[]): FixSuggestion[];
    /**
     * Analyze single failure
     */
    private analyzeFailure;
    /**
     * Analyze selector-related failures
     */
    private analyzeSelectorFailure;
    /**
     * Analyze timeout failures
     */
    private analyzeTimeoutFailure;
    /**
     * Analyze assertion failures
     */
    private analyzeAssertionFailure;
    /**
     * Analyze configuration failures
     */
    private analyzeConfigurationFailure;
    /**
     * Analyze unknown failures
     */
    private analyzeUnknownFailure;
    /**
     * Group failures by category
     */
    groupFailuresByCategory(failures: TestFailure[]): Map<string, TestFailure[]>;
    /**
     * Identify patterns across failures
     */
    identifyPatterns(failures: TestFailure[]): string[];
}
//# sourceMappingURL=fix-analyzer.d.ts.map