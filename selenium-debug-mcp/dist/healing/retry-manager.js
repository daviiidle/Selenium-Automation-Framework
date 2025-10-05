/**
 * Retry Manager
 * Manages test retry execution after fixes are applied
 */
import { spawn } from 'child_process';
export class RetryManager {
    frameworkRoot;
    retryQueue;
    constructor(frameworkRoot) {
        this.frameworkRoot = frameworkRoot;
        this.retryQueue = new Set();
    }
    /**
     * Add test to retry queue
     */
    addToRetryQueue(testClass, testMethod) {
        const testName = `${testClass}#${testMethod}`;
        this.retryQueue.add(testName);
        console.error(`Added to retry queue: ${testName}`);
    }
    /**
     * Get current retry queue
     */
    getRetryQueue() {
        return Array.from(this.retryQueue);
    }
    /**
     * Clear retry queue
     */
    clearRetryQueue() {
        this.retryQueue.clear();
    }
    /**
     * Retry a single specific test
     */
    async retrySingleTest(testClass, testMethod) {
        const testName = `${testClass}#${testMethod}`;
        const startTime = Date.now();
        console.error(`Retrying test: ${testName}`);
        try {
            const result = await this.executeTest(testName);
            const duration = Date.now() - startTime;
            return {
                testName,
                passed: result.exitCode === 0,
                duration,
                output: result.output,
                error: result.exitCode !== 0 ? 'Test failed after retry' : undefined,
            };
        }
        catch (error) {
            return {
                testName,
                passed: false,
                duration: Date.now() - startTime,
                output: '',
                error: error instanceof Error ? error.message : String(error),
            };
        }
    }
    /**
     * Retry all tests in the queue
     */
    async retryAllQueued() {
        const results = [];
        const testsToRetry = Array.from(this.retryQueue);
        if (testsToRetry.length === 0) {
            console.error('Retry queue is empty');
            return results;
        }
        console.error(`Retrying ${testsToRetry.length} tests...`);
        // Retry tests in batch for efficiency
        const testFilter = testsToRetry.join(',');
        const startTime = Date.now();
        try {
            const result = await this.executeTest(testFilter);
            const duration = Date.now() - startTime;
            // Parse output to determine individual test results
            const individualResults = this.parseTestResults(result.output, testsToRetry);
            results.push(...individualResults);
        }
        catch (error) {
            // If batch retry fails, try individually
            console.error('Batch retry failed, trying individually...');
            for (const testName of testsToRetry) {
                const [testClass, testMethod] = testName.split('#');
                const result = await this.retrySingleTest(testClass, testMethod);
                results.push(result);
            }
        }
        // Remove successful tests from queue
        for (const result of results) {
            if (result.passed) {
                this.retryQueue.delete(result.testName);
            }
        }
        return results;
    }
    /**
     * Execute Maven test
     */
    executeTest(testFilter) {
        return new Promise((resolve, reject) => {
            const args = ['test', `-Dtest=${testFilter}`, '-DfailIfNoTests=false'];
            console.error(`Executing: mvn ${args.join(' ')}`);
            const mvn = spawn('mvn', args, {
                cwd: this.frameworkRoot,
                stdio: ['pipe', 'pipe', 'pipe'],
            });
            let output = '';
            let errorOutput = '';
            mvn.stdout.on('data', (data) => {
                output += data.toString();
            });
            mvn.stderr.on('data', (data) => {
                errorOutput += data.toString();
            });
            mvn.on('close', (code) => {
                resolve({
                    exitCode: code || 0,
                    output: output + errorOutput,
                });
            });
            mvn.on('error', (error) => {
                reject(error);
            });
        });
    }
    /**
     * Parse test results from Maven output
     */
    parseTestResults(output, expectedTests) {
        const results = [];
        for (const testName of expectedTests) {
            const [testClass, testMethod] = testName.split('#');
            // Check for test pass
            const passPattern = new RegExp(`Test passed:\\s*${testClass.replace(/\./g, '\\.')}\.${testMethod}`);
            const passed = passPattern.test(output);
            // Check for test fail
            const failPattern = new RegExp(`Test failed:\\s*${testClass.replace(/\./g, '\\.')}\.${testMethod}.*Error:\\s*(.+)`);
            const failMatch = output.match(failPattern);
            results.push({
                testName,
                passed,
                duration: 0, // Would need to parse from output
                output: '',
                error: failMatch ? failMatch[1] : undefined,
            });
        }
        return results;
    }
    /**
     * Get retry statistics
     */
    getStats() {
        return {
            queueSize: this.retryQueue.size,
            tests: Array.from(this.retryQueue),
        };
    }
}
//# sourceMappingURL=retry-manager.js.map