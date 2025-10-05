/**
 * Maven test output parser
 * Extracts critical test information from mvn test output
 */
import { MavenTestResult } from '../types/test-results.js';
export declare class TestParser {
    private frameworkRoot;
    constructor(frameworkRoot: string);
    /**
     * Parse Maven test output and extract all important details
     */
    parseTestResults(rawOutput: string): Promise<MavenTestResult>;
    /**
     * Extract test summary from Maven output
     */
    private extractSummary;
    /**
     * Extract test failures from surefire reports
     */
    private extractFailures;
    /**
     * Parse XML surefire report
     */
    private parseXmlReport;
    /**
     * Extract failures from raw output as fallback
     */
    private extractFailuresFromOutput;
    /**
     * Extract successful tests
     */
    private extractSuccesses;
    /**
     * Extract skipped tests
     */
    private extractSkipped;
    /**
     * Categorize error type
     */
    private categorizeError;
    /**
     * Extract error type from message
     */
    private extractErrorType;
    /**
     * Parse execution time string to seconds
     */
    private parseExecutionTime;
}
//# sourceMappingURL=test-parser.d.ts.map