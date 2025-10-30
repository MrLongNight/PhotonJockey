#!/usr/bin/env python3
"""
Static code analysis tool for PhotonJockey project.

This script scans the Java codebase and generates:
- docs/Reports/CODEBASE_UEBERSICHT.md: A comprehensive overview of the codebase
- docs/Diagramme/dependency.dot: A Graphviz diagram showing package dependencies
"""

import os
import re
from pathlib import Path
from collections import defaultdict
from typing import Dict, List, Set, Tuple


class CodebaseAnalyzer:
    """Analyzes Java codebase structure and dependencies."""
    
    def __init__(self, src_dir: str, build_file: str):
        self.src_dir = Path(src_dir)
        self.build_file = Path(build_file)
        self.packages: Dict[str, List[str]] = defaultdict(list)
        self.classes: Dict[str, Dict] = {}
        self.imports: Dict[str, Set[str]] = defaultdict(set)
        self.dependencies: List[str] = []
        self.thread_creation_points: List[Tuple[str, int, str]] = []
        self.main_classes: List[str] = []
        
    def analyze(self):
        """Run the complete analysis."""
        print("Starting codebase analysis...")
        self._scan_java_files()
        self._extract_dependencies()
        print(f"Analysis complete. Found {len(self.classes)} classes in {len(self.packages)} packages.")
        
    def _scan_java_files(self):
        """Scan all Java source files."""
        java_files = list(self.src_dir.rglob("*.java"))
        print(f"Scanning {len(java_files)} Java files...")
        
        for java_file in java_files:
            self._analyze_java_file(java_file)
    
    def _analyze_java_file(self, file_path: Path):
        """Analyze a single Java file."""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Extract package name
            package_match = re.search(r'package\s+([\w.]+)\s*;', content)
            if not package_match:
                return
            
            package_name = package_match.group(1)
            
            # Extract class name and type
            class_matches = re.finditer(
                r'(?:public\s+)?(?:abstract\s+)?(class|interface|enum)\s+(\w+)',
                content
            )
            
            for match in class_matches:
                class_type = match.group(1)
                class_name = match.group(2)
                full_class_name = f"{package_name}.{class_name}"
                
                # Extract Javadoc comment if present
                javadoc = self._extract_javadoc(content, match.start())
                
                # Check if it's a main class
                is_main = 'public static void main(String' in content
                if is_main:
                    self.main_classes.append(full_class_name)
                
                # Store class information
                self.classes[full_class_name] = {
                    'name': class_name,
                    'type': class_type,
                    'package': package_name,
                    'javadoc': javadoc,
                    'file': str(file_path.relative_to(self.src_dir.parent)),
                    'is_main': is_main
                }
                
                self.packages[package_name].append(class_name)
            
            # Extract imports
            import_matches = re.finditer(r'import\s+([\w.]+)\s*;', content)
            full_class = f"{package_name}.{Path(file_path).stem}"
            for import_match in import_matches:
                imported = import_match.group(1)
                # Only track imports from our own codebase
                if imported.startswith('pw.wunderlich') or imported.startswith('org.jitsi'):
                    # Extract package from import
                    imported_package = '.'.join(imported.split('.')[:-1])
                    if imported_package and imported_package != package_name:
                        self.imports[package_name].add(imported_package)
            
            # Find thread creation points
            self._find_threading_patterns(content, file_path)
            
        except Exception as e:
            print(f"Error analyzing {file_path}: {e}")
    
    def _extract_javadoc(self, content: str, class_pos: int) -> str:
        """Extract Javadoc comment before a class definition."""
        # Look backwards from class position for javadoc
        before_class = content[:class_pos]
        javadoc_match = re.search(r'/\*\*(.*?)\*/', before_class, re.DOTALL)
        if javadoc_match:
            # Get the last javadoc comment before the class
            all_javadocs = list(re.finditer(r'/\*\*(.*?)\*/', before_class, re.DOTALL))
            if all_javadocs:
                last_javadoc = all_javadocs[-1]
                # Check if it's close to the class (within 200 chars)
                if class_pos - last_javadoc.end() < 200:
                    javadoc_text = last_javadoc.group(1)
                    # Clean up javadoc: remove * at line starts, trim whitespace
                    lines = javadoc_text.split('\n')
                    cleaned_lines = []
                    for line in lines:
                        line = re.sub(r'^\s*\*\s?', '', line)
                        line = line.strip()
                        if line and not line.startswith('@'):
                            cleaned_lines.append(line)
                    return ' '.join(cleaned_lines)
        return ""
    
    def _find_threading_patterns(self, content: str, file_path: Path):
        """Find thread creation and concurrent execution patterns."""
        lines = content.split('\n')
        
        patterns = [
            (r'new\s+Thread\s*\(', 'new Thread'),
            (r'Executors\.new\w+ThreadPool', 'ExecutorService'),
            (r'ScheduledExecutorService', 'ScheduledExecutorService'),
            (r'ExecutorService', 'ExecutorService'),
            (r'implements\s+Runnable', 'implements Runnable'),
            (r'extends\s+Thread', 'extends Thread'),
            (r'CompletableFuture', 'CompletableFuture'),
            (r'@Async', '@Async annotation'),
        ]
        
        for line_num, line in enumerate(lines, 1):
            for pattern, description in patterns:
                if re.search(pattern, line):
                    self.thread_creation_points.append((
                        str(file_path.relative_to(self.src_dir.parent)),
                        line_num,
                        description
                    ))
    
    def _extract_dependencies(self):
        """Extract external dependencies from build.gradle."""
        if not self.build_file.exists():
            print(f"Warning: {self.build_file} not found")
            return
        
        try:
            with open(self.build_file, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Extract implementation, testImplementation, etc.
            dep_pattern = r"(?:implementation|testImplementation|runtimeOnly|testRuntimeOnly)\s+['\"]([^'\"]+)['\"]"
            matches = re.finditer(dep_pattern, content)
            
            for match in matches:
                dep = match.group(1)
                self.dependencies.append(dep)
            
            print(f"Found {len(self.dependencies)} dependencies in build.gradle")
            
        except Exception as e:
            print(f"Error reading build.gradle: {e}")
    
    def generate_overview_markdown(self, output_path: str):
        """Generate the codebase overview markdown file."""
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write("# PhotonJockey Codebase Overview\n\n")
            f.write("*Generated by tools/analyze_codebase.py*\n\n")
            
            # Table of contents
            f.write("## Table of Contents\n\n")
            f.write("1. [Package Structure](#package-structure)\n")
            f.write("2. [Main Classes](#main-classes)\n")
            f.write("3. [Class Responsibilities](#class-responsibilities)\n")
            f.write("4. [Thread Creation Points](#thread-creation-points)\n")
            f.write("5. [External Dependencies](#external-dependencies)\n")
            f.write("6. [Hot Spots](#hot-spots)\n\n")
            
            # Package structure
            f.write("## Package Structure\n\n")
            f.write(f"The codebase contains **{len(self.packages)} packages** with **{len(self.classes)} classes**.\n\n")
            
            sorted_packages = sorted(self.packages.keys())
            for package in sorted_packages:
                classes = self.packages[package]
                f.write(f"### `{package}`\n\n")
                f.write(f"- **Classes**: {len(classes)}\n")
                f.write(f"- **Types**: {', '.join(sorted(set(classes)))}\n\n")
            
            # Main classes / entry points
            f.write("## Main Classes\n\n")
            f.write("Classes with `public static void main(String[] args)` methods:\n\n")
            
            if self.main_classes:
                for main_class in sorted(self.main_classes):
                    if main_class in self.classes:
                        class_info = self.classes[main_class]
                        f.write(f"### `{main_class}`\n\n")
                        f.write(f"- **File**: `{class_info['file']}`\n")
                        if class_info['javadoc']:
                            f.write(f"- **Description**: {class_info['javadoc']}\n")
                        f.write("\n")
            else:
                f.write("*No main classes found.*\n\n")
            
            # Class responsibilities
            f.write("## Class Responsibilities\n\n")
            f.write("Key classes and their responsibilities (from Javadoc):\n\n")
            
            # Group by package for better organization
            for package in sorted_packages:
                classes_with_docs = [
                    (cls, self.classes[f"{package}.{cls}"])
                    for cls in self.packages[package]
                    if f"{package}.{cls}" in self.classes 
                    and self.classes[f"{package}.{cls}"].get('javadoc')
                ]
                
                if classes_with_docs:
                    f.write(f"### Package: `{package}`\n\n")
                    for class_name, class_info in classes_with_docs:
                        f.write(f"#### `{class_name}`\n\n")
                        f.write(f"{class_info['javadoc']}\n\n")
            
            # Thread creation points
            f.write("## Thread Creation Points\n\n")
            f.write("Locations where threads or concurrent execution is used:\n\n")
            
            if self.thread_creation_points:
                # Group by file for better readability
                by_file = defaultdict(list)
                for file_path, line_num, description in self.thread_creation_points:
                    by_file[file_path].append((line_num, description))
                
                for file_path in sorted(by_file.keys()):
                    f.write(f"### `{file_path}`\n\n")
                    for line_num, description in sorted(by_file[file_path]):
                        f.write(f"- Line {line_num}: {description}\n")
                    f.write("\n")
            else:
                f.write("*No explicit thread creation points found.*\n\n")
            
            # External dependencies
            f.write("## External Dependencies\n\n")
            f.write("External libraries used in the project (from build.gradle):\n\n")
            
            if self.dependencies:
                # Group dependencies by type
                runtime_deps = [d for d in self.dependencies if 'test' not in d.lower()]
                test_deps = [d for d in self.dependencies if 'test' in d.lower()]
                
                if runtime_deps:
                    f.write("### Runtime Dependencies\n\n")
                    for dep in sorted(set(runtime_deps)):
                        f.write(f"- `{dep}`\n")
                    f.write("\n")
                
                if test_deps:
                    f.write("### Test Dependencies\n\n")
                    for dep in sorted(set(test_deps)):
                        f.write(f"- `{dep}`\n")
                    f.write("\n")
            else:
                f.write("*No dependencies found in build.gradle.*\n\n")
            
            # Hot spots
            f.write("## Hot Spots\n\n")
            f.write("Potential areas of interest for optimization or careful review:\n\n")
            
            f.write("### Concurrency\n\n")
            if self.thread_creation_points:
                thread_files = set(fp for fp, _, _ in self.thread_creation_points)
                f.write(f"- **{len(self.thread_creation_points)}** threading-related code locations found across **{len(thread_files)}** files\n")
                f.write("- Review thread safety and synchronization mechanisms\n")
            else:
                f.write("- No explicit threading patterns detected\n")
            f.write("\n")
            
            f.write("### External Dependencies\n\n")
            if self.dependencies:
                f.write(f"- **{len(set(self.dependencies))}** external dependencies\n")
                f.write("- Review for security updates and compatibility\n")
            f.write("\n")
            
            f.write("### Package Coupling\n\n")
            # Find packages with most dependencies
            coupling = [(pkg, len(deps)) for pkg, deps in self.imports.items()]
            coupling.sort(key=lambda x: x[1], reverse=True)
            
            if coupling:
                f.write("Packages with the most dependencies (top 5):\n\n")
                for pkg, dep_count in coupling[:5]:
                    f.write(f"- `{pkg}`: {dep_count} package dependencies\n")
            else:
                f.write("- No inter-package dependencies detected\n")
            f.write("\n")
        
        print(f"Generated overview: {output_path}")
    
    def generate_dependency_dot(self, output_path: str):
        """Generate Graphviz DOT file showing package dependencies."""
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write("digraph PackageDependencies {\n")
            f.write("    rankdir=LR;\n")
            f.write("    node [shape=box, style=rounded];\n")
            f.write("    \n")
            f.write("    // Package nodes\n")
            
            # Shorten package names for readability
            def shorten_package(pkg: str) -> str:
                parts = pkg.split('.')
                if len(parts) > 3:
                    return '...'.join(parts[-2:])
                return pkg
            
            # Add all packages as nodes
            for package in sorted(self.packages.keys()):
                label = shorten_package(package)
                f.write(f'    "{package}" [label="{label}"];\n')
            
            f.write("    \n")
            f.write("    // Package dependencies\n")
            
            # Add edges for dependencies
            for from_pkg, to_pkgs in sorted(self.imports.items()):
                for to_pkg in sorted(to_pkgs):
                    if to_pkg in self.packages:  # Only show internal dependencies
                        f.write(f'    "{from_pkg}" -> "{to_pkg}";\n')
            
            f.write("}\n")
        
        print(f"Generated dependency diagram: {output_path}")


def main():
    """Main entry point."""
    # Determine project root
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    
    src_dir = project_root / "src" / "main" / "java"
    build_file = project_root / "build.gradle"
    
    # Output paths
    overview_md = project_root / "docs" / "Reports" / "CODEBASE_UEBERSICHT.md"
    dependency_dot = project_root / "docs" / "Diagramme" / "dependency.dot"
    
    # Ensure output directories exist
    overview_md.parent.mkdir(parents=True, exist_ok=True)
    dependency_dot.parent.mkdir(parents=True, exist_ok=True)
    
    # Run analysis
    analyzer = CodebaseAnalyzer(str(src_dir), str(build_file))
    analyzer.analyze()
    
    # Generate outputs
    analyzer.generate_overview_markdown(str(overview_md))
    analyzer.generate_dependency_dot(str(dependency_dot))
    
    print("\n✓ Analysis complete!")
    print(f"  - Overview: {overview_md}")
    print(f"  - Dependency diagram: {dependency_dot}")


if __name__ == "__main__":
    main()
