# AI-Optimized Test Failure Analysis Guide

## Overview

Your CI workflows now automatically generate token-efficient failure summaries optimized for AI analysis. This reduces token consumption by **95%+** compared to uploading raw XML reports.

## How It Works

### Automatic CI Generation

After each CI run, the workflow:
1. ✅ Runs all test shards in parallel
2. ✅ Extracts only failed/skipped tests with minimal details
3. ✅ Creates condensed `test-failures.txt` file (2-5 KB vs 1+ MB XML)
4. ✅ Uploads as artifact `📋-test-failures-summary`
5. ✅ Shows first 20 failures in GitHub Actions summary

### What's Included

**Token-Efficient Format:**
```
[shard] TestClass.method: ERROR/FAIL:ExceptionType | Message (truncated to 120 chars)
```

**Example Output:**
```
=== TEST FAILURES SUMMARY ===

FAILED/ERROR TESTS:
[login] LoginTests.testInvalidCredentials: FAIL:AssertionError | Expected error message but got success
[cart] ShoppingCartTests.testAddItem: ERROR:NoSuchElementException | Unable to locate element .add-cart-btn
[checkout] CheckoutTests.testPayment: ERROR:TimeoutException | Timed out after 10s waiting for payment form

SKIPPED TESTS:
[registration] RegistrationTests.testEmailVerification

=== STATISTICS ===
[login] Tests:10 F:1 E:0 S:0
[cart] Tests:5 F:0 E:1 S:0

TOTAL: Tests=82 Pass=79 Fail=2 Error=1 Skip=0 Rate=96.3%
```

## Usage Methods

### Method 1: Download from GitHub Actions

1. Go to your GitHub Actions run
2. Scroll to **Artifacts** section
3. Download `📋-test-failures-summary`
4. Extract and copy content

### Method 2: Using the Analysis Script (Recommended)

**Download and analyze in one command:**

```bash
# By workflow run ID
.github/scripts/analyze-failures.sh 1234567890

# Or from local file
.github/scripts/analyze-failures.sh test-failures.txt
```

**Output includes:**
- Statistics breakdown
- Failures grouped by error type
- Failures by shard
- Top failing test classes
- Ready-to-paste AI prompt

**Example output:**
```
=========================================
   Test Failure Analysis Summary
=========================================

TOTAL: Tests=82 Pass=79 Fail=2 Error=1 Skip=0 Rate=96.3%

Breakdown:
  - Failures (assertion/logic): 2
  - Errors (exceptions): 1
  - Skipped: 0

=========================================
   Failures Grouped by Error Type
=========================================

    2 × NoSuchElementException
    1 × AssertionError
    1 × TimeoutException

=========================================
   AI Analysis Prompt (copy & paste)
=========================================

[Full formatted failure list ready for AI]

Token Estimate: ~850 tokens
=========================================
```

### Method 3: Direct Download with GitHub CLI

```bash
# List recent runs
gh run list --limit 5

# Download specific run's failures
gh run download <run-id> -n "📋-test-failures-summary"

# View content
cat test-failures.txt
```

## Using with AI (Claude Code)

### Quick Analysis

```bash
# Download failures
gh run download <run-id> -n "📋-test-failures-summary"

# In Claude Code chat
cat test-failures.txt
```

Then prompt:
```
I have test failures from my Selenium CI run. Please analyze these failures
and suggest fixes for each one:

[Paste test-failures.txt content here]

For each failure, provide:
1. Root cause analysis
2. Specific fix recommendation
3. Which file needs to be modified
```

### Automated Fixing Workflow

```bash
# 1. Download failures
.github/scripts/analyze-failures.sh <run-id> > analysis.txt

# 2. Feed to Claude Code with MCP tools
# The failure format works seamlessly with MCP debugging tools:

# For selector issues:
mcp__selenium-debug__diagnose-error \
  --errorMessage "NoSuchElementException: Unable to locate .add-cart-btn"

# For specific test analysis:
mcp__mvn-test-runner__analyze-failure \
  --testClass "LoginTests" \
  --testMethod "testInvalidCredentials"

# 3. Apply fixes and rerun
mvn test -Dtest=LoginTests#testInvalidCredentials
```

