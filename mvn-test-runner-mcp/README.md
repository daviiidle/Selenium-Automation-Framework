# Maven Test Runner MCP Server

A specialized Model Context Protocol (MCP) server for running Maven tests and extracting critical failure details for automated fixing.

## Features

- **Execute Maven Tests**: Run all tests, specific test classes, or individual test methods
- **Parse Test Results**: Extract detailed failure information from Surefire reports
- **Categorize Failures**: Automatically categorize failures (SELECTOR, TIMEOUT, ASSERTION, CONFIGURATION, UNKNOWN)
- **Fix Suggestions**: Generate intelligent fix suggestions based on failure patterns
- **Pattern Analysis**: Identify common failure patterns across test suite
- **Integration Ready**: Works seamlessly with selenium-debug MCP for automated healing

## Available Tools

### 1. `run-all-tests`
Execute all Maven tests and extract comprehensive failure details.

**Parameters:**
- `parallel` (boolean): Run tests in parallel
- `threadCount` (number): Number of parallel threads (default: 4)
- `timeout` (number): Test execution timeout in milliseconds (default: 600000)

**Returns:**
- Test summary (passed, failed, skipped counts)
- Detailed failure information
- Fix suggestions for each failure
- Identified patterns across failures

### 2. `run-test-class`
Run specific test class and get detailed results.

**Parameters:**
- `testClass` (string, required): Test class name (e.g., "ComprehensiveLoginTests")
- `timeout` (number): Timeout in milliseconds (default: 300000)

**Returns:**
- Test class execution results
- Failures with detailed stack traces
- Fix suggestions

### 3. `run-test-method`
Run specific test method and get detailed results.

**Parameters:**
- `testClass` (string, required): Test class name
- `testMethod` (string, required): Test method name
- `timeout` (number): Timeout in milliseconds (default: 120000)

**Returns:**
- Individual test method results
- Failure details if test failed
- Specific fix suggestions

### 4. `get-failure-summary`
Get comprehensive summary of all test failures with fix suggestions.

**Returns:**
- Overall test summary
- Failures grouped by category (SELECTOR, TIMEOUT, etc.)
- Common patterns identified
- High-priority fixes

### 5. `analyze-failure`
Analyze specific test failure and get detailed fix suggestions.

**Parameters:**
- `testClass` (string, required): Test class name
- `testMethod` (string, required): Test method name

**Returns:**
- Detailed failure information
- Categorized error type
- Comprehensive fix suggestions
- Related MCP tools for automated fixing

### 6. `compile-tests`
Compile tests without running them.

**Returns:**
- Compilation success status
- Output and error messages

### 7. `get-test-patterns`
Identify common failure patterns across all tests.

**Returns:**
- Total failure count
- Identified patterns
- Category breakdown
- Strategic recommendations

## Failure Categories

The server automatically categorizes failures into:

- **SELECTOR**: Element not found, NoSuchElementException
- **TIMEOUT**: Wait timeouts, element not visible in time
- **ASSERTION**: Test assertions failed (expected vs actual mismatch)
- **CONFIGURATION**: TestNG configuration errors, BeforeMethod/AfterMethod issues
- **UNKNOWN**: Uncategorized errors requiring manual investigation

## Fix Suggestions

For each failure, the server provides:

- **Issue Description**: Clear explanation of what went wrong
- **Suggested Fix**: Step-by-step fix instructions
- **Priority**: HIGH, MEDIUM, or LOW
- **Auto-Fixable**: Boolean indicating if automated fixing is possible
- **Related Tools**: List of selenium-debug MCP tools that can help fix the issue

## Installation

```bash
cd mvn-test-runner-mcp
npm install
npm run build
```

## Configuration

Add to `.mcp.json`:

```json
{
  "mcpServers": {
    "mvn-test-runner": {
      "command": "node",
      "args": ["mvn-test-runner-mcp/dist/index.js"]
    }
  }
}
```

Set `FRAMEWORK_ROOT` environment variable (optional):
```bash
export FRAMEWORK_ROOT=/path/to/selenium/framework
```

## Usage Examples

### Run All Tests
```javascript
{
  "tool": "run-all-tests",
  "args": {
    "parallel": true,
    "threadCount": 4
  }
}
```

### Analyze Specific Failure
```javascript
{
  "tool": "analyze-failure",
  "args": {
    "testClass": "tests.authentication.ComprehensiveLoginTests",
    "testMethod": "testValidLogin"
  }
}
```

### Get Failure Patterns
```javascript
{
  "tool": "get-test-patterns",
  "args": {}
}
```

## Integration with Selenium Debug MCP

This server works seamlessly with `selenium-debug` MCP:

1. Run `run-all-tests` to identify failures
2. Use suggested tools from `selenium-debug` to fix issues:
   - `validate-selector-live` for SELECTOR failures
   - `test-wait-strategies` for TIMEOUT failures
   - `update-page-object-selector` for automated healing
3. Re-run tests to verify fixes

## Architecture

```
mvn-test-runner-mcp/
├── src/
│   ├── index.ts                 # MCP server entry point
│   ├── tools/
│   │   └── maven-test-tools.ts  # Test execution tools
│   ├── utils/
│   │   ├── maven-executor.ts    # Maven command execution
│   │   ├── test-parser.ts       # Surefire XML parsing
│   │   └── fix-analyzer.ts      # Failure analysis & suggestions
│   └── types/
│       └── test-results.ts      # TypeScript type definitions
├── package.json
├── tsconfig.json
└── README.md
```

## Technologies

- **TypeScript**: Type-safe development
- **@modelcontextprotocol/sdk**: MCP server implementation
- **fast-xml-parser**: Parse Surefire XML reports
- **Node.js child_process**: Execute Maven commands

## License

MIT
