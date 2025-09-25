#!/bin/bash
# Simple log cleanup script

LOG_DIR="target/logs"

echo "Cleaning logs in $LOG_DIR"

# Remove old log files (keep last 3 days)
find "$LOG_DIR" -name "*.log" -type f -mtime +3 -delete 2>/dev/null

# Clear current test run log
> "$LOG_DIR/test-run.log" 2>/dev/null

echo "Log cleanup completed"