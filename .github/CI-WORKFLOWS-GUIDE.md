# CI Workflows Guide

## Available Workflows

Your repository has 2 optimized CI workflows for different scenarios:

### 1. CI Optimized (`selenium-tests-free-tier.yml`)

**Best for:** GitHub accounts with limited resources, cost optimization

**Features:**
- Smart strategy selection based on context
- Pull requests: 4 shards, max 2 parallel (conservative)
- Main branch/scheduled: 12 shards, max 3 parallel (balanced)
- Automatic failure extraction and AI-optimized reporting

**When it runs:**
- Every push to main/develop
- Every pull request
- Scheduled nightly (2 AM UTC)
- Manual trigger with strategy selection

**Execution time:** 15-20 minutes (PR), 25-30 minutes (full)

### 2. Standard Sharded (`selenium-tests-sharded.yml`)

**Best for:** Balanced performance, 4-shard strategy

**Features:**
- 4 parallel shards (test suite groups)
- All shards run simultaneously
- Merged reporting with failure extraction
- Suitable for mid-sized test suites

**When it runs:**
- Push to main/develop
- Pull requests
- Scheduled nightly (2 AM UTC)

**Execution time:** 10-12 minutes

## Common Features (All Workflows)

### ✅ Automated Failure Extraction

All workflows automatically generate token-efficient failure summaries:
- `test-failures.txt` - Condensed format (2-5 KB)
- Artifact: `test-failures-summary`
- 95%+ token reduction vs raw XML
- First 20 failures shown in Actions summary

### ✅ Comprehensive Reporting

- Surefire XML reports
- Allure reports (combined across shards)
- Per-shard detailed results
- Overall statistics and pass rates

### ✅ Failure Management

- `maven.test.failure.ignore=true` - All shards complete
- Individual shard status tracking
- Merged failure analysis
- Ready for AI-powered fixing

### ✅ Security Scanning

- Trivy vulnerability scanning
- SARIF format for GitHub Security
- Runs independently of tests

## Choosing the Right Workflow

### Use CI Optimized if:
- ✅ On GitHub account with limited resources
- ✅ Want to minimize CI minutes
- ✅ Need adaptive strategy (smart selection)
- ✅ Cost is a primary concern

### Use Standard Sharded if:
- ✅ Have 4 logical test suite groups
- ✅ Want balanced execution (10-12 min)
- ✅ Tests have interdependencies within groups
- ✅ Medium-sized test suite (20-40 tests)

## CI Minutes Comparison

**For 82 tests on GitHub Actions:**

| Workflow | Execution Time | Parallel Jobs | CI Minutes Used |
|----------|----------------|---------------|-----------------|
| CI Optimized (PR) | 15-20 min | 2 at a time | 60-80 minutes |
| CI Optimized (main) | 25-30 min | 3 at a time | 100-120 minutes |
| Standard Sharded | 10-12 min | 4 at once | 40-48 minutes |

**GitHub Free Tier:** 2,000 CI minutes/month
**Recommendation:** Use CI Optimized workflow for automatic optimization

## Artifacts Generated

### Every Workflow Run Produces:

1. **test-failures-summary** (30 days)
   - Token-optimized failure list
   - Ready for AI analysis
   - 2-5 KB condensed format

2. **Test Results per Shard** (7 days)
   - Surefire XML reports
   - Allure results
   - Screenshots (if any)

3. **Combined Allure Report** (30 days)
   - HTML interactive report
   - Merged from all shards
   - Detailed test history

## GitHub Actions Summary

Each workflow run shows in the Actions tab:
- Overall pass rate
- Per-shard statistics
- **First 20 failures** (inline preview)
- Link to download full failure summary

## Using Failure Summaries with AI

### Quick Start

1. **Go to failed workflow run**
2. **Download** `test-failures-summary` artifact
3. **Extract** `test-failures.txt`
4. **Paste** into Claude Code

### Using the Analysis Script

```bash
# Download and analyze automatically
.github/scripts/analyze-failures.sh <workflow-run-id>

# Or analyze local file
.github/scripts/analyze-failures.sh test-failures.txt
```

### Output

```
=== TEST FAILURES SUMMARY ===

FAILED/ERROR TESTS:
[login] LoginTests.testInvalidPassword: FAIL:AssertionError | Expected error but got success
[cart] ShoppingCartTests.testAddItem: ERROR:NoSuchElementException | Unable to locate .add-cart-btn

TOTAL: Tests=82 Pass=79 Fail=2 Error=1 Skip=0 Rate=96.3%
```

**Token usage:** ~500-1,000 tokens (vs 50,000-100,000 for raw XML)

## Workflow Configuration

### Trigger Customization

Edit the workflow `on:` section:

```yaml
on:
  push:
    branches: [ main, develop ]  # Add/remove branches
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 2 * * *'  # Change schedule time
```

### Browser Matrix

Currently configured for Chrome only. To add browsers:

```yaml
matrix:
  browser: [chrome, firefox, edge]
```

### Java Version

Currently using Java 21. To change:

```yaml
java-version: [21]  # Change to 17, 11, etc.
```

## Maintenance

### Adding New Test Shards

For CI optimized workflow, edit matrix:

```yaml
matrix:
  shard:
    - { name: "new-feature", file: "testng-shard-new-feature.xml" }
```

Then create the TestNG XML file:
```bash
cp src/test/resources/config/shards/testng-shard-login.xml \
   src/test/resources/config/shards/testng-shard-new-feature.xml
```

### Updating Failure Extraction

Edit `.github/scripts/extract-failures.sh` for customization:
- Line 35: Adjust message truncation length
- Line 40: Add stack trace extraction
- Line 60: Modify output format

## Troubleshooting

### Workflow not triggering

Check:
1. Branch name matches workflow configuration
2. Workflow file is in `.github/workflows/`
3. YAML syntax is valid (`yamllint workflow.yml`)

### Failures not extracted

Verify:
1. `extract-failures.sh` has execute permissions
2. Surefire reports exist (`target/surefire-reports/TEST-*.xml`)
3. XML files are valid (not truncated)

### Artifact not found

Check:
1. `upload-artifact` step ran (check logs)
2. Retention period hasn't expired (7-30 days)
3. Artifact name matches download command

## Best Practices

### Workflow Selection Strategy

**Development (PRs):**
- Use CI optimized or standard sharded
- Fast feedback more important than coverage

**Main Branch:**
- Use CI optimized for full coverage with controlled parallelization
- Balance between speed and resource usage

**Nightly/Scheduled:**
- Use CI optimized for comprehensive testing
- Time less critical, coverage is priority

### Failure Management

1. **View summary in Actions tab** - First 20 failures inline
2. **Download artifact if needed** - Full failure list
3. **Use analysis script** - Organized breakdown
4. **Feed to AI** - Get fix recommendations
5. **Apply fixes** - Use MCP tools for automation
6. **Verify** - Rerun failed tests only

## Summary

✅ **2 workflows** for different scenarios
✅ **Automatic failure extraction** in all workflows
✅ **Token-optimized reports** (95%+ reduction)
✅ **Flexible execution strategies** (4 or 12 shards)
✅ **Cost-aware** (resource optimized)
✅ **AI-ready** (MCP-compatible format)

**Recommended default:** CI Optimized workflow (adaptive strategy)
