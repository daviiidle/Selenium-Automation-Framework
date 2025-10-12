# AI-Optimized Test Failure Analysis

## Overview

This directory contains scripts to extract test failures in a token-efficient format optimized for AI analysis and automated fixing.

## Files

- `extract-failures.sh` - Parses TestNG/Surefire XML reports and extracts only failed/skipped tests with minimal details

## How It Works

### In CI Pipeline

1. After all shards complete, the `merge-and-report` job downloads all test results
2. `extract-failures.sh` processes all XML files and creates `test-failures.txt`
3. The condensed summary is uploaded as artifact `📋-test-failures-summary`
4. First 20 failures are shown in GitHub Actions summary
5. Full report available for download

### Output Format

```
=== TEST FAILURES SUMMARY ===
Format: [SHARD] TestClass.method: ErrorType | Message

FAILED/ERROR TESTS:
[login] LoginTests.testInvalidPassword: FAIL:AssertionError | Expected login to fail but succeeded
[cart] ShoppingCartTests.testAddToCart: ERROR:NoSuchElementException | Unable to locate element: .add-to-cart-button
[checkout] CheckoutTests.testPayment: FAIL:TimeoutException | Timed out after 10 seconds waiting for payment form

SKIPPED TESTS:
[registration] RegistrationTests.testEmailVerification

=== STATISTICS ===
[login] Tests:10 F:1 E:0 S:0
[cart] Tests:5 F:0 E:1 S:0
[checkout] Tests:5 F:1 E:0 S:0

TOTAL: Tests=82 Pass=79 Fail=2 Error=1 Skip=0 Rate=96.3%
=== END SUMMARY ===
```

## Usage for AI Analysis

### Downloading from GitHub Actions

1. Go to your workflow run in GitHub Actions
2. Scroll to "Artifacts" section
3. Download `📋-test-failures-summary` artifact
4. Extract `test-failures.txt`

### Token Efficiency

**Traditional approach (uploading full XML):**
- 100+ KB per shard × 12 shards = 1.2+ MB
- XML verbosity = 50,000-100,000 tokens
- ❌ High token consumption

**Token-optimized approach (this script):**
- Condensed text format ≈ 2-5 KB
- Only failed/skipped tests included
- Error messages truncated to 120 chars
- ✅ 95%+ token reduction (500-1000 tokens typical)

### Example: Feeding to AI

```bash
# Download the artifact and extract
unzip test-failures-summary.zip

# Feed to Claude Code or other AI
cat test-failures.txt
```

Then prompt:
```
I have test failures from my CI run. Here's the summary:
[paste test-failures.txt content]

Please analyze these failures and suggest fixes for each failed test.
```

## Local Testing

Test the script locally before pushing:

```bash
# Run tests and generate XML reports
mvn clean test -Pci

# Extract failures
.github/scripts/extract-failures.sh target/surefire-reports test-failures-local.txt

# Review output
cat test-failures-local.txt
```

## Customization

### Adjusting Message Length

Edit line 35 in `extract-failures.sh`:

```bash
if (length(msg) > 120) msg=substr(msg, 1, 120) "..."
```

Change `120` to desired length. Recommendations:
- **80** - Ultra-condensed (50% token reduction)
- **120** - Default (balanced)
- **200** - More context (moderate reduction)

### Adding Stack Traces

For more detailed debugging, modify the script to include stack traces (increases token usage):

```bash
# After line 40, add:
/<CDATA\[/ {
  getline trace
  if (length(trace) > 300) trace=substr(trace, 1, 300) "..."
  print "  Stack: " trace
}
```

## Integration with MCP Tools

The condensed format works seamlessly with MCP debugging tools:

```bash
# Download failure summary
gh run download <run-id> -n 📋-test-failures-summary

# Feed to Selenium Debug MCP
mcp__selenium-debug__diagnose-error \
  --errorMessage "$(grep 'NoSuchElementException' test-failures.txt | head -1)"

# Or use with Maven Test Runner MCP
mcp__mvn-test-runner__analyze-failure \
  --testClass "LoginTests" \
  --testMethod "testInvalidPassword"
```

## Benefits Summary

✅ **95%+ token reduction** compared to full XML
✅ **Instant failure overview** in GitHub Actions summary
✅ **AI-friendly format** - no XML parsing needed
✅ **Shard-aware** - tracks which shard had which failure
✅ **Automated** - runs on every CI execution
✅ **Downloadable** - available as artifact for 30 days

## Troubleshooting

### Script fails with "xmllint not found"

The script automatically falls back to grep-based parsing. For better accuracy, install xmllint:

```bash
# Ubuntu/Debian
sudo apt-get install libxml2-utils

# macOS
brew install libxml2
```

### No failures detected but tests failed

Check that XML files exist:

```bash
ls -la target/surefire-reports/TEST-*.xml
```

Verify XML format:

```bash
head -20 target/surefire-reports/TEST-*.xml
```

### Output too verbose

Reduce message length or filter by severity:

```bash
# Only show errors (not failures)
grep 'ERROR:' test-failures.txt

# Only show specific test classes
grep 'LoginTests' test-failures.txt
```
