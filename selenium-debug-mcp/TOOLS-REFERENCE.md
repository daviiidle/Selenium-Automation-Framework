# MCP Tools Reference

Complete reference of all 30+ tools available in the Selenium Debug MCP Server.

## 🤖 Self-Healing Tools (12 tools)

### `stream-test-execution`
**Purpose**: Monitor `mvn test` in real-time without blocking
**When to use**: Start of healing workflow
**Arguments**:
- `testFilter` (optional): Filter tests (e.g., "LoginTest")
- `duration` (optional): Monitoring duration in seconds (0 = until completion)

**Example**:
```javascript
{
  "testFilter": "LoginTest",
  "duration": 0
}
```

---

### `find-working-selector`
**Purpose**: Navigate to live page and discover working selector
**When to use**: When current selector fails
**Arguments**:
- `page`: Page path (e.g., "/login")
- `currentSelectorType`: Type of selector (css, xpath, id, name)
- `currentSelectorValue`: Current selector value

**Returns**: Best working selector with stability score

---

### `update-selector-file`
**Purpose**: Update JSON selector file
**When to use**: After finding working selector
**Arguments**:
- `selectorFile`: File name (e.g., "login-selectors.json")
- `selectorName`: Selector key name
- `newType`: New selector type
- `newValue`: New selector value

**Creates backup**: Yes

---

### `update-page-object-selector`
**Purpose**: Update Java Page Object selector
**When to use**: When selector is hardcoded in Java
**Arguments**:
- `pageClassName`: Page class name (e.g., "LoginPage")
- `selectorVariable`: Variable name (e.g., "emailInput")
- `newType`: Selenium By method (cssSelector, id, name, xpath)
- `newValue`: New selector value

**Creates backup**: Yes

---

### `update-wait-time`
**Purpose**: Update wait/timeout duration
**When to use**: Timeout errors
**Arguments**:
- `javaFile`: Java file name without .java extension
- `currentSeconds`: Current timeout in seconds
- `newSeconds`: New timeout in seconds

**Creates backup**: Yes

---

### `add-to-retry-queue`
**Purpose**: Add test to retry queue
**When to use**: After applying a fix
**Arguments**:
- `testClass`: Test class name
- `testMethod`: Test method name

---

### `retry-single-test`
**Purpose**: Retry one specific test
**When to use**: Verify a specific fix
**Arguments**:
- `testClass`: Test class name
- `testMethod`: Test method name

**Returns**: Pass/fail status, duration, output

---

### `retry-all-queued`
**Purpose**: Retry all tests in queue
**When to use**: After initial run completes
**Arguments**: None

**Returns**: Results for all retried tests

---

### `get-retry-queue`
**Purpose**: Check current retry queue
**When to use**: Status check
**Arguments**: None

---

### `clear-retry-queue`
**Purpose**: Clear the retry queue
**When to use**: Start fresh
**Arguments**: None

---

### `list-backups`
**Purpose**: List all backup files
**When to use**: Safety check / before restore
**Arguments**: None

---

### `restore-backup`
**Purpose**: Restore file from backup
**When to use**: Undo a change
**Arguments**:
- `backupFile`: Backup file name
- `targetFile`: File path to restore to

---

## 🔴 Live Testing Tools (5 tools)

### `validate-selector-live`
Test selector on live website

### `test-wait-strategies`
Test multiple wait times

### `find-alternative-selectors`
Generate selector alternatives

### `test-page-navigation`
Navigate through page flow

### `validate-selector-json`
Test all selectors from JSON file

---

## 📊 Static Analysis Tools (8 tools)

### `analyze-test-failures`
Parse TestNG results

### `get-test-summary`
Get execution summary

### `analyze-failure-patterns`
Identify common patterns

### `check-testng-config`
Validate TestNG XML

### `inspect-page-object`
Extract selectors from Page Object

### `inspect-test-class`
Extract test methods

### `list-selector-files`
List all JSON selector files

### `read-selector-file`
Read selector JSON content

---

## 🔧 Fix Suggestion Tools (5 tools)

### `suggest-fixes`
Get automated fix recommendations

### `diagnose-error`
Diagnose specific error

### `suggest-wait-fix`
Wait strategy recommendations

### `suggest-selector-fix`
Selector improvement suggestions

### `suggest-parallel-fixes`
Parallel execution recommendations

---

## Tool Categories by Use Case

### 🚨 When Test Fails
1. `analyze-test-failures` - Understand what failed
2. `diagnose-error` - Get error details
3. `suggest-fixes` - Get recommendations

### 🔍 Investigation
1. `validate-selector-live` - Test on real page
2. `test-wait-strategies` - Find optimal timing
3. `inspect-page-object` - See current implementation

### 🔧 Applying Fixes
1. `find-working-selector` - Discover working alternative
2. `update-selector-file` - Apply to JSON
3. `update-page-object-selector` - Apply to Java

### ✅ Validation
1. `retry-single-test` - Test one fix
2. `retry-all-queued` - Test all fixes
3. `validate-selector-json` - Verify all selectors

### 🛡️ Safety
1. `list-backups` - Check backups
2. `restore-backup` - Undo changes
3. All update tools create automatic backups

---

## Typical Self-Healing Workflow

```
1. stream-test-execution
   ↓
2. (Failure detected)
   ↓
3. find-working-selector
   ↓
4. update-selector-file
   ↓
5. add-to-retry-queue
   ↓
6. (Initial run completes)
   ↓
7. retry-all-queued
   ↓
8. (Report results)
```

---

## All 30 Tools at a Glance

| # | Tool Name | Category | Modifies Files |
|---|-----------|----------|----------------|
| 1 | stream-test-execution | Healing | No |
| 2 | find-working-selector | Healing | No |
| 3 | update-selector-file | Healing | Yes |
| 4 | update-page-object-selector | Healing | Yes |
| 5 | update-wait-time | Healing | Yes |
| 6 | add-to-retry-queue | Healing | No |
| 7 | retry-single-test | Healing | No |
| 8 | retry-all-queued | Healing | No |
| 9 | get-retry-queue | Healing | No |
| 10 | clear-retry-queue | Healing | No |
| 11 | list-backups | Healing | No |
| 12 | restore-backup | Healing | Yes |
| 13 | validate-selector-live | Live Testing | No |
| 14 | test-wait-strategies | Live Testing | No |
| 15 | find-alternative-selectors | Live Testing | No |
| 16 | test-page-navigation | Live Testing | No |
| 17 | validate-selector-json | Live Testing | No |
| 18 | analyze-test-failures | Static Analysis | No |
| 19 | get-test-summary | Static Analysis | No |
| 20 | analyze-failure-patterns | Static Analysis | No |
| 21 | check-testng-config | Static Analysis | No |
| 22 | inspect-page-object | Static Analysis | No |
| 23 | inspect-test-class | Static Analysis | No |
| 24 | list-selector-files | Static Analysis | No |
| 25 | read-selector-file | Static Analysis | No |
| 26 | suggest-fixes | Fix Suggestions | No |
| 27 | diagnose-error | Fix Suggestions | No |
| 28 | suggest-wait-fix | Fix Suggestions | No |
| 29 | suggest-selector-fix | Fix Suggestions | No |
| 30 | suggest-parallel-fixes | Fix Suggestions | No |

---

## Resources (5 available)

1. `selenium://framework/pom.xml` - Maven config
2. `selenium://framework/testng-config` - TestNG XML
3. `selenium://framework/test-results` - Latest results
4. `selenium://framework/selectors` - All selector files
5. `selenium://framework/base-test` - BaseTest config
