# AI-Driven Self-Healing Guide

## Overview

The Selenium Debug MCP Server enables **Claude Code to automatically fix failing tests** by combining live browser testing with intelligent code modifications.

## How It Works

```
┌─────────────────────────────────────────────────────────────┐
│ YOU: "Run my tests and fix any failures"                   │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Claude: Uses MCP tool "stream-test-execution"              │
│         → Starts: mvn clean test                            │
│         → Monitors output in real-time                      │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Test Execution (Continuous - Don't Stop!)                  │
│ ✅ HomePage.testLogo                                        │
│ ❌ LoginTest.testValidLogin → NoSuchElementException       │
│ ✅ ProductTest.testSearch                                   │
│ ... (tests continue running)                                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Claude (Parallel Healing while tests run):                 │
│                                                             │
│ 1. Detects failure: "#Email selector not found"            │
│                                                             │
│ 2. Uses: find-working-selector                              │
│    → Launches Chrome headless                               │
│    → Navigates to https://demowebshop.tricentis.com/login  │
│    → Tests current selector: #Email → NOT FOUND            │
│    → Scans page for alternatives                            │
│    → Finds: input[name="Email"] → WORKS! ✅                │
│                                                             │
│ 3. Uses: update-selector-file                               │
│    → Backs up login-selectors.json                          │
│    → Updates: emailInput: "#Email" → "input[name='Email']" │
│                                                             │
│ 4. Uses: add-to-retry-queue                                 │
│    → Queues: LoginTest#testValidLogin                       │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Initial Test Run Completes                                  │
│ Results: 57/59 passed, 2 failed                             │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Claude: Uses retry-all-queued                               │
│         → Runs: mvn test -Dtest=LoginTest#testValidLogin    │
│         → LoginTest.testValidLogin → PASS ✅                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ Claude Reports:                                             │
│ "✅ All tests now passing! (59/59)                          │
│                                                             │
│ Fixes Applied:                                              │
│ • login-selectors.json: emailInput selector updated         │
│ • Backup created: .mcp-backups/login-selectors.json.backup" │
└─────────────────────────────────────────────────────────────┘
```

## Key Features

### 🎯 Continuous Execution
- Tests **never stop** during healing
- Failures are detected in real-time
- Fixes applied **in parallel** to running tests

### 🤖 AI Intelligence
- **Claude decides** which fix to apply
- **Not rule-based** - uses reasoning
- Learns from page structure
- Chooses most stable selectors

### 🔧 Automatic Code Updates
- Updates JSON selector files
- Modifies Java Page Objects
- Adjusts wait times
- **Creates backups** before every change

### 🔁 Smart Retry Logic
- Maintains retry queue
- Re-runs only fixed tests
- Validates fixes work
- Reports results

## Usage Examples

### Example 1: Simple Request

```
You: "Fix my failing tests"

Claude:
→ Streams test execution
→ Detects 3 failures
→ Fixes all 3 (selectors + wait times)
→ Retries
→ Reports: "All tests passing!"
```

### Example 2: Specific Test

```
You: "LoginTest is failing, please fix it"

Claude:
→ Runs LoginTest only
→ Finds email selector issue
→ Navigates to /login page
→ Discovers working selector
→ Updates login-selectors.json
→ Retries LoginTest
→ Reports fix details
```

### Example 3: Monitoring Only

```
You: "Watch my test execution and tell me about failures"

Claude:
→ Streams test output
→ Collects failure events
→ Shows you patterns
→ Suggests fixes (doesn't apply yet)
→ Waits for your approval
```

## What Claude Can Fix

### ✅ Selector Issues
- **Element not found** → Finds working alternative
- **Brittle selectors** → Replaces with stable ones
- **Multiple candidates** → Chooses best by stability score

### ✅ Timing Issues
- **Timeouts** → Increases wait times appropriately
- **Stale elements** → Adds retry logic suggestions
- **Page load delays** → Adjusts timeout configuration

