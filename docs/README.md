# PhotonJockey Documentation

Welcome to the PhotonJockey documentation! This directory contains all project documentation organized by topic.

## 📄 Root Level Documentation
- **[CODE_ANALYSIS.md](CODE_ANALYSIS.md)** - Comprehensive code analysis and recommendations (latest: 2025-11-06)
- **[DOCUMENTATION_GUIDELINES.md](DOCUMENTATION_GUIDELINES.md)** - Complete documentation standards and conventions
- **[DOCUMENTATION_GUIDELINES_COPILOT.md](DOCUMENTATION_GUIDELINES_COPILOT.md)** - Quick reference for AI agents
- **[COPILOT_CUSTOM_INSTRUCTIONS.md](COPILOT_CUSTOM_INSTRUCTIONS.md)** - Complete instructions for Copilot Custom Instructions

## 📚 Documentation Structure

### 📋 [Project Planning & Status](project/)
Project management, planning, and implementation tracking:
- **[01-PROJECT_PLAN.md](project/01-PROJECT_PLAN.md)** - Master project plan with all TaskGroups
- **[02-IMPLEMENTATION_STATUS.md](project/02-IMPLEMENTATION_STATUS.md)** - Current implementation status of all tasks
- **[03-IMPLEMENTED_TASKS.md](project/03-IMPLEMENTED_TASKS.md)** - Summary of completed tasks

#### Refactoring Documentation
- **[01-CODEBASE_OVERVIEW.md](project/refactor/01-CODEBASE_OVERVIEW.md)** - Generated codebase analysis
- **[02-REFACTOR_PLAN.md](project/refactor/02-REFACTOR_PLAN.md)** - Refactoring strategy and priorities

### ✅ [Task Completion Summaries](completion/)
Detailed completion reports for each TaskGroup:
- **[01-TG1.1_COMPLETION.md](completion/01-TG1.1_COMPLETION.md)** - Code style & project conventions
- **[02-TG2.4_COMPLETION.md](completion/02-TG2.4_COMPLETION.md)** - Audio profiles & presets
- **[03-TG2.5_COMPLETION.md](completion/03-TG2.5_COMPLETION.md)** - Audio visualizer UI
- **[04-TG3.5_COMPLETION.md](completion/04-TG3.5_COMPLETION.md)** - Smart mapping tool UI

### 📖 [User & Developer Guides](guides/)

#### Testing
- **[01-TESTING_QUICKSTART_DE.md](guides/testing/01-TESTING_QUICKSTART_DE.md)** - Quick start guide for testing (German)
- **[02-TESTING_GUIDE_DE.md](guides/testing/02-TESTING_GUIDE_DE.md)** - Comprehensive testing guide (German)

#### Troubleshooting
- **[01-TROUBLESHOOTING_DE.md](guides/troubleshooting/01-TROUBLESHOOTING_DE.md)** - Problem solving and diagnostics (German)
- **[02-IMAGE_FIX_NOTES.md](guides/troubleshooting/02-IMAGE_FIX_NOTES.md)** - Image display issue fix documentation
- **[03-IMAGE_FIX_NOTES_DE.md](guides/troubleshooting/03-IMAGE_FIX_NOTES_DE.md)** - Bildanzeige-Problem Lösung (German)

#### User Interface
- **[01-UI_OVERVIEW_DE.md](guides/ui/01-UI_OVERVIEW_DE.md)** - UI layout and components overview (German)

### 🎵 [Feature Documentation](features/)
Detailed documentation for specific features:
- **[01-AUDIO_PROFILES.md](features/01-AUDIO_PROFILES.md)** - Audio profile system for different music genres
- **[02-AUDIO_VISUALIZER.md](features/02-AUDIO_VISUALIZER.md)** - Real-time audio visualization dashboard
- **[03-SMART_MAPPING_TOOL.md](features/03-SMART_MAPPING_TOOL.md)** - Light mapping configuration tool

