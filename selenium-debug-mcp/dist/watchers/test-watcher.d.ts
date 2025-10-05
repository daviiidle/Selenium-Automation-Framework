/**
 * Test Watcher
 * Monitors mvn test execution in real-time and detects failures
 */
import { EventEmitter } from 'events';
export interface TestFailure {
    testClass: string;
    testMethod: string;
    errorType: string;
    errorMessage: string;
    stackTrace: string;
    timestamp: Date;
}
export interface TestEvent {
    type: 'start' | 'pass' | 'fail' | 'skip' | 'complete';
    testClass?: string;
    testMethod?: string;
    failure?: TestFailure;
}
export declare class TestWatcher extends EventEmitter {
    private process;
    private frameworkRoot;
    private outputBuffer;
    private currentTest;
    constructor(frameworkRoot: string);
    /**
     * Start watching mvn test execution
     */
    startWatching(testFilter?: string): Promise<void>;
    /**
     * Parse test output in real-time
     */
    private parseOutput;
    /**
     * Extract error type from error message
     */
    private extractErrorType;
    /**
     * Stop watching
     */
    stop(): void;
    /**
     * Get full output buffer
     */
    getOutput(): string;
    /**
     * Wait for execution to complete
     */
    waitForCompletion(): Promise<number>;
}
//# sourceMappingURL=test-watcher.d.ts.map