# Coding Conventions

This document outlines the coding conventions and standards for the PhotonJockey project.

## Code Style

This project follows the **Google Java Style Guide**. 

- Code formatting is enforced using Checkstyle
- Editor configuration is provided via `.editorconfig`
- Run `./gradlew check` to verify your code follows the style guidelines

## Branch Naming Convention

Branch names must follow the pattern:

```
feature/<TG#>-<shortname>
```

Where:
- `<TG#>` is the Task Group number (e.g., 1, 2, 3)
- `<shortname>` is a short descriptive name

**Examples:**
- `feature/1-setup`
- `feature/2-audio-processing`
- `feature/3-ui-improvements`

## Commit Message Convention

All commit messages must contain the Task number in the format:

```
TG<TaskGroup>.<Task>: <description>
```

**Examples:**
- `TG1.1: Add checkstyle configuration`
- `TG1.2: Update documentation`
- `TG2.1: Implement audio analyzer`

## Pull Request Process

Before submitting a pull request, ensure:

1. Your code builds successfully (`./gradlew build`)
2. All tests pass (`./gradlew test`)
3. Code style checks pass (`./gradlew check`)
4. Documentation is updated if necessary

Use the pull request template provided in `.github/pull_request_template.md` when creating PRs.
