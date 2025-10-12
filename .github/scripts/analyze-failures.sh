#!/bin/bash
# Quick failure analysis helper for AI consumption
# Usage: ./analyze-failures.sh [test-failures.txt or workflow-run-id]

set -e

FAILURES_FILE=""

# Check if argument is a file or GitHub run ID
if [ -f "$1" ]; then
  FAILURES_FILE="$1"
elif [[ "$1" =~ ^[0-9]+$ ]]; then
  echo "Downloading failures from GitHub Actions run $1..."
  gh run download "$1" -n "📋-test-failures-summary" -D /tmp/failure-download
  FAILURES_FILE="/tmp/failure-download/test-failures.txt"
else
  echo "Usage: $0 <test-failures.txt | workflow-run-id>"
  echo ""
  echo "Examples:"
  echo "  $0 test-failures.txt              # Analyze local file"
  echo "  $0 1234567890                     # Download from GitHub run"
  echo "  gh run list | head -5             # List recent runs"
  exit 1
fi

if [ ! -f "$FAILURES_FILE" ]; then
  echo "Error: Could not find $FAILURES_FILE"
  exit 1
fi

echo "========================================="
echo "   Test Failure Analysis Summary"
echo "========================================="
echo ""

# Extract statistics
STATS=$(grep "^TOTAL:" "$FAILURES_FILE")
echo "$STATS"
echo ""

# Count failures by type
ERROR_COUNT=$(grep -c "ERROR:" "$FAILURES_FILE" 2>/dev/null || echo "0")
FAIL_COUNT=$(grep -c "FAIL:" "$FAILURES_FILE" 2>/dev/null || echo "0")
SKIP_COUNT=$(grep -c "^\[.*\].*\.$" "$FAILURES_FILE" 2>/dev/null | grep "SKIPPED" -c || echo "0")

echo "Breakdown:"
echo "  - Failures (assertion/logic): $FAIL_COUNT"
echo "  - Errors (exceptions): $ERROR_COUNT"
echo "  - Skipped: $SKIP_COUNT"
echo ""

# Group by error type
echo "========================================="
echo "   Failures Grouped by Error Type"
echo "========================================="
echo ""

grep -E "(ERROR:|FAIL:)" "$FAILURES_FILE" | \
  sed 's/.*\(ERROR:\|FAIL:\)/\1/' | \
  sed 's/ |.*//' | \
  sort | uniq -c | sort -rn | \
  awk '{printf "  %3d × %s\n", $1, $2}'

echo ""

# Group by shard
echo "========================================="
echo "   Failures by Shard"
echo "========================================="
echo ""

grep "^\[.*\].*:" "$FAILURES_FILE" | \
  grep -oP '^\[\K[^\]]+' | \
  sort | uniq -c | sort -rn | \
  awk '{printf "  %2d failures in [%s]\n", $1, $2}'

echo ""

# Show top failing test classes
echo "========================================="
echo "   Top Failing Test Classes"
echo "========================================="
echo ""

grep "^\[.*\].*:" "$FAILURES_FILE" | \
  sed 's/^\[.*\] //' | \
  sed 's/\..*//' | \
  sort | uniq -c | sort -rn | head -10 | \
  awk '{printf "  %2d × %s\n", $1, $2}'

echo ""
echo "========================================="
echo "   AI Analysis Prompt (copy & paste)"
echo "========================================="
echo ""
echo "I have test failures from my Selenium CI run. Please analyze and suggest fixes:"
echo ""
cat "$FAILURES_FILE"
echo ""
echo "For each failed test, provide:"
echo "1. Root cause analysis"
echo "2. Specific fix recommendation"
echo "3. Related tests that might have similar issues"
echo ""
echo "========================================="
echo "   Raw Failures (token-optimized format)"
echo "========================================="
echo ""

# Extract just the failed tests for easy copy-paste
grep "^\[.*\].*:" "$FAILURES_FILE" | head -30

TOTAL_FAILURES=$(grep -c "^\[.*\].*:" "$FAILURES_FILE" 2>/dev/null || echo "0")
if [ "$TOTAL_FAILURES" -gt 30 ]; then
  echo "... and $((TOTAL_FAILURES - 30)) more (see full file: $FAILURES_FILE)"
fi

echo ""
echo "========================================="
echo "Token Estimate: ~$(($(wc -c < "$FAILURES_FILE") / 4)) tokens"
echo "File: $FAILURES_FILE"
echo "========================================="