### 🔧 [Development Documentation](development/)
Information for developers:
- **[01-BUILD_INSTRUCTIONS.md](development/01-BUILD_INSTRUCTIONS.md)** - How to build and run PhotonJockey
- **[02-CODING_CONVENTIONS.md](development/02-CODING_CONVENTIONS.md)** - Code style, branching, and commit conventions
- **[ARBEITSANWEISUNG_JULES_UI_INTEGRATION.md](development/ARBEITSANWEISUNG_JULES_UI_INTEGRATION.md)** - UI integration work instructions for Jules agent
- **[UI_INTEGRATION_SUMMARY.md](development/UI_INTEGRATION_SUMMARY.md)** - Executive summary of UI integration project
- **[UI_INTEGRATION_README.md](development/UI_INTEGRATION_README.md)** - Quick start guide for UI integration
- **[diagrams/](development/diagrams/)** - Architecture and dependency diagrams

### ⚖️ [Legal Documentation](legal/)
License and security information:
- **[01-LICENSE.md](legal/01-LICENSE.md)** - Project license
- **[02-SECURITY.md](legal/02-SECURITY.md)** - Security policy and vulnerability reporting
- **[03-THIRD_PARTY_LICENSES.md](legal/03-THIRD_PARTY_LICENSES.md)** - Third-party dependency licenses

### 📦 [Archive](archive/)
Historical documentation for reference:
- **[01-DOCUMENTATION_REORGANIZATION_SUMMARY.md](archive/01-DOCUMENTATION_REORGANIZATION_SUMMARY.md)** - Previous documentation reorganization (2025-11-01)
- **[02-UI_MODERNIZATION_ANALYSIS_COMPLETE.md](archive/02-UI_MODERNIZATION_ANALYSIS_COMPLETE.md)** - UI modernization analysis (2025-11-04)

### 📝 [Changelog](changelog/)
Change summaries and migration notes:
- **[01-WORKFLOW_CLEANUP_SUMMARY.md](changelog/01-WORKFLOW_CLEANUP_SUMMARY.md)** - Workflow cleanup and consolidation

## 🚀 Quick Links

### For Users
- **Getting Started**: See [README.md](../README.md) in the root directory
- **Building**: [development/01-BUILD_INSTRUCTIONS.md](development/01-BUILD_INSTRUCTIONS.md)
- **Testing**: [guides/testing/01-TESTING_QUICKSTART_DE.md](guides/testing/01-TESTING_QUICKSTART_DE.md)
- **Troubleshooting**: [guides/troubleshooting/01-TROUBLESHOOTING_DE.md](guides/troubleshooting/01-TROUBLESHOOTING_DE.md)

### For Developers
- **Project Status**: [project/02-IMPLEMENTATION_STATUS.md](project/02-IMPLEMENTATION_STATUS.md)
- **Coding Standards**: [development/02-CODING_CONVENTIONS.md](development/02-CODING_CONVENTIONS.md)
- **Project Plan**: [project/01-PROJECT_PLAN.md](project/01-PROJECT_PLAN.md)
- **Refactoring Plan**: [project/refactor/02-REFACTOR_PLAN.md](project/refactor/02-REFACTOR_PLAN.md)

### For Contributors
- **Implementation Status**: [project/02-IMPLEMENTATION_STATUS.md](project/02-IMPLEMENTATION_STATUS.md)
- **Coding Conventions**: [development/02-CODING_CONVENTIONS.md](development/02-CODING_CONVENTIONS.md)
- **Build Instructions**: [development/01-BUILD_INSTRUCTIONS.md](development/01-BUILD_INSTRUCTIONS.md)

## 📝 Documentation Conventions

### Naming Convention
- Numbered prefixes (01-, 02-, etc.) for ordering within folders
- UPPERCASE for document titles with underscores for separation
- Language suffix (_DE) for German documents
- Folder names in lowercase

### File Organization
- **project/** - Planning, status tracking, and refactoring documentation
- **completion/** - TaskGroup completion summaries (historical records)
- **guides/** - How-to guides for users and developers
- **features/** - Feature-specific technical documentation
- **development/** - Development setup and standards
- **legal/** - Legal and security documentation
- **archive/** - Historical documentation for reference
- **changelog/** - Change summaries and migration notes

## 🔄 Updating Documentation

When updating documentation:
1. Place the document in the appropriate category folder
2. Use the numbered prefix to maintain ordering
3. Update cross-references if file paths change
4. Update this README.md if adding new major documents
5. Follow the naming conventions above

## 📞 Need Help?

- **GitHub Issues**: [https://github.com/MrLongNight/PhotonJockey/issues](https://github.com/MrLongNight/PhotonJockey/issues)
- **Project Lead**: @MrLongNight

---

*Last Updated: 2025-11-06*
