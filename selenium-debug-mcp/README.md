# Selenium Debug MCP Server

An intelligent **Model Context Protocol (MCP)** server for debugging and analyzing Java Selenium test frameworks. This server provides live test execution, selector validation, and **AI-driven self-healing** through Claude Code.

## Features

### 🤖 AI-Driven Self-Healing (NEW!)
- **Continuous test monitoring** while tests run
- **Parallel healing** - fixes applied while tests continue
- **Intelligent selector discovery** using live page analysis
- **Automatic code updates** with backup protection
- **Retry queue management** for fixed tests
- **Claude-powered decisions** for optimal fixes

### 🔴 Live Test Execution
- **Validate selectors** on the live DemoWebShop website
- **Test wait strategies** to find optimal timing
- **Find alternative selectors** when current ones fail
- **Navigate page flows** and capture bottlenecks
- **Validate entire selector JSON files** on live pages

### 📊 Static Analysis
- **Analyze test failures** from TestNG/Surefire reports
- **Identify failure patterns** across multiple tests
- **Inspect Page Objects** and extract selectors
- **Inspect test classes** and extract test methods
- **Validate TestNG configuration** for issues

### 🔧 Automated Fix Application
- **Update selector JSON files** automatically
- **Modify Java Page Objects** with backup
- **Adjust wait times** based on actual page behavior
- **TestNG configuration tuning** for stability
- **File backup and restore** capabilities

## Installation

### Prerequisites
- **Node.js 18+** (for MCP server)
- **Chrome/Chromium** (for live testing)
- **Java 21** (your Selenium framework)
- **Claude Code** (or any MCP-compatible client)

### Setup

1. **Navigate to the MCP server directory**:
```bash
cd selenium-debug-mcp
```

2. **Install dependencies**:
```bash
npm install
```

3. **Build the TypeScript project**:
```bash
npm run build
```

4. **Configure environment** (optional):
```bash
cp .env.example .env
# Edit .env with your settings
```

## Claude Code Integration

### Configure MCP Server in Claude Code

Add this configuration to your Claude Code MCP settings:

**For global configuration** (`~/.config/claude-code/mcp.json` or OS equivalent):
```json
{
  "mcpServers": {
    "selenium-debug": {
      "command": "node",
      "args": [
        "/mnt/c/Users/D/Java Selenium framework/selenium-debug-mcp/dist/index.js"
      ],
      "env": {
        "FRAMEWORK_ROOT": "/mnt/c/Users/D/Java Selenium framework"
      }
    }
  }
}
```

**For project-specific configuration** (`.claude/mcp-config.json` in your framework):
```json
{
  "mcpServers": {
    "selenium-debug": {
      "command": "node",
      "args": ["./selenium-debug-mcp/dist/index.js"]
    }
  }
}
```

### Restart Claude Code

After adding the configuration, restart Claude Code to load the MCP server.

## Usage

Once configured, you can interact with the MCP server through Claude Code in two ways:

### 1. Direct Tool Calls (Recommended)

Simply ask Claude Code to help debug your tests:

```
"Why is my login test failing?"
"Check if the email input selector on the login page works"
"Analyze the test failures from my last run"
"Suggest fixes for timeout errors"
```

Claude will automatically use the appropriate MCP tools.

### 2. Explicit MCP Commands

You can also explicitly call MCP tools:

```
/mcp selenium-debug validate-selector-live --selectorType css --selectorValue "#Email" --page "/login"
```

## Available Tools

### Live Testing Tools

#### `validate-selector-live`
Test a selector on the live website:
```json
{
  "selectorType": "css",
  "selectorValue": "#Email",
  "page": "/login",
  "waitTime": 10000
}
```

#### `test-wait-strategies`
Test different wait times to find optimal configuration:
```json
{
  "selectorType": "css",
  "selectorValue": ".product-item",
  "page": "/books"
}
```

#### `find-alternative-selectors`
Find working alternatives for failing selectors:
```json
{
  "selectorType": "css",
  "selectorValue": "button.login-button",
  "page": "/login"
}
```

#### `test-page-navigation`
Test navigation through multiple pages:
```json
{
  "pages": ["/", "/login", "/register"],
  "takeScreenshots": true
}
```

#### `validate-selector-json`
Validate all selectors from a JSON file:
```json
{
  "selectorFile": "homepage-selectors.json",
  "page": "/"
}
```

### Static Analysis Tools

#### `analyze-test-failures`
Analyze failures from the last test run:
```json
{
  "detailed": true
}
```

#### `get-test-summary`
Get summary of test execution:
```json
{}
```

#### `analyze-failure-patterns`
Identify common failure patterns:
```json
{}
```

#### `check-testng-config`
Validate TestNG configuration:
```json
{
  "configFile": "testng-powerhouse.xml"
}
```

#### `inspect-page-object`
Inspect Page Object selectors:
```json
{
  "pageClassName": "HomePage"
}
```

#### `inspect-test-class`
Inspect test class methods:
```json
{
  "testClassName": "ComprehensiveLoginTests"
}
```

#### `list-selector-files`
List all selector JSON files:
```json
{}
```

#### `read-selector-file`
Read a specific selector file:
```json
{
  "selectorFile": "product-selectors.json"
}
```

### Fix Suggestion Tools

#### `suggest-fixes`
Get automated fix suggestions:
```json
{
  "testName": "testValidLogin"
}
```

#### `diagnose-error`
Diagnose a specific error:
```json
{
  "errorMessage": "NoSuchElementException: Unable to locate element",
  "errorType": "NoSuchElementException"
}
```

