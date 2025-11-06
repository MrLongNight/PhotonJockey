# Documentation Guidelines for PhotonJockey

## Purpose
This document defines the standards and conventions for all documentation in the PhotonJockey repository. Following these guidelines ensures consistency, maintainability, and easy navigation for all users and contributors.

## Core Principles

### 1. Location Rules
**STRICT REQUIREMENT**: Only these markdown files are allowed in the repository root:
- `README.md` - Main project overview and quick start
- `Agents.md` - Agent definitions and workflow

**All other documentation MUST be in the `docs/` directory.**

### 2. Directory Structure

```
docs/
├── CODE_ANALYSIS.md              # Current code analysis reports
├── README.md                     # Documentation index and navigation
├── project/                      # Project planning and status
│   ├── 01-PROJECT_PLAN.md
│   ├── 02-IMPLEMENTATION_STATUS.md
│   ├── 03-IMPLEMENTED_TASKS.md
│   └── refactor/                 # Refactoring plans
│       ├── 01-CODEBASE_OVERVIEW.md
│       └── 02-REFACTOR_PLAN.md
├── completion/                   # Task completion summaries
│   ├── 01-TG1.1_COMPLETION.md
│   ├── 02-TG2.4_COMPLETION.md
│   └── ...
├── guides/                       # User and developer guides
│   ├── testing/
│   │   ├── 01-TESTING_QUICKSTART_DE.md
│   │   └── 02-TESTING_GUIDE_DE.md
│   ├── troubleshooting/
│   │   ├── 01-TROUBLESHOOTING_DE.md
│   │   ├── 02-IMAGE_FIX_NOTES.md
│   │   └── 03-IMAGE_FIX_NOTES_DE.md
│   └── ui/
│       └── 01-UI_OVERVIEW_DE.md
├── features/                     # Feature-specific documentation
│   ├── 01-AUDIO_PROFILES.md
│   ├── 02-AUDIO_VISUALIZER.md
│   └── 03-SMART_MAPPING_TOOL.md
├── development/                  # Development documentation
│   ├── 01-BUILD_INSTRUCTIONS.md
│   ├── 02-CODING_CONVENTIONS.md
│   └── diagrams/
├── legal/                        # Legal and security
│   ├── 01-LICENSE.md
│   ├── 02-SECURITY.md
│   └── 03-THIRD_PARTY_LICENSES.md
├── archive/                      # Historical documentation
│   ├── 01-DOCUMENTATION_REORGANIZATION_SUMMARY.md
│   └── 02-UI_MODERNIZATION_ANALYSIS_COMPLETE.md
└── changelog/                    # Change summaries
    └── 01-WORKFLOW_CLEANUP_SUMMARY.md
```

## Naming Conventions

### File Names
1. **Numbered Prefixes**: Use `01-`, `02-`, `03-` etc. for documents within a category
   - Allows logical ordering
   - Makes sequence clear
   - Example: `01-BUILD_INSTRUCTIONS.md`, `02-CODING_CONVENTIONS.md`

2. **UPPERCASE Titles**: Use UPPERCASE for main document titles
   - Easy to scan
   - Consistent appearance
   - Example: `TESTING_GUIDE_DE.md`, `PROJECT_PLAN.md`

3. **Underscores**: Use underscores to separate words
   - Consistent with project style
   - Better readability than hyphens
   - Example: `IMPLEMENTATION_STATUS.md`, `AUDIO_PROFILES.md`

4. **Language Suffixes**: Add `_DE` for German documents
   - Clear language indication
   - Easy to find translations
   - Example: `TROUBLESHOOTING_DE.md`, `TESTING_GUIDE_DE.md`

### Directory Names
- **Lowercase**: All directory names in lowercase
- **Descriptive**: Names should clearly indicate content
- **Plural**: Use plural for categories (guides, features, not guide, feature)
- Examples: `project`, `completion`, `guides`, `features`

## Document Categories

### 1. Project Documentation (`project/`)
**Purpose**: Project planning, status tracking, and strategic documentation

**When to use**:
- Project plans and roadmaps
- Implementation status tracking
- Task lists and schedules
- Refactoring plans and strategies

**Naming pattern**: `0X-DESCRIPTIVE_NAME.md`

### 2. Completion Summaries (`completion/`)
**Purpose**: Historical records of completed task groups

**When to use**:
- TaskGroup completion reports
- Milestone achievements
- Historical implementation records

**Naming pattern**: `0X-TGX.X_COMPLETION.md`

### 3. User & Developer Guides (`guides/`)
**Purpose**: How-to documentation and tutorials

**Subcategories**:
- `testing/` - Testing guides and quick starts
- `troubleshooting/` - Problem solving and fixes
- `ui/` - User interface documentation

**When to use**:
- Step-by-step instructions
- Troubleshooting guides
- Quick start guides
- Best practices

**Naming pattern**: `0X-GUIDE_NAME_DE.md` (add _DE for German)

### 4. Feature Documentation (`features/`)
**Purpose**: Technical documentation for specific features

**When to use**:
- Feature specifications
- Feature implementation details
- Feature usage documentation

**Naming pattern**: `0X-FEATURE_NAME.md`

### 5. Development Documentation (`development/`)
**Purpose**: Setup, standards, and development processes

**When to use**:
- Build instructions
- Coding conventions
- Architecture diagrams
- Development workflows
- UI integration plans

