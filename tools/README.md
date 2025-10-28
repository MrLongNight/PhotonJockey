# Tools Directory

This directory contains utility scripts for analyzing and managing the PhotonJockey codebase.

## analyze_codebase.py

Python script that performs static code analysis on the Java codebase.

### Purpose

- Scans the `src/main/java` directory for all Java source files
- Extracts package structure, class information, and dependencies
- Identifies thread creation points and concurrency patterns
- Parses `build.gradle` to extract external dependencies
- Generates documentation and visualization artifacts

### Outputs

1. **docs/codebase_overview.md**: A comprehensive markdown document containing:
   - Complete package structure
   - List of main entry point classes
   - Class responsibilities (extracted from Javadoc)
   - Thread creation and concurrency hot spots
   - External dependencies from build.gradle
   - Analysis of package coupling and potential hot spots

2. **docs/diagrams/dependency.dot**: A Graphviz DOT file showing package-to-package dependencies

### Usage

```bash
# Run from the project root
python3 tools/analyze_codebase.py
```

### Requirements

- Python 3.12 or higher
- No external Python dependencies required (uses only standard library)

### CI Integration

The script is automatically run by the "Static Code Analysis" GitHub Actions workflow (`.github/workflows/static_analysis.yml`) on every push and pull request. The generated artifacts are uploaded and available for download from the workflow run.

### Development

To add new analysis features:

1. Add methods to the `CodebaseAnalyzer` class
2. Update the `analyze()` method to call your new methods
3. Update the output generation methods (`generate_overview_markdown()` and/or `generate_dependency_dot()`)
4. Test locally before committing

### Notes

- The script is designed to work even if the Java code doesn't compile
- Analysis is based on static text parsing, not bytecode analysis
- Thread detection looks for common patterns (ExecutorService, new Thread, etc.)
- Package dependencies are based on import statements

## generate_metrics.py

Python script that generates detailed code metrics for refactoring analysis.

### Purpose

- Analyzes Java source files for complexity metrics
- Identifies refactoring candidates based on multiple criteria
- Generates JSON report with detailed metrics per class
- Supports prioritization of refactoring work

### Outputs

**reports/metrics.json**: A JSON file containing:
- Lines of code per class
- Method counts and method lengths
- Field counts and import dependencies
- Thread usage and synchronization patterns
- Complexity scores for prioritization
- Refactor indicators (manual threads, long methods, etc.)

### Usage

```bash
# Run from the project root
python3 tools/generate_metrics.py

# Or use the Gradle task
./gradlew generateCodeMetrics
```

### Metrics Calculated

- **Lines of Code (LOC)**: Non-comment, non-blank lines
- **Method Count**: Number of methods in the class
- **Field Count**: Number of class fields
- **Thread Usage**: Manual thread creation (`new Thread()`)
- **Executor Usage**: Use of ExecutorService and related classes
- **Synchronized Blocks**: Synchronization complexity
- **Complexity Score**: Weighted combination of above factors
- **Long Methods**: Methods exceeding 50 lines

### Complexity Score Formula

```
complexity = (LOC / 10) + (methods × 3) + fields + (threads × 10) + (executors × 5) + (sync_blocks × 5)
```

### Requirements

- Python 3.6 or higher
- No external dependencies (uses standard library only)

### Integration with Refactoring

The metrics.json output is used to generate the refactoring plan in `docs/refactor_plan.md`. Classes are prioritized by complexity score:

- **High Priority**: Complexity > 150
- **Medium Priority**: Complexity 80-150
- **Low Priority**: Complexity < 80
