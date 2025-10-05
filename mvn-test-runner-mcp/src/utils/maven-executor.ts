/**
 * Maven test execution utilities
 */

import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

export class MavenExecutor {
  private frameworkRoot: string;

  constructor(frameworkRoot: string) {
    this.frameworkRoot = frameworkRoot;
  }

  /**
   * Execute mvn test command
   */
  async runTests(options: {
    testFilter?: string;
    parallel?: boolean;
    threadCount?: number;
    timeout?: number;
  } = {}): Promise<{ stdout: string; stderr: string; exitCode: number }> {
    let command = 'mvn clean test';

    // Add test filter if specified
    if (options.testFilter) {
      command += ` -Dtest=${options.testFilter}`;
    }

    // Add parallel execution if specified
    if (options.parallel) {
      const threads = options.threadCount || 4;
      command += ` -Dparallel=methods -DthreadCount=${threads}`;
    }

    // Execute command
    try {
      const { stdout, stderr } = await execAsync(command, {
        cwd: this.frameworkRoot,
        maxBuffer: 10 * 1024 * 1024, // 10MB buffer
        timeout: options.timeout || 600000, // 10 minutes default
      });

      return { stdout, stderr, exitCode: 0 };
    } catch (error: any) {
      // Maven returns non-zero exit code on test failures, but we still want the output
      return {
        stdout: error.stdout || '',
        stderr: error.stderr || '',
        exitCode: error.code || 1,
      };
    }
  }

  /**
   * Run specific test class
   */
  async runTestClass(testClass: string, timeout?: number): Promise<{ stdout: string; stderr: string; exitCode: number }> {
    return this.runTests({ testFilter: testClass, timeout });
  }

  /**
   * Run specific test method
   */
  async runTestMethod(testClass: string, testMethod: string, timeout?: number): Promise<{ stdout: string; stderr: string; exitCode: number }> {
    return this.runTests({ testFilter: `${testClass}#${testMethod}`, timeout });
  }

  /**
   * Clean Maven project
   */
  async clean(): Promise<void> {
    await execAsync('mvn clean', { cwd: this.frameworkRoot });
  }

  /**
   * Compile tests without running
   */
  async compileTests(): Promise<{ stdout: string; stderr: string }> {
    const { stdout, stderr } = await execAsync('mvn test-compile', {
      cwd: this.frameworkRoot,
      maxBuffer: 10 * 1024 * 1024,
    });
    return { stdout, stderr };
  }
}
