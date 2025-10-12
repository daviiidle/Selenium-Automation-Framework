#!/bin/bash
# Token-Efficient Test Failure Extractor
# Extracts only failed/skipped tests with minimal details for AI consumption

set -e

RESULTS_DIR="${1:-all-shards}"
OUTPUT_FILE="${2:-test-failures.txt}"

# Initialize counters
total_tests=0
total_failures=0
total_errors=0
total_skipped=0

# Output header
cat > "$OUTPUT_FILE" << 'EOF'
=== TEST FAILURES SUMMARY ===
Format: [SHARD] TestClass.method: ErrorType | Message

FAILED/ERROR TESTS:
EOF

# Process each shard directory
find "$RESULTS_DIR" -name "TEST-*.xml" -type f | sort | while read xml_file; do
  shard_name=$(echo "$xml_file" | grep -oP 'results-\K[^/]+' || echo "unknown")

  # Extract only failures and errors using xmllint/grep
  if command -v xmllint &> /dev/null; then
    # Parse XML properly
    xmllint --xpath "//testcase[failure or error]" "$xml_file" 2>/dev/null | \
    grep -oP '(classname="[^"]+"|name="[^"]+"|<failure[^>]*type="[^"]+"|<error[^>]*type="[^"]+"|message="[^"]+")' | \
    awk -v shard="$shard_name" '
      /classname=/ { class=$0; gsub(/.*classname="|"$/, "", class) }
      /name=/ && !/classname/ { method=$0; gsub(/.*name="|"$/, "", method) }
      /<failure.*type=/ || /<error.*type=/ {
        type=$0;
        gsub(/.*type="|".*/, "", type);
        if (/<error/) { type="ERROR:" type }
        else { type="FAIL:" type }
      }
      /message=/ {
        msg=$0;
        gsub(/.*message="|"$/, "", msg);
        # Truncate long messages
        if (length(msg) > 120) msg=substr(msg, 1, 120) "..."
        if (class && method) {
          print "[" shard "] " class "." method ": " type " | " msg
          class=""; method=""; type=""; msg=""
        }
      }
    ' >> "$OUTPUT_FILE"
  else
    # Fallback to grep-based extraction
    grep -A5 '<failure\|<error' "$xml_file" 2>/dev/null | \
    awk -v shard="$shard_name" '
      /<testcase.*classname=/ {
        match($0, /classname="([^"]+)".*name="([^"]+)"/, arr)
        test=arr[1] "." arr[2]
      }
      /<failure type=|<error type=/ {
        match($0, /type="([^"]+)".*message="([^"]+)"/, arr)
        type=arr[1]
        msg=arr[2]
        if (length(msg) > 120) msg=substr(msg, 1, 120) "..."
        if (test) {
          marker = (/<error/) ? "ERROR" : "FAIL"
          print "[" shard "] " test ": " marker ":" type " | " msg
          test=""
        }
      }
    ' >> "$OUTPUT_FILE"
  fi
done

# Add skipped tests section
echo "" >> "$OUTPUT_FILE"
echo "SKIPPED TESTS:" >> "$OUTPUT_FILE"

find "$RESULTS_DIR" -name "TEST-*.xml" -type f | while read xml_file; do
  shard_name=$(echo "$xml_file" | grep -oP 'results-\K[^/]+' || echo "unknown")

  grep '<testcase.*<skipped' "$xml_file" 2>/dev/null | \
  grep -oP 'classname="[^"]+".*?name="[^"]+"' | \
  sed 's/classname="//g; s/" name="/./' | sed 's/"$//' | \
  awk -v shard="$shard_name" '{print "[" shard "] " $0}' >> "$OUTPUT_FILE"
done

# Calculate summary statistics
echo "" >> "$OUTPUT_FILE"
echo "=== STATISTICS ===" >> "$OUTPUT_FILE"

for xml_file in $(find "$RESULTS_DIR" -name "TEST-*.xml" -type f); do
  shard_name=$(echo "$xml_file" | grep -oP 'results-\K[^/]+' || echo "unknown")

  tests=$(grep -oP 'tests="\K[0-9]+' "$xml_file" | head -1)
  failures=$(grep -oP 'failures="\K[0-9]+' "$xml_file" | head -1)
  errors=$(grep -oP 'errors="\K[0-9]+' "$xml_file" | head -1)
  skipped=$(grep -oP 'skipped="\K[0-9]+' "$xml_file" | head -1)

  if [ -n "$tests" ] && [ "$tests" -gt 0 ]; then
    total_tests=$((total_tests + tests))
    total_failures=$((total_failures + ${failures:-0}))
    total_errors=$((total_errors + ${errors:-0}))
    total_skipped=$((total_skipped + ${skipped:-0}))

    if [ "${failures:-0}" -gt 0 ] || [ "${errors:-0}" -gt 0 ]; then
      echo "[$shard_name] Tests:$tests F:${failures:-0} E:${errors:-0} S:${skipped:-0}" >> "$OUTPUT_FILE"
    fi
  fi
done

total_passed=$((total_tests - total_failures - total_errors - total_skipped))
pass_rate=$(awk "BEGIN {printf \"%.1f\", $total_passed * 100 / $total_tests}" 2>/dev/null || echo "0")

cat >> "$OUTPUT_FILE" << EOF

TOTAL: Tests=$total_tests Pass=$total_passed Fail=$total_failures Error=$total_errors Skip=$total_skipped Rate=${pass_rate}%
=== END SUMMARY ===
EOF

echo "Failure summary extracted to $OUTPUT_FILE"
cat "$OUTPUT_FILE"
