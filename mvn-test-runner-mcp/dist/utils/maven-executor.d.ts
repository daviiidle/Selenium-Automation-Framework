/**
 * Maven test execution utilities
 */
export declare class MavenExecutor {
    private frameworkRoot;
    constructor(frameworkRoot: string);
    /**
     * Execute mvn test command
     */
    runTests(options?: {
        testFilter?: string;
        parallel?: boolean;
        threadCount?: number;
        timeout?: number;
    }): Promise<{
        stdout: string;
        stderr: string;
        exitCode: number;
    }>;
    /**
     * Run specific test class
     */
    runTestClass(testClass: string, timeout?: number): Promise<{
        stdout: string;
        stderr: string;
        exitCode: number;
    }>;
    /**
     * Run specific test method
     */
    runTestMethod(testClass: string, testMethod: string, timeout?: number): Promise<{
        stdout: string;
        stderr: string;
        exitCode: number;
    }>;
    /**
     * Clean Maven project
     */
    clean(): Promise<void>;
    /**
     * Compile tests without running
     */
    compileTests(): Promise<{
        stdout: string;
        stderr: string;
    }>;
}
//# sourceMappingURL=maven-executor.d.ts.map