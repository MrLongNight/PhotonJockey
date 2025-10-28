#!/usr/bin/env python3
"""
Code Metrics Collection Tool for PhotonJockey (LightBeat)

This tool analyzes Java source code and generates metrics including:
- Lines of Code (LOC) per package
- Cyclomatic complexity per method (top 20)
- Number of threads started per class
- List of external libraries

Usage:
    python3 tools/run_metrics.py
    
Output:
    reports/metrics.json
"""

import json
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Tuple, Any


class JavaMetricsCollector:
    """Collects various metrics from Java source code."""
    
    def __init__(self, project_root: Path):
        self.project_root = project_root
        self.src_main_java = project_root / "src" / "main" / "java"
        self.build_gradle = project_root / "build.gradle"
        
    def collect_all_metrics(self) -> Dict[str, Any]:
        """Collect all metrics and return as dictionary."""
        print("Collecting code metrics...")
        
        metrics = {
            "loc_per_package": self.calculate_loc_per_package(),
            "cyclomatic_complexity_top_20": self.calculate_cyclomatic_complexity(),
            "threads_per_class": self.count_threads_per_class(),
            "external_libraries": self.extract_external_libraries(),
            "spotbugs_analysis": self.run_spotbugs_analysis(),
            "summary": {}
        }
        
        # Add summary statistics
        metrics["summary"] = {
            "total_packages": len(metrics["loc_per_package"]),
            "total_loc": sum(metrics["loc_per_package"].values()),
            "total_classes_with_threads": len([c for c, count in metrics["threads_per_class"].items() if count > 0]),
            "total_external_libraries": len(metrics["external_libraries"]),
            "spotbugs_bugs_found": metrics["spotbugs_analysis"].get("total_bugs", 0)
        }
        
        return metrics
    
    def calculate_loc_per_package(self) -> Dict[str, int]:
        """Calculate Lines of Code per package."""
        print("  - Calculating LOC per package...")
        package_loc = defaultdict(int)
        
        if not self.src_main_java.exists():
            print(f"    Warning: {self.src_main_java} does not exist")
            return dict(package_loc)
        
        for java_file in self.src_main_java.rglob("*.java"):
            # Get package name from directory structure
            relative_path = java_file.relative_to(self.src_main_java)
            package_parts = relative_path.parts[:-1]  # Exclude filename
            package_name = ".".join(package_parts) if package_parts else "(default)"
            
            # Count non-empty, non-comment lines
            loc = self._count_loc_in_file(java_file)
            package_loc[package_name] += loc
        
        # Sort by package name
        sorted_packages = dict(sorted(package_loc.items()))
        print(f"    Found {len(sorted_packages)} packages")
        return sorted_packages
    
    def _count_loc_in_file(self, file_path: Path) -> int:
        """Count lines of code in a file (excluding blank lines and comments)."""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Remove multi-line comments
            content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
            
            lines = content.split('\n')
            loc = 0
            for line in lines:
                # Remove single-line comments
                line = re.sub(r'//.*$', '', line).strip()
                # Count non-empty lines
                if line:
                    loc += 1
            
            return loc
        except Exception as e:
            print(f"    Warning: Error reading {file_path}: {e}")
            return 0
    
    def calculate_cyclomatic_complexity(self) -> List[Dict[str, Any]]:
        """Calculate cyclomatic complexity per method and return top 20."""
        print("  - Calculating cyclomatic complexity (top 20 methods)...")
        complexities = []
        
        if not self.src_main_java.exists():
            print(f"    Warning: {self.src_main_java} does not exist")
            return complexities
        
        for java_file in self.src_main_java.rglob("*.java"):
            file_complexities = self._calculate_file_complexity(java_file)
            complexities.extend(file_complexities)
        
        # Sort by complexity (descending) and take top 20
        complexities.sort(key=lambda x: x['complexity'], reverse=True)
        top_20 = complexities[:20]
        
        print(f"    Analyzed {len(complexities)} methods, returning top 20")
        return top_20
    
    def _calculate_file_complexity(self, file_path: Path) -> List[Dict[str, Any]]:
        """Calculate cyclomatic complexity for all methods in a file."""
        complexities = []
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Get relative path for reporting
            relative_path = file_path.relative_to(self.project_root)
            
            # Remove comments before extracting class name to avoid false matches
            content_no_comments = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
            content_no_comments = re.sub(r'//.*$', '', content_no_comments, flags=re.MULTILINE)
            
            # Extract class name - look for public/private class declaration
            class_match = re.search(r'(public|private|protected)?\s*(abstract|final|static)?\s*class\s+(\w+)', content_no_comments)
            class_name = class_match.group(3) if class_match else "Unknown"
            
            # Find all methods (simplified pattern)
            # This pattern looks for method declarations
            method_pattern = r'(public|protected|private|static|\s)+[\w<>\[\]]+\s+(\w+)\s*\([^)]*\)\s*(?:throws\s+[\w\s,]+)?\s*\{'
            
            for method_match in re.finditer(method_pattern, content):
                method_name = method_match.group(2)
                
                # Skip Java reserved keywords that shouldn't be method names
                java_keywords = {
                    'if', 'while', 'for', 'switch', 'catch', 'synchronized', 'try', 'else',
                    'return', 'break', 'continue', 'throw', 'throws', 'new', 'class',
                    'interface', 'enum', 'extends', 'implements', 'package', 'import',
                    'abstract', 'assert', 'boolean', 'byte', 'case', 'char', 'const',
                    'default', 'do', 'double', 'final', 'finally', 'float', 'goto',
                    'instanceof', 'int', 'long', 'native', 'short', 'static', 'strictfp',
                    'super', 'this', 'transient', 'void', 'volatile'
                }
                if method_name in java_keywords:
                    continue
                
                # Find the method body
                method_start = method_match.end() - 1  # Start at opening brace
                method_body = self._extract_method_body(content, method_start)
                
                if method_body:
                    complexity = self._calculate_complexity(method_body)
                    complexities.append({
                        "method": f"{class_name}.{method_name}",
                        "file": str(relative_path),
                        "complexity": complexity
                    })
        
        except Exception as e:
            print(f"    Warning: Error analyzing {file_path}: {e}")
        
        return complexities
    
    def _extract_method_body(self, content: str, start_pos: int) -> str:
        """Extract method body by matching braces, handling Java strings and chars."""
        brace_count = 0
        in_double_quote_string = False
        in_single_quote_char = False
        in_multiline_comment = False
        in_single_line_comment = False
        escape_next = False
        
        method_body = []
        
        for i in range(start_pos, len(content)):
            char = content[i]
            next_char = content[i + 1] if i + 1 < len(content) else ''
            
            # Handle escape sequences
            if escape_next:
                escape_next = False
                method_body.append(char)
                continue
            
            # Handle comments (only when not in strings)
            if not in_double_quote_string and not in_single_quote_char:
                # Start of single-line comment
                if char == '/' and next_char == '/' and not in_multiline_comment:
                    in_single_line_comment = True
                    method_body.append(char)
                    continue
                
                # End of single-line comment
                if in_single_line_comment and char == '\n':
                    in_single_line_comment = False
                    method_body.append(char)
                    continue
                
                # Start of multi-line comment
                if char == '/' and next_char == '*' and not in_single_line_comment:
                    in_multiline_comment = True
                    method_body.append(char)
                    continue
                
                # End of multi-line comment
                if in_multiline_comment and char == '*' and next_char == '/':
                    method_body.append(char)
                    method_body.append(next_char)
                    in_multiline_comment = False
                    continue
            
            # Skip processing if in a comment
            if in_single_line_comment or in_multiline_comment:
                method_body.append(char)
                continue
            
            # Handle escape character
            if char == '\\' and (in_double_quote_string or in_single_quote_char):
                escape_next = True
                method_body.append(char)
                continue
            
            # Handle double-quoted strings
            if char == '"' and not in_single_quote_char:
                in_double_quote_string = not in_double_quote_string
                method_body.append(char)
                continue
            
            # Handle single-quoted characters
            if char == "'" and not in_double_quote_string:
                in_single_quote_char = not in_single_quote_char
                method_body.append(char)
                continue
            
            # Count braces only when not in strings or comments
            if not in_double_quote_string and not in_single_quote_char:
                if char == '{':
                    brace_count += 1
                elif char == '}':
                    brace_count -= 1
                    if brace_count == 0:
                        return ''.join(method_body)
            
            method_body.append(char)
        
        return ''.join(method_body)
    
    def _calculate_complexity(self, method_body: str) -> int:
        """
        Calculate cyclomatic complexity for a method body.
        Complexity = 1 + number of decision points
        """
        # Start with base complexity of 1
        complexity = 1
        
        # Remove strings and comments to avoid false positives
        # Remove strings
        method_body = re.sub(r'"[^"]*"', '', method_body)
        # Remove single-line comments
        method_body = re.sub(r'//.*$', '', method_body, flags=re.MULTILINE)
        # Remove multi-line comments
        method_body = re.sub(r'/\*.*?\*/', '', method_body, flags=re.DOTALL)
        
        # Count decision points
        # if statements
        complexity += len(re.findall(r'\bif\s*\(', method_body))
        # for loops
        complexity += len(re.findall(r'\bfor\s*\(', method_body))
        # while loops
        complexity += len(re.findall(r'\bwhile\s*\(', method_body))
        # case statements (each case adds 1)
        complexity += len(re.findall(r'\bcase\s+', method_body))
        # catch blocks
        complexity += len(re.findall(r'\bcatch\s*\(', method_body))
        # conditional operators (? :)
        complexity += len(re.findall(r'\?', method_body))
        # logical AND operators in conditions
        complexity += len(re.findall(r'&&', method_body))
        # logical OR operators in conditions
        complexity += len(re.findall(r'\|\|', method_body))
        
        return complexity
    
    def count_threads_per_class(self) -> Dict[str, int]:
        """Count the number of threads started per class."""
        print("  - Counting threads started per class...")
        thread_counts = {}
        
        if not self.src_main_java.exists():
            print(f"    Warning: {self.src_main_java} does not exist")
            return thread_counts
        
        for java_file in self.src_main_java.rglob("*.java"):
            count = self._count_threads_in_file(java_file)
            if count > 0:
                # Get class name from file name
                class_name = java_file.stem
                thread_counts[class_name] = count
        
        # Sort by count (descending)
        sorted_threads = dict(sorted(thread_counts.items(), key=lambda x: x[1], reverse=True))
        print(f"    Found {len(sorted_threads)} classes that start threads")
        return sorted_threads
    
    def _count_threads_in_file(self, file_path: Path) -> int:
        """Count thread creation patterns in a file."""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            count = 0
            
            # Pattern 1: new Thread(...)
            count += len(re.findall(r'new\s+Thread\s*\(', content))
            
            # Pattern 2: ExecutorService.execute() or submit()
            count += len(re.findall(r'\.(execute|submit)\s*\(', content))
            
            # Pattern 3: CompletableFuture.runAsync() or supplyAsync()
            count += len(re.findall(r'CompletableFuture\.(runAsync|supplyAsync)\s*\(', content))
            
            # Pattern 4: thread.start()
            count += len(re.findall(r'\.start\s*\(\s*\)', content))
            
            # Pattern 5: ForkJoinPool
            count += len(re.findall(r'ForkJoinPool', content))
            
            return count
        
        except Exception as e:
            print(f"    Warning: Error reading {file_path}: {e}")
            return 0
    
    def extract_external_libraries(self) -> List[Dict[str, str]]:
        """Extract external libraries from build.gradle."""
        print("  - Extracting external libraries from build.gradle...")
        libraries = []
        
        if not self.build_gradle.exists():
            print(f"    Warning: {self.build_gradle} does not exist")
            return libraries
        
        try:
            with open(self.build_gradle, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Find dependencies block
            deps_match = re.search(r'dependencies\s*\{([^}]*)\}', content, re.DOTALL)
            if not deps_match:
                print("    Warning: Could not find dependencies block")
                return libraries
            
            deps_content = deps_match.group(1)
            
            # Pattern to match dependency declarations
            # Matches: implementation 'group:artifact:version'
            dep_pattern = r"(implementation|testImplementation|runtimeOnly|compileOnly|api)\s+['\"]([^'\"]+)['\"]"
            
            for match in re.finditer(dep_pattern, deps_content):
                dep_type = match.group(1)
                dep_string = match.group(2)
                
                # Parse group:artifact:version
                parts = dep_string.split(':')
                if len(parts) >= 2:
                    library = {
                        "group": parts[0],
                        "artifact": parts[1],
                        "version": parts[2] if len(parts) > 2 else "unknown",
                        "scope": dep_type
                    }
                    libraries.append(library)
            
            print(f"    Found {len(libraries)} external libraries")
        
        except Exception as e:
            print(f"    Warning: Error reading {self.build_gradle}: {e}")
        
        return libraries
    
    def run_spotbugs_analysis(self) -> Dict[str, Any]:
        """Run SpotBugs analysis using Gradle and extract metrics."""
        print("  - Running SpotBugs analysis...")
        
        result = {
            "status": "not_run",
            "total_bugs": 0,
            "bugs_by_priority": {},
            "bugs_by_category": {},
            "note": ""
        }
        
        try:
            # Check if gradlew exists
            gradlew = self.project_root / "gradlew"
            if not gradlew.exists():
                result["note"] = "gradlew not found in project root"
                print(f"    Warning: {result['note']}")
                return result
            
            # Run SpotBugs - compile first, then analyze
            print("    Compiling classes for SpotBugs...")
            compile_cmd = [str(gradlew), "compileJava", "--quiet"]
            compile_result = subprocess.run(
                compile_cmd,
                cwd=self.project_root,
                capture_output=True,
                text=True,
                timeout=300
            )
            
            # Check if compilation failed (may fail due to missing dependencies)
            if compile_result.returncode != 0:
                result["status"] = "compilation_failed"
                result["note"] = "Compilation failed - possibly due to missing dependencies"
                print(f"    Warning: {result['note']}")
                return result
            
            print("    Running SpotBugs on compiled classes...")
            spotbugs_cmd = [str(gradlew), "spotbugsMain", "--quiet"]
            spotbugs_result = subprocess.run(
                spotbugs_cmd,
                cwd=self.project_root,
                capture_output=True,
                text=True,
                timeout=300
            )
            
            # SpotBugs may return non-zero if bugs are found, which is OK
            # Parse the XML report if it exists
            spotbugs_xml = self.project_root / "build" / "reports" / "spotbugs" / "main.xml"
            
            if spotbugs_xml.exists():
                result = self._parse_spotbugs_xml(spotbugs_xml)
                result["status"] = "completed"
                print(f"    SpotBugs analysis complete: {result['total_bugs']} bugs found")
            else:
                result["status"] = "no_report"
                result["note"] = "SpotBugs ran but no report was generated"
                print(f"    Warning: {result['note']}")
        
        except subprocess.TimeoutExpired:
            result["status"] = "timeout"
            result["note"] = "SpotBugs analysis timed out"
            print(f"    Warning: {result['note']}")
        except Exception as e:
            result["status"] = "error"
            result["note"] = f"Error running SpotBugs: {str(e)}"
            print(f"    Warning: {result['note']}")
        
        return result
    
    def _parse_spotbugs_xml(self, xml_file: Path) -> Dict[str, Any]:
        """Parse SpotBugs XML report to extract metrics."""
        result = {
            "total_bugs": 0,
            "bugs_by_priority": {},
            "bugs_by_category": {}
        }
        
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            
            # Count bugs by priority and category
            for bug_instance in root.findall('.//BugInstance'):
                result["total_bugs"] += 1
                
                priority = bug_instance.get('priority', 'unknown')
                category = bug_instance.get('category', 'unknown')
                
                result["bugs_by_priority"][priority] = result["bugs_by_priority"].get(priority, 0) + 1
                result["bugs_by_category"][category] = result["bugs_by_category"].get(category, 0) + 1
        
        except Exception as e:
            print(f"    Warning: Error parsing SpotBugs XML: {e}")
        
        return result


def main():
    """Main entry point."""
    # Get project root (one level up from tools/)
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    
    print(f"Project root: {project_root}")
    print()
    
    # Collect metrics
    collector = JavaMetricsCollector(project_root)
    metrics = collector.collect_all_metrics()
    
    # Ensure reports directory exists
    reports_dir = project_root / "reports"
    reports_dir.mkdir(exist_ok=True)
    
    # Write metrics to JSON
    output_file = reports_dir / "metrics.json"
    print()
    print(f"Writing metrics to {output_file}...")
    
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(metrics, f, indent=2)
    
    print()
    print("=" * 60)
    print("METRICS SUMMARY")
    print("=" * 60)
    print(f"Total packages: {metrics['summary']['total_packages']}")
    print(f"Total LOC: {metrics['summary']['total_loc']}")
    print(f"Methods analyzed: {len(metrics['cyclomatic_complexity_top_20'])}")
    print(f"Classes with threads: {metrics['summary']['total_classes_with_threads']}")
    print(f"External libraries: {metrics['summary']['total_external_libraries']}")
    print(f"SpotBugs bugs found: {metrics['summary']['spotbugs_bugs_found']}")
    print("=" * 60)
    print()
    print(f"✓ Metrics saved to: {output_file}")


if __name__ == "__main__":
    main()
