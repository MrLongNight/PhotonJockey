#!/bin/bash
#
# Code Metrics Collection Script for PhotonJockey
#
# This script runs the Python-based metrics collection tool.
# It generates reports/metrics.json containing:
#   - LOC per package
#   - Cyclomatic complexity per method (top 20)
#   - Number of threads started per class
#   - List of external libraries
#
# Usage:
#   ./tools/run_metrics.sh
#

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "PhotonJockey Code Metrics Collection"
echo "====================================="
echo ""

# Check for Python 3
if ! command -v python3 &> /dev/null; then
    echo "Error: python3 is not installed or not in PATH"
    exit 1
fi

# Run the Python metrics collector
python3 "$SCRIPT_DIR/run_metrics.py"

exit 0