#### `suggest-wait-fix`
Get wait strategy recommendations:
```json
{
  "currentWait": 10000,
  "errorMessage": "TimeoutException"
}
```

#### `suggest-selector-fix`
Get selector improvement suggestions:
```json
{
  "selectorType": "css",
  "selectorValue": "div > div > div:nth-child(3) > button"
}
```

#### `suggest-parallel-fixes`
Get parallel execution recommendations:
```json
{}
```

## Self-Healing Tools (AI-Driven)

### `stream-test-execution`
Monitor mvn test in real-time (non-blocking):
```json
{
  "testFilter": "LoginTest",
  "duration": 0
}
```

### `find-working-selector`
Navigate to live page and find working selector:
```json
{
  "page": "/login",
  "currentSelectorType": "css",
  "currentSelectorValue": "#Email"
}
```

### `update-selector-file`
Update JSON selector file:
```json
{
  "selectorFile": "login-selectors.json",
  "selectorName": "emailInput",
  "newType": "name",
  "newValue": "Email"
}
```

### `update-page-object-selector`
Update Java Page Object:
```json
{
  "pageClassName": "LoginPage",
  "selectorVariable": "emailInput",
  "newType": "name",
  "newValue": "Email"
}
```

### `add-to-retry-queue`
Queue test for retry after fix:
```json
{
  "testClass": "LoginTest",
  "testMethod": "testValidLogin"
}
```

### `retry-single-test`
Retry one test:
```json
{
  "testClass": "LoginTest",
  "testMethod": "testValidLogin"
}
```

### `retry-all-queued`
Retry all fixed tests:
```json
{}
```

## Example Workflows

### Workflow 1: AI-Driven Self-Healing (Recommended!)

**You**: "Run my tests and automatically fix any failures"

**Claude executes**:
```
1. Uses: stream-test-execution
   → Monitors mvn clean test in real-time

2. Test fails: LoginTest.testValidLogin - NoSuchElementException: #Email

3. Claude investigates (while tests continue running):
   → Uses: find-working-selector
   → Page: /login
   → Current: #Email (not found)
   → Discovers: input[name="Email"] ✅

4. Claude applies fix:
   → Uses: update-selector-file
   → login-selectors.json: #Email → input[name="Email"]

5. Claude queues for retry:
   → Uses: add-to-retry-queue
   → LoginTest#testValidLogin

6. Initial run completes (57/59 passed)

7. Claude retries fixed tests:
   → Uses: retry-all-queued
   → LoginTest.testValidLogin → PASS ✅

8. Claude reports:
   "✅ All tests passing! Fixed email selector in login-selectors.json"
```

### Workflow 2: Debug Failing Login Test

1. **Run tests** and get failure:
```bash
mvn clean test
```

2. **Analyze failures in Claude Code**:
```
"Analyze my test failures and tell me what's wrong"
```

3. **Validate specific selector**:
```
"Check if the email input selector works on the login page"
```

4. **Get fix suggestions**:
```
"Suggest fixes for the login test failures"
```

5. **Apply fixes** based on recommendations

### Workflow 2: Optimize Selector Performance

1. **Test current selector**:
```
"Test wait strategies for the product grid selector"
```

2. **Find alternatives if needed**:
```
"Find alternative selectors for the product grid"
```

3. **Validate across selector file**:
```
"Validate all selectors in product-selectors.json"
```

### Workflow 3: Fix Parallel Execution Issues

1. **Analyze patterns**:
```
"Analyze failure patterns from my test run"
```

2. **Check configuration**:
```
"Check my TestNG configuration for issues"
```

3. **Get parallel fixes**:
```
"Suggest fixes for parallel execution problems"
```

## Resources

The MCP server also exposes resources that can be accessed:

- `selenium://framework/pom.xml` - Maven configuration
- `selenium://framework/testng-config` - TestNG configuration
- `selenium://framework/test-results` - Latest test results
- `selenium://framework/selectors` - All selector files
- `selenium://framework/base-test` - BaseTest configuration

## Troubleshooting

### MCP Server Not Starting

1. **Check Node.js version**:
```bash
node --version  # Should be 18+
```

2. **Rebuild the project**:
```bash
npm run clean
npm run build
```

3. **Check logs** in Claude Code output

### Selectors Not Working on Live Site

1. **Verify page URL** is correct
2. **Check if site is accessible**:
```bash
curl https://demowebshop.tricentis.com
```
3. **Increase wait times** if page loads slowly

### Test Results Not Found

1. **Run tests first**:
```bash
mvn clean test
```

2. **Verify results location**:
```bash
ls -la target/surefire-reports/testng-results.xml
```

## Architecture

```
selenium-debug-mcp/
├── src/
│   ├── index.ts                    # MCP server entry point
│   ├── selenium/
│   │   └── browser-controller.ts   # WebDriver wrapper for live testing
│   ├── analyzers/
│   │   └── testng-parser.ts        # TestNG report parser
│   ├── tools/
│   │   ├── live-testing.ts         # Live testing tools
│   │   ├── static-analysis.ts      # Static analysis tools
│   │   └── fix-suggestions.ts      # Fix suggestion engine
│   ├── resources/
│   │   └── framework-resources.ts  # MCP resources
│   └── utils/
│       └── framework-reader.ts     # Java/JSON file reader
├── package.json
├── tsconfig.json
└── .env
```

## Contributing

This MCP server is specifically designed for the DemoWebShop Java Selenium framework. Extend or modify tools in the `src/tools/` directory to add new debugging capabilities.

## License

MIT

---

**Happy Debugging! 🚀**