**Naming pattern**: `0X-DESCRIPTIVE_NAME.md` or descriptive names for specialized docs

### 6. Legal Documentation (`legal/`)
**Purpose**: Licenses, security policies, legal notices

**When to use**:
- License information
- Security policies
- Third-party licenses
- Legal compliance

**Naming pattern**: `0X-DOCUMENT_TYPE.md`

### 7. Archive (`archive/`)
**Purpose**: Historical documentation no longer current but kept for reference

**When to use**:
- Outdated analysis documents
- Superseded plans
- Historical reorganization summaries
- Completed migration documentation

**Naming pattern**: `0X-DESCRIPTIVE_NAME.md`

### 8. Changelog (`changelog/`)
**Purpose**: Change summaries and migration notes

**When to use**:
- Workflow changes
- Major refactoring summaries
- Migration documentation
- Breaking changes

**Naming pattern**: `0X-CHANGE_SUMMARY.md`

## Content Guidelines

### Document Structure
Every documentation file should include:

1. **Title** (H1): Clear, descriptive title
2. **Metadata** (optional): Date, version, author, status
3. **Purpose/Overview**: Brief explanation of document purpose
4. **Content**: Main documentation content
5. **Related Documents**: Links to related documentation
6. **Last Updated**: Date of last update

### Example Template:
```markdown
# Feature Name / Guide Title

**Date**: YYYY-MM-DD  
**Status**: Current / Archived / Draft  
**Version**: X.X.X (if applicable)

## Purpose
Brief explanation of what this document covers.

## Content
[Your main content here]

## Related Documentation
- [Related Doc 1](path/to/doc1.md)
- [Related Doc 2](path/to/doc2.md)

---

*Last Updated: YYYY-MM-DD*
```

### Writing Style
- **Clear and Concise**: Use simple, direct language
- **Structured**: Use headings, lists, and formatting
- **Examples**: Include code examples where relevant
- **Links**: Link to related documentation
- **Current**: Keep information up-to-date

### Language
- **English**: Default language for technical documentation
- **German**: Add `_DE` suffix for German versions
- Both languages can coexist for the same topic

## Workflow for Adding Documentation

### Step 1: Determine Category
Ask yourself:
- Is it about project planning? → `project/`
- Is it a completion report? → `completion/`
- Is it a how-to guide? → `guides/` (+ subcategory)
- Is it about a feature? → `features/`
- Is it for developers? → `development/`
- Is it legal/security? → `legal/`
- Is it historical? → `archive/`
- Is it a change summary? → `changelog/`

### Step 2: Choose File Name
1. Find the next available number in the category
2. Use UPPERCASE and underscores
3. Add language suffix if needed
4. Example: `03-NEW_FEATURE_GUIDE_DE.md`

### Step 3: Create Document
1. Use the template structure above
2. Write clear, focused content
3. Add relevant links
4. Include last updated date

### Step 4: Update Index
Add entry to `docs/README.md` in the appropriate section

### Step 5: Update Cross-References
If moving or renaming files, update all references:
- Check `README.md` in root
- Check `docs/README.md`
- Check other documentation files
- Check source code comments

## Maintenance

### Regular Reviews
Documentation should be reviewed:
- When features change
- When processes change
- Quarterly for accuracy
- When user feedback indicates confusion

### Archiving Process
When documentation becomes outdated:
1. Move to `archive/` directory
2. Add appropriate numbered prefix
3. Update `docs/README.md`
4. Add note in document indicating it's archived
5. Keep for historical reference

### Removing Documentation
**RARELY DONE**. Only remove documentation if:
- It's completely irrelevant
- It contains sensitive information
- It's causing active confusion and has no historical value

In most cases, archive instead of delete.

## Automation Reminders

### For AI Agents (Copilot, Jules, etc.)
When creating or modifying documentation:

1. **Check Location**:
   - Is it in the correct category?
   - Is it in `docs/` (never root except README.md and Agents.md)?

2. **Check Naming**:
   - Does it follow numbered prefix pattern?
   - Is it UPPERCASE with underscores?
   - Does it have language suffix if needed?

3. **Update Index**:
   - Did you update `docs/README.md`?
   - Did you update any affected cross-references?

4. **Content Quality**:
   - Does it have a clear purpose?
   - Is it well-structured?
   - Does it link to related docs?
   - Is the last updated date current?

## Examples of Correct Placement

### ✅ Correct
```
docs/guides/testing/01-TESTING_QUICKSTART_DE.md
docs/features/04-NEW_AUDIO_FEATURE.md
docs/development/03-DEPLOYMENT_GUIDE.md
docs/archive/03-OLD_ARCHITECTURE_PLAN.md
docs/changelog/02-API_CHANGES_SUMMARY.md
```

### ❌ Incorrect
```
TESTING_QUICKSTART_DE.md                    # Should be in docs/guides/testing/
docs/NewFeature.md                          # Missing prefix and wrong case
docs/testing-guide-de.md                    # Wrong case, hyphens instead of underscores
docs/Guide.md                               # Too generic, missing category
```

## Questions?

If you're unsure where to place documentation:
1. Check existing similar documentation
2. Review this guide
3. Ask in PR comments or issues
4. Default to `docs/development/` for developer docs or `docs/guides/` for user docs

---

**Document Status**: Current  
**Last Updated**: 2025-11-06  
**Maintained By**: @MrLongNight, @copilot
