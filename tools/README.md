# Tools Directory

This directory contains utility scripts for analyzing and managing the PhotonJockey codebase.

## Available Tools

### 1. analyze_codebase.py

Python script that performs static code analysis on the Java codebase.

#### Purpose

- Scans the `src/main/java` directory for all Java source files
- Extracts package structure, class information, and dependencies
- Identifies thread creation points and concurrency patterns
- Parses `build.gradle` to extract external dependencies
- Generates documentation and visualization artifacts

#### Outputs

1. **docs/codebase_overview.md**: A comprehensive markdown document containing:
   - Complete package structure
   - List of main entry point classes
   - Class responsibilities (extracted from Javadoc)
   - Thread creation and concurrency hot spots
   - External dependencies from build.gradle
   - Analysis of package coupling and potential hot spots

2. **docs/diagrams/dependency.dot**: A Graphviz DOT file showing package-to-package dependencies

#### Usage

```bash
python3 tools/analyze_codebase.py
```

---

### 2. generate_metrics.py

Python script for generating detailed code metrics for refactoring analysis.

#### Purpose

- Analyzes individual Java classes for complexity metrics
- Generates class-level statistics including LOC, method count, field count
- Identifies potential refactoring candidates
- Provides detailed metrics for TG1.4 refactoring plan

#### Outputs

**reports/metrics.json**: A detailed JSON report containing per-class metrics with structure:
```json
{
  "analysis_date": "ISO-8601 timestamp",
  "total_classes": 74,
  "classes": [
    {
      "class_name": "fully.qualified.ClassName",
      "file_path": "relative/path/to/File.java",
      "metrics": {
        "lines_of_code": 516,
        "method_count": 28,
        "field_count": 92,
        "import_count": 30,
        "thread_usage": 0,
        "executor_usage": 0,
        "synchronized_blocks": 0,
        "complexity_score": 227,
        "long_methods": 0
      },
      "methods": [...]
    }
  ]
}
```

#### Usage

```bash
python3 tools/generate_metrics.py

# Or via Gradle task
./gradlew generateCodeMetrics
```

---

### 3. Code Metrics Collection Tool (TG1.3)

Automated tool for collecting comprehensive code metrics including SpotBugs analysis.

#### Scripts

- **run_metrics.sh** - Shell wrapper script
- **run_metrics.py** - Python metrics collector

#### Purpose

- Calculates Lines of Code (LOC) per package
- Computes cyclomatic complexity for methods (top 20)
- Detects thread creation patterns across classes
- Extracts external library dependencies
- Integrates SpotBugs static analysis

#### Metrics Collected

1. **Lines of Code (LOC) per package**
   - Non-empty, non-comment lines counted per Java package
   - Excludes blank lines and comments (both single-line and multi-line)

2. **Cyclomatic Complexity (Top 20 methods)**
   - McCabe cyclomatic complexity calculated for each method
   - Decision points counted: if, for, while, case, catch, ternary operators, && and ||
   - Sorted by complexity (highest first)
   - Includes method name, file path, and complexity value

3. **Thread Creation Statistics**
   - Number of threads started per class
   - Detects patterns:
     - `new Thread(...)`
     - `ExecutorService.execute()` / `submit()`
     - `CompletableFuture.runAsync()` / `supplyAsync()`
     - `.start()` method calls
     - `ForkJoinPool` usage

4. **External Libraries**
   - List of all external dependencies from build.gradle
   - Includes group, artifact, version, and scope
   - Covers implementation, testImplementation, and other dependency types

5. **SpotBugs Static Analysis**
   - Runs SpotBugs on compiled classes (if compilation succeeds)
   - Reports total bugs found
   - Categorizes by priority and category
   - Gracefully handles compilation failures

#### Usage

```bash
# Using shell script
./tools/run_metrics.sh

# Or directly with Python
python3 tools/run_metrics.py
```

#### Output Structure

The tool generates `reports/metrics_tg13.json` with the following structure:

```json
{
  "loc_per_package": {
    "package.name": 123
  },
  "cyclomatic_complexity_top_20": [
    {
      "method": "ClassName.methodName",
      "file": "src/main/java/...",
      "complexity": 15
    }
  ],
  "threads_per_class": {
    "ClassName": 3
  },
  "external_libraries": [
    {
      "group": "org.example",
      "artifact": "library",
      "version": "1.0.0",
      "scope": "implementation"
    }
  ],
  "spotbugs_analysis": {
    "status": "completed",
    "total_bugs": 5,
    "bugs_by_priority": {},
    "bugs_by_category": {}
  },
  "summary": {
    "total_packages": 16,
    "total_loc": 5439,
    "total_classes_with_threads": 10,
    "total_external_libraries": 10,
    "spotbugs_bugs_found": 5
  }
}
```

---

## Requirements

- Python 3.6 or higher
- Java 17 or higher (for running Gradle and SpotBugs)
- Gradle (wrapper included in project)

## Notes

- The SpotBugs analysis requires the project to compile successfully
- If dependencies are missing, SpotBugs will be skipped but other metrics will still be collected
- All tools automatically handle errors and provide informative messages
- Different tools output to different files to avoid conflicts:
  - `analyze_codebase.py` → `docs/codebase_overview.md`
  - `generate_metrics.py` → `reports/metrics.json`
  - `run_metrics.py` → `reports/metrics_tg13.json`
