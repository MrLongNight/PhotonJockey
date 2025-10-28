#!/usr/bin/env python3
"""
Code Metrics Generator for PhotonJockey

This script analyzes Java source files and generates a metrics.json report
that can be used to identify refactoring candidates.

Usage:
    python3 tools/generate_metrics.py
    
Output:
    reports/metrics.json - JSON file with code metrics
"""

import json
import os
import re
from pathlib import Path
from datetime import datetime

SRC_DIR = "src/main/java"
OUTPUT_FILE = "reports/metrics.json"


def analyze_file(filepath, src_dir):
    """Analyze a single Java file and return metrics."""
    with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()
        lines = content.split('\n')
    
    # Get class name from file path
    rel_path = os.path.relpath(filepath, src_dir)
    class_name = rel_path.replace('.java', '').replace(os.sep, '.')
    
    # Count non-empty, non-comment lines
    code_lines = 0
    in_multiline_comment = False
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('/*'):
            in_multiline_comment = True
        if in_multiline_comment:
            if '*/' in stripped:
                in_multiline_comment = False
            continue
        if stripped and not stripped.startswith('//') and not stripped.startswith('*'):
            code_lines += 1
    
    # Count methods (improved regex)
    method_pattern = r'^\s*(public|private|protected|static|\s)*\s+[\w\<\>\[\]]+\s+(\w+)\s*\([^\)]*\)\s*(?:throws\s+[\w\s,]+)?\s*\{'
    methods = []
    for i, line in enumerate(lines):
        if re.search(method_pattern, line):
            method_name = re.search(r'\s+(\w+)\s*\(', line)
            if method_name:
                # Count method lines (simple heuristic)
                method_lines = 1
                brace_count = line.count('{') - line.count('}')
                j = i + 1
                while j < len(lines) and brace_count > 0:
                    method_lines += 1
                    brace_count += lines[j].count('{') - lines[j].count('}')
                    j += 1
                
                methods.append({
                    'name': method_name.group(1),
                    'line_number': i + 1,
                    'lines_of_code': method_lines
                })
    
    # Count fields
    field_pattern = r'^\s*(public|private|protected|static|final|\s)+\s+[\w\<\>\[\]]+\s+\w+\s*(=|;)'
    fields = len([line for line in lines if re.search(field_pattern, line)])
    
    # Check for concurrency patterns
    thread_usage = len(re.findall(r'new\s+Thread\s*\(', content))
    executor_usage = len(re.findall(r'ExecutorService|ScheduledExecutorService|ThreadPoolExecutor', content))
    sync_blocks = len(re.findall(r'synchronized\s*[\(\{]', content))
    
    # Count imports
    imports = len([line for line in lines if line.strip().startswith('import ')])
    
    # Calculate complexity score
    complexity_score = (
        code_lines // 10 +
        len(methods) * 3 +
        fields +
        thread_usage * 10 +
        executor_usage * 5 +
        sync_blocks * 5
    )
    
    # Find long methods (>50 lines)
    long_methods = [m for m in methods if m['lines_of_code'] > 50]
    
    return {
        'class_name': class_name,
        'file_path': rel_path,
        'metrics': {
            'lines_of_code': code_lines,
            'method_count': len(methods),
            'field_count': fields,
            'import_count': imports,
            'thread_usage': thread_usage,
            'executor_usage': executor_usage,
            'synchronized_blocks': sync_blocks,
            'complexity_score': complexity_score,
            'long_methods': len(long_methods)
        },
        'methods': methods[:10],  # Top 10 methods only
        'refactor_indicators': {
            'uses_manual_threads': thread_usage > 0,
            'has_synchronization': sync_blocks > 0,
            'has_long_methods': len(long_methods) > 0,
            'high_complexity': complexity_score > 20
        }
    }


def main():
    """Main function to analyze all Java files and generate metrics."""
    # Find all Java files
    java_files = list(Path(SRC_DIR).rglob('*.java'))
    print(f"Analyzing {len(java_files)} Java files...")
    
    # Analyze all files
    results = []
    for filepath in sorted(java_files):
        try:
            result = analyze_file(filepath, SRC_DIR)
            results.append(result)
        except Exception as e:
            print(f"Error analyzing {filepath}: {e}")
    
    # Sort by complexity
    results.sort(key=lambda x: x['metrics']['complexity_score'], reverse=True)
    
    # Write JSON
    output = {
        'analysis_date': datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ'),
        'total_classes': len(results),
        'classes': results
    }
    
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(output, f, indent=2)
    
    print(f"\nAnalysis complete. Results written to {OUTPUT_FILE}")
    print(f"\nTop 10 most complex classes:")
    for i, cls in enumerate(results[:10], 1):
        print(f"{i}. {cls['class_name']} (complexity: {cls['metrics']['complexity_score']})")


if __name__ == '__main__':
    main()