### ✅ Configuration Issues
- **Parallel execution** → Reduces thread count
- **TestNG timeouts** → Updates XML configuration
- **Resource conflicts** → Suggests fixes

## Safety Features

### 🛡️ Backup Protection
- **Every file** backed up before modification
- Backups stored in `.mcp-backups/`
- Timestamped for easy recovery
- Can restore with `restore-backup` tool

### 🔍 Change Tracking
- All modifications logged
- Detailed change descriptions
- Before/after comparisons
- Rollback capability

### ⚠️ Validation
- Tests fixes before applying
- Verifies selectors work on live page
- Confirms retry passes
- Reports if fix didn't work

## Tool Reference

| Tool | Purpose | When Claude Uses It |
|------|---------|-------------------|
| `stream-test-execution` | Monitor tests | To watch mvn test run |
| `find-working-selector` | Discover selectors | When element not found |
| `update-selector-file` | Modify JSON | To apply selector fix |
| `update-page-object-selector` | Modify Java | To fix Page Object |
| `update-wait-time` | Adjust timing | For timeout errors |
| `add-to-retry-queue` | Queue test | After applying fix |
| `retry-single-test` | Re-run one test | To verify fix |
| `retry-all-queued` | Re-run all fixed | After initial run |
| `get-retry-queue` | Check queue | To report status |
| `list-backups` | Show backups | For safety check |
| `restore-backup` | Undo changes | If fix was wrong |

## Best Practices

### 1. Clear Instructions
✅ **Good**: "Run my tests and fix any selector issues"
✅ **Good**: "Watch my tests and automatically heal failures"
❌ **Vague**: "Help with tests"

### 2. Let Tests Complete
- Claude will monitor the **entire** test run
- Fixes happen **during** execution
- Retries happen **after** initial run completes

### 3. Trust the AI
- Claude chooses fixes based on **reasoning**
- Uses live page testing for validation
- Prioritizes **stable** selectors over quick fixes

### 4. Review Backups
- Check `.mcp-backups/` directory
- Claude reports all changes
- Can restore if needed

## Troubleshooting

### Issue: Tests still failing after healing
**Claude will**:
1. Report which fixes didn't work
2. Try alternative approaches
3. Suggest manual investigation if stuck

### Issue: Wrong selector chosen
**You can**:
1. Ask Claude to use a different selector
2. Restore from backup
3. Manually specify the selector to use

### Issue: Too many changes
**Set boundaries**:
- "Only fix selector issues, don't change wait times"
- "Show me fixes before applying"
- "Only fix LoginTest, leave others alone"

## Advanced Usage

### Healing with Constraints
```
"Fix my tests but only update JSON files, don't touch Java code"
"Fix selectors but keep wait times unchanged"
"Only fix tests in the authentication package"
```

### Iterative Healing
```
"Fix one test at a time and show me the results"
"Try fixing just the login test first"
"Apply fixes but ask me before retrying"
```

### Investigation Mode
```
"Find out why LoginTest is failing but don't fix it yet"
"Show me what selector would work best for the email input"
"Compare current selectors vs what's actually on the page"
```

## Files and Directories

```
Your Framework/
├── .mcp-backups/              ← Backup files created during healing
│   ├── login-selectors.json.2025-01-15T10-30-00.backup
│   └── LoginPage.java.2025-01-15T10-30-15.backup
├── src/
│   ├── main/resources/selectors/  ← JSON files Claude can modify
│   └── main/java/.../pages/       ← Page Objects Claude can modify
└── target/surefire-reports/   ← Test results Claude reads
```

## Success Metrics

After a self-healing session, Claude will report:

```
✅ Initial Run: 57/59 passed
🔧 Fixes Applied: 2
   • login-selectors.json: emailInput selector
   • RegisterPage.java: submit button wait time
🔁 Retry Results: 2/2 passed
🎯 Final Status: 59/59 passing (100%)
📦 Backups: 2 files backed up
```

---

**Ready to try?**

Just say: **"Run my tests and fix any failures you find!"**
