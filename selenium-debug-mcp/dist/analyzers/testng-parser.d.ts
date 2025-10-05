/**
 * TestNG and Surefire Report Parser
 * Analyzes test results and failures
 */
export interface TestResult {
    testClass: string;
    testMethod: string;
    status: 'PASS' | 'FAIL' | 'SKIP';
    duration: number;
    errorMessage?: string;
    stackTrace?: string;
    errorType?: string;
}
export interface TestSuiteResult {
    name: string;
    tests: number;
    failures: number;
    skipped: number;
    time: number;
    testResults: TestResult[];
}
export interface FailurePattern {
    pattern: string;
    count: number;
    affectedTests: string[];
    category: 'selector' | 'timeout' | 'stale-element' | 'network' | 'assertion' | 'unknown';
}
export declare class TestNGParser {
    private frameworkRoot;
    private parser;
    constructor(frameworkRoot: string);
    /**
     * Parse TestNG results XML
     */
    parseTestNGResults(resultsFile?: string): Promise<TestSuiteResult>;
    /**
     * Get failed tests only
     */
    getFailedTests(): Promise<TestResult[]>;
    /**
     * Analyze failure patterns
     */
    analyzeFailurePatterns(): Promise<FailurePattern[]>;
    /**
     * Categorize error type
     */
    private categorizeError;
    /**
     * Extract error pattern for grouping
     */
    private extractErrorPattern;
    /**
     * Get test execution summary
     */
    getSummary(): Promise<{
        total: number;
        passed: number;
        failed: number;
        skipped: number;
        passRate: number;
        totalTime: number;
    }>;
    /**
     * Check if test results exist
     */
    hasTestResults(): Promise<boolean>;
}
//# sourceMappingURL=testng-parser.d.ts.map