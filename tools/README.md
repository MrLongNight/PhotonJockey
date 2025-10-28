# Code Metrics Tool

This directory contains tools for collecting code metrics from the PhotonJockey (LightBeat) project.

## Usage

Run the metrics collection tool:

```bash
# Using shell script
./tools/run_metrics.sh

# Or directly with Python
python3 tools/run_metrics.py
```

## Output

The tool generates `reports/metrics.json` containing:

### Metrics Collected

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

## Requirements

- Python 3.6 or higher
- Java 17 or higher (for running Gradle)
- Gradle (wrapper included in project)

## Notes

- The SpotBugs analysis requires the project to compile successfully
- If dependencies are missing, SpotBugs will be skipped but other metrics will still be collected
- The tool automatically handles errors and provides informative messages

## Example Output Structure

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
