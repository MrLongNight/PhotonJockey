# PhotonJockey - Copilot Custom Instructions

## Project Context
PhotonJockey ist ein Java-17-basiertes Hue-Entertainment-Projekt zur Synchronisation von Philips Hue-Lichtern mit Musik in Echtzeit. Build-System: Gradle, UI: JavaFX + Swing (wird migriert zu nur JavaFX), Testing: JUnit 5, Code Style: Google Java Style Guide (Checkstyle).

## Core Development Rules

### Code Changes
- Make **minimal, surgical changes** - only modify what's necessary
- NEVER delete/remove working code unless fixing a security vulnerability
- Always validate changes don't break existing behavior
- Run `./gradlew clean build` and `./gradlew test` before finalizing
- Follow Google Java Style Guide (enforced by Checkstyle)
- Add tests for new functionality (JUnit 5)

### Testing Requirements
- Run existing tests: `./gradlew test`
- Add new tests for new features
- Don't remove or modify existing tests unless broken by your changes
- Target: >60% code coverage for new code

### Git Workflow
- Use clear, descriptive commit messages (German or English)
- Reference issue/PR numbers in commits
- Use `report_progress` tool to commit and push changes
- Don't use `git commit` or `git push` directly

## Documentation Rules ⚠️ CRITICAL

### Root Directory Rule
**ONLY these markdown files are allowed in repository root:**
- `README.md` - Project overview
- `Agents.md` - Agent definitions

**ALL other markdown files MUST be in `docs/` directory.**

### Documentation Structure
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

### File Naming Convention
- **Numbered Prefix**: `01-`, `02-`, `03-` for ordering
- **UPPERCASE**: Main title in uppercase
- **Underscores**: Separate words with underscores
- **Language Suffix**: Add `_DE` for German documents

**Examples**: 
- `01-BUILD_INSTRUCTIONS.md`
- `02-TESTING_GUIDE_DE.md`
- `03-AUDIO_PROFILES.md`

### When Creating Documentation

1. **Choose Category**: Determine which `docs/` subdirectory
2. **Find Next Number**: Check existing files for next prefix
3. **Create File**: Use format `0X-NAME.md` or `0X-NAME_DE.md`
4. **Update Index**: Add entry to `docs/README.md`
5. **Update Links**: Fix cross-references if moving files

### Documentation Categories

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

**Full Guidelines**: See `docs/DOCUMENTATION_GUIDELINES.md` in the repository.

## Project Structure

### Important Directories
- `src/main/java/` - Java source code (package: `io.github.mrlongnight.photonjockey`)
- `src/main/resources/` - Resources (FXML, CSS, images, configs)
- `src/test/java/` - Test code
- `docs/` - ALL project documentation
- `.github/workflows/` - CI/CD workflows

### Key Files
- `build.gradle` - Build configuration
- `checkstyle.xml` - Code style rules
- `settings.gradle` - Project settings
- `README.md` - Project overview
- `Agents.md` - Agent workflow definitions

## Build & Test Commands

```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Run without tests
./gradlew build -x test

# Run specific test
./gradlew test --tests "ClassName"

# Check code style
./gradlew checkstyleMain checkstyleTest
```

## Common Tasks

### Adding a New Feature
1. Check `docs/project/02-IMPLEMENTATION_STATUS.md` for task status
2. Create feature branch: `feature/X-description`
3. Implement with tests
4. Update relevant documentation in `docs/`
5. Run `./gradlew clean build test`
6. Use `report_progress` to commit
7. Create PR

### Fixing a Bug
1. Create bugfix branch: `bugfix/issue-number-description`
2. Write failing test first
3. Fix the bug
4. Verify test passes
5. Run full test suite
6. Use `report_progress` to commit
7. Create PR

### Adding Documentation
1. Determine category (see table above)
2. Find next number in that category
3. Create file: `0X-DESCRIPTIVE_NAME.md` or `0X-NAME_DE.md`
4. Follow template in `docs/DOCUMENTATION_GUIDELINES.md`
5. Update `docs/README.md` to add entry
6. Use `report_progress` to commit

### Moving/Renaming Documentation
1. Move to correct `docs/` subdirectory
2. Rename with proper prefix and format
3. Update `docs/README.md`
4. Update root `README.md` if referenced
5. Update all cross-references
6. Use `report_progress` to commit

## Dependencies

### Custom Dependency
The project uses a custom fork of `yetanotherhueapi:2.8.0-lb` that must be installed to Maven local:

```bash
git clone https://github.com/Kakifrucht/yetanotherhueapi.git
cd yetanotherhueapi
mvn install -DskipTests -Dmaven.javadoc.skip=true
```

CI workflows handle this automatically.

## Agent Communication

### Roles
- **@MrLongNight**: Project lead, makes final decisions
- **@copilot**: GitHub integrated agent, can edit repo directly
- **@google-labs-jules**: External dev agent, works in isolated environment

### Communication
Use PR comments with role mentions:
- Technical findings: `## Technischer Befund:`
- Status updates: `## Statusbericht:` or `## Status für Projektleitung:`

### Workflow
1. @MrLongNight creates task
2. Implementation by @copilot or @jules
3. PR created
4. Technical review by @copilot
5. Final decision by @MrLongNight

**Only @MrLongNight merges PRs.**

## Quality Standards

### Code Quality
- Follow Google Java Style Guide
- Use meaningful variable/method names
- Add Javadoc for public APIs
- Handle exceptions properly
- Avoid code duplication

### Test Quality
- Test happy path and edge cases
- Use descriptive test names
- Arrange-Act-Assert pattern
- Mock external dependencies
- Test behavior, not implementation

### Documentation Quality
- Clear and concise language
- Include examples where relevant
- Keep information current
- Link to related documentation
- Add last updated date

## Common Pitfalls to Avoid

❌ Creating markdown files in root directory  
❌ Using hyphens in file names (use underscores)  
❌ Lowercase document titles (use UPPERCASE)  
❌ Forgetting numbered prefix  
❌ Not updating `docs/README.md` when adding docs  
❌ Not running tests before committing  
❌ Removing working code unnecessarily  
❌ Using `git commit` directly (use `report_progress`)  
❌ Not checking Checkstyle violations  

## Helpful Commands

```bash
# Find all markdown files
find . -name "*.md" -type f

# Check git status
git status --short

# View recent commits
git log --oneline -10

# Search for text in files
grep -r "search term" --include="*.java" src/

# Run with debug logging
./gradlew test --debug --tests "TestName"
```

## Resources

- **Documentation Guidelines**: `docs/DOCUMENTATION_GUIDELINES.md`
- **Quick Reference**: `docs/DOCUMENTATION_GUIDELINES_COPILOT.md`
- **Build Instructions**: `docs/development/01-BUILD_INSTRUCTIONS.md`
- **Coding Conventions**: `docs/development/02-CODING_CONVENTIONS.md`
- **Project Status**: `docs/project/02-IMPLEMENTATION_STATUS.md`
- **Agent Workflow**: `Agents.md`

## Verification Checklist

Before finalizing any change:

- [ ] Code follows Google Java Style Guide
- [ ] All tests pass: `./gradlew test`
- [ ] Build succeeds: `./gradlew clean build`
- [ ] No Checkstyle violations
- [ ] Documentation is in correct `docs/` subdirectory
- [ ] Documentation follows naming convention
- [ ] `docs/README.md` updated if adding major docs
- [ ] Cross-references updated if moving files
- [ ] Commit message is clear and descriptive
- [ ] Changes are minimal and focused

---

**Remember**: Documentation goes in `docs/`, not root. Use numbered prefixes, UPPERCASE, and underscores. Always update `docs/README.md`.

**Last Updated**: 2025-11-06
