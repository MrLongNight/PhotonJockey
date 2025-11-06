# PhotonJockey Documentation Guidelines - For Copilot Custom Instructions

## Critical Rule: Root Directory
**ONLY these markdown files are allowed in repository root:**
- `README.md` - Project overview
- `Agents.md` - Agent definitions

**ALL other markdown files MUST be in `docs/` directory.**

## Directory Structure
```
docs/
├── project/          # Project plans, status, refactoring
├── completion/       # TaskGroup completion reports
├── guides/           # How-to guides (testing, troubleshooting, ui)
├── features/         # Feature documentation
├── development/      # Build, coding standards, architecture
├── legal/            # License, security, third-party
├── archive/          # Historical/outdated documentation
└── changelog/        # Change summaries, migrations
```

## Naming Conventions

### File Names
- **Numbered Prefix**: `01-`, `02-`, `03-` for ordering
- **UPPERCASE**: Main title in uppercase
- **Underscores**: Separate words with underscores
- **Language Suffix**: Add `_DE` for German documents

**Examples**: 
- `01-BUILD_INSTRUCTIONS.md`
- `02-TESTING_GUIDE_DE.md`
- `03-AUDIO_PROFILES.md`

### Directory Names
- **Lowercase**: All directory names lowercase
- **Plural**: Use plural forms (guides, features)

## Quick Decision Guide

### Where to place documentation?

| Content Type | Location | Example |
|--------------|----------|---------|
| Project plans/status | `docs/project/` | `01-PROJECT_PLAN.md` |
| Completion reports | `docs/completion/` | `01-TG1.1_COMPLETION.md` |
| How-to guides | `docs/guides/[subcategory]/` | `guides/testing/01-QUICKSTART_DE.md` |
| Feature docs | `docs/features/` | `01-AUDIO_PROFILES.md` |
| Build/coding standards | `docs/development/` | `01-BUILD_INSTRUCTIONS.md` |
| License/security | `docs/legal/` | `01-LICENSE.md` |
| Historical docs | `docs/archive/` | `01-OLD_ANALYSIS.md` |
| Change summaries | `docs/changelog/` | `01-WORKFLOW_CLEANUP.md` |

## Workflow for New Documentation

1. **Choose Category**: Determine which `docs/` subdirectory
2. **Find Next Number**: Check existing files for next prefix number
3. **Create File**: Use format `0X-NAME.md` or `0X-NAME_DE.md`
4. **Add Content**: Include title, purpose, content, last updated date
5. **Update Index**: Add entry to `docs/README.md`
6. **Update Links**: Fix any cross-references if moving existing files

## Document Structure Template

```markdown
# Title

**Date**: YYYY-MM-DD  
**Status**: Current / Archived / Draft

## Purpose
Brief explanation of document purpose.

## Content
[Main content here]

## Related Documentation
- [Link to related doc](path/to/doc.md)

---

*Last Updated: YYYY-MM-DD*
```

## When Moving Documentation

1. Move file to correct `docs/` subdirectory
2. Rename with proper prefix and format
3. Update `docs/README.md`
4. Update root `README.md` if referenced there
5. Update all cross-references in other documentation
6. Update any code comments referencing the file

## Archive vs Delete

- **Archive**: Move to `docs/archive/` when outdated but historically valuable
- **Delete**: Only if completely irrelevant or harmful (rare)
- **Default**: Archive, don't delete

## Common Mistakes to Avoid

❌ Creating markdown files in repository root (except README.md and Agents.md)  
❌ Using hyphens instead of underscores in file names  
❌ Using lowercase for document titles  
❌ Missing numbered prefix  
❌ Forgetting to update docs/README.md  
❌ Not updating cross-references when moving files  

## Verification Checklist

Before committing documentation changes:
- [ ] Is it in the correct `docs/` subdirectory?
- [ ] Does it follow naming convention (0X-NAME.md)?
- [ ] Is `docs/README.md` updated?
- [ ] Are cross-references updated?
- [ ] Does it have proper structure (title, purpose, content, date)?
- [ ] Is language suffix added if German (_DE)?

---

**For full guidelines**: See `docs/DOCUMENTATION_GUIDELINES.md`  
**Last Updated**: 2025-11-06
