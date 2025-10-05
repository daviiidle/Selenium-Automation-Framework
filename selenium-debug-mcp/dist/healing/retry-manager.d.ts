/**
 * Retry Manager
 * Manages test retry execution after fixes are applied
 */
export interface RetryResult {
    testName: string;
    passed: boolean;
    duration: number;
    error?: string;
    output: string;
}
export declare class RetryManager {
    private frameworkRoot;
    private retryQueue;
    constructor(frameworkRoot: string);
    /**
     * Add test to retry queue
     */
    addToRetryQueue(testClass: string, testMethod: string): void;
    /**
     * Get current retry queue
     */
    getRetryQueue(): string[];
    /**
     * Clear retry queue
     */
    clearRetryQueue(): void;
    /**
     * Retry a single specific test
     */
    retrySingleTest(testClass: string, testMethod: string): Promise<RetryResult>;
    /**
     * Retry all tests in the queue
     */
    retryAllQueued(): Promise<RetryResult[]>;
    /**
     * Execute Maven test
     */
    private executeTest;
    /**
     * Parse test results from Maven output
     */
    private parseTestResults;
    /**
     * Get retry statistics
     */
    getStats(): {
        queueSize: number;
        tests: string[];
    };
}
//# sourceMappingURL=retry-manager.d.ts.map