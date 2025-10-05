/**
 * Type definitions for Maven test execution results
 */

export interface TestFailure {
  testClass: string;
  testMethod: string;
  errorType: string;
  errorMessage: string;
  stackTrace: string;
  executionTime: number;
  category: 'ASSERTION' | 'SELECTOR' | 'TIMEOUT' | 'CONFIGURATION' | 'UNKNOWN';
}

export interface TestSuccess {
  testClass: string;
  testMethod: string;
  executionTime: number;
}

export interface TestSkipped {
  testClass: string;
  testMethod: string;
  reason?: string;
}

export interface TestSummary {
  totalTests: number;
  passed: number;
  failed: number;
  skipped: number;
  executionTime: number;
  timestamp: string;
}

export interface MavenTestResult {
  summary: TestSummary;
  failures: TestFailure[];
  successes: TestSuccess[];
  skipped: TestSkipped[];
  rawOutput: string;
}

export interface FixSuggestion {
  testClass: string;
  testMethod: string;
  issue: string;
  suggestedFix: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  autoFixable: boolean;
}