## Token Savings Comparison

| Method | Size | Tokens | Notes |
|--------|------|--------|-------|
| Full XML upload | 1.2 MB | 50,000-100,000 | ❌ Wasteful |
| Multiple XML files | 100 KB each | 5,000-10,000 each | ❌ Still high |
| **Condensed summary** | **2-5 KB** | **500-1,000** | ✅ **95%+ savings** |

**Example savings:**
- Before: 80,000 tokens for full XML reports
- After: 850 tokens for condensed summary
- **Savings: 98.9%** 🎉

## Customization

### Adjust Message Length

Edit `.github/scripts/extract-failures.sh` line 35:

```bash
if (length(msg) > 120) msg=substr(msg, 1, 120) "..."
```

**Recommendations:**
- `80` - Ultra-condensed (60% additional reduction)
- `120` - Default (balanced)
- `200` - More context (30% increase)

### Filter by Severity

```bash
# Only show errors (not failures)
grep 'ERROR:' test-failures.txt

# Only show failures (not errors)
grep 'FAIL:' test-failures.txt

# Specific test classes
grep 'LoginTests' test-failures.txt
```

## Integration with Existing Workflows

All three sharded workflows automatically generate failure summaries:
- ✅ `selenium-tests-sharded.yml` (4 shards)
- ✅ `selenium-tests-ultra-sharded.yml` (12 shards)
- ✅ `selenium-tests-free-tier.yml` (4 or 12 shards)

No configuration needed - just download the artifact after any run!

## Best Practices

### When to Use

✅ **Use condensed summary for:**
- Initial failure analysis
- Identifying patterns across shards
- Quick AI consultation
- Token-limited scenarios

✅ **Use full XML/Allure reports for:**
- Deep debugging specific tests
- Stack trace analysis
- Manual investigation
- Detailed reproduction steps

### Workflow Tips

1. **Quick check**: View first 20 failures in GitHub Actions summary
2. **AI analysis**: Download condensed summary for token-efficient AI help
3. **Deep dive**: Download full Allure report if AI needs more context
4. **Fix and verify**: Rerun only failed tests to confirm fixes

## Troubleshooting

### Script fails to extract failures

**Check XML files exist:**
```bash
find target/surefire-reports -name "TEST-*.xml"
```

**Verify script permissions:**
```bash
chmod +x .github/scripts/extract-failures.sh
```

### No failures detected but tests failed

**Verify XML format:**
```bash
head -20 target/surefire-reports/TEST-*.xml
```

**Check for parsing errors:**
```bash
.github/scripts/extract-failures.sh target/surefire-reports /tmp/test.txt
cat /tmp/test.txt
```

### Too many failures to analyze at once

**Prioritize by shard:**
```bash
# Show only critical shards
grep '^\[login\]' test-failures.txt
grep '^\[checkout\]' test-failures.txt
```

**Group by error type:**
```bash
grep 'NoSuchElementException' test-failures.txt
grep 'TimeoutException' test-failures.txt
```

## Summary

✅ **95%+ token reduction** - 2-5 KB vs 1+ MB
✅ **Automatic generation** - No manual work required
✅ **AI-optimized format** - Plain text, concise, structured
✅ **Shard-aware** - Track failures by parallel job
✅ **GitHub Actions integrated** - Artifact + summary view
✅ **MCP-compatible** - Works with debugging tools
✅ **30-day retention** - Available for historical analysis

**Next Steps:**
1. Run your CI workflow
2. Download `📋-test-failures-summary` artifact
3. Paste into Claude Code for instant analysis
4. Apply suggested fixes
5. Profit! 🎉
