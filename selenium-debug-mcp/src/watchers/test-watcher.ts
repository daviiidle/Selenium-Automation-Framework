/**
 * Test Watcher
 * Monitors mvn test execution in real-time and detects failures
 */

import { spawn, ChildProcess } from 'child_process';
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

export class TestWatcher extends EventEmitter {
  private process: ChildProcess | null = null;
  private frameworkRoot: string;
  private outputBuffer: string = '';
  private currentTest: { class: string; method: string } | null = null;

  constructor(frameworkRoot: string) {
    super();
    this.frameworkRoot = frameworkRoot;
  }

  /**
   * Start watching mvn test execution
   */
  async startWatching(testFilter?: string): Promise<void> {
    const args = ['clean', 'test'];

    if (testFilter) {
      args.push(`-Dtest=${testFilter}`);
    }

    console.error(`Starting Maven test execution: mvn ${args.join(' ')}`);

    this.process = spawn('mvn', args, {
      cwd: this.frameworkRoot,
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    this.process.stdout?.on('data', (data) => {
      const output = data.toString();
      this.outputBuffer += output;
      this.parseOutput(output);
    });

    this.process.stderr?.on('data', (data) => {
      const output = data.toString();
      console.error('Maven stderr:', output);
    });

    this.process.on('close', (code) => {
      this.emit('complete', { exitCode: code });
      console.error(`Maven test execution completed with code ${code}`);
    });

    this.process.on('error', (error) => {
      this.emit('error', error);
      console.error('Maven execution error:', error);
    });
  }

  /**
   * Parse test output in real-time
   */
  private parseOutput(output: string): void {
    const lines = output.split('\n');

    for (const line of lines) {
      // Detect test start
      const testStartMatch = line.match(/Running\s+(.+)/);
      if (testStartMatch) {
        const testClass = testStartMatch[1].trim();
        this.emit('test-class-start', { testClass });
        console.error(`Test class started: ${testClass}`);
      }

      // Detect test method execution (from TestNG/TestListener logs)
      const testMethodMatch = line.match(/Test started:\s*(.+)\.(.+)/);
      if (testMethodMatch) {
        const testClass = testMethodMatch[1].trim();
        const testMethod = testMethodMatch[2].trim();
        this.currentTest = { class: testClass, method: testMethod };
        this.emit('test-start', { testClass, testMethod });
        console.error(`Test started: ${testClass}.${testMethod}`);
      }

      // Detect test failure
      const failureMatch = line.match(/Test failed:\s*(.+)\.(.+).*Error:\s*(.+)/);
      if (failureMatch) {
        const testClass = failureMatch[1].trim();
        const testMethod = failureMatch[2].trim();
        const errorMessage = failureMatch[3].trim();

        const failure: TestFailure = {
          testClass,
          testMethod,
          errorType: this.extractErrorType(errorMessage),
          errorMessage,
          stackTrace: '',
          timestamp: new Date(),
        };

        this.emit('test-fail', { testClass, testMethod, failure });
        console.error(`Test failed: ${testClass}.${testMethod} - ${errorMessage}`);
      }

      // Detect test pass
      const passMatch = line.match(/Test passed:\s*(.+)\.(.+)/);
      if (passMatch) {
        const testClass = passMatch[1].trim();
        const testMethod = passMatch[2].trim();
        this.emit('test-pass', { testClass, testMethod });
        console.error(`Test passed: ${testClass}.${testMethod}`);
      }

      // Detect test skip
      const skipMatch = line.match(/Test skipped:\s*(.+)\.(.+)/);
      if (skipMatch) {
        const testClass = skipMatch[1].trim();
        const testMethod = skipMatch[2].trim();
        this.emit('test-skip', { testClass, testMethod });
        console.error(`Test skipped: ${testClass}.${testMethod}`);
      }

      // Detect NoSuchElementException
      if (line.includes('NoSuchElementException') && this.currentTest) {
        const selectorMatch = line.match(/Unable to locate element:\s*(.+)/);
        if (selectorMatch) {
          this.emit('selector-not-found', {
            testClass: this.currentTest.class,
            testMethod: this.currentTest.method,
            selector: selectorMatch[1].trim(),
          });
        }
      }

      // Detect TimeoutException
      if (line.includes('TimeoutException') && this.currentTest) {
        this.emit('timeout-error', {
          testClass: this.currentTest.class,
          testMethod: this.currentTest.method,
          message: line.trim(),
        });
      }

      // Detect StaleElementReferenceException
      if (line.includes('StaleElementReferenceException') && this.currentTest) {
        this.emit('stale-element', {
          testClass: this.currentTest.class,
          testMethod: this.currentTest.method,
        });
      }
    }
  }

  /**
   * Extract error type from error message
   */
  private extractErrorType(errorMessage: string): string {
    const exceptionMatch = errorMessage.match(/([A-Z]\w+Exception)/);
    return exceptionMatch ? exceptionMatch[1] : 'Unknown';
  }

  /**
   * Stop watching
   */
  stop(): void {
    if (this.process) {
      this.process.kill();
      this.process = null;
      console.error('Stopped Maven test watcher');
    }
  }

  /**
   * Get full output buffer
   */
  getOutput(): string {
    return this.outputBuffer;
  }

  /**
   * Wait for execution to complete
   */
  async waitForCompletion(): Promise<number> {
    return new Promise((resolve) => {
      if (!this.process) {
        resolve(-1);
        return;
      }

      this.process.on('close', (code) => {
        resolve(code || 0);
      });
    });
  }
}
