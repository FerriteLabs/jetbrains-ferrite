# Contributing to Ferrite for JetBrains IDEs

Thank you for your interest in contributing! This repository contains the official JetBrains IDE plugin for Ferrite.

## Getting Started

- Familiarize yourself with the [main Ferrite contributing guide](https://github.com/ferritelabs/ferrite/blob/main/CONTRIBUTING.md) for general project standards
- Read the [IntelliJ Platform Plugin SDK docs](https://plugins.jetbrains.com/docs/intellij/)

## Prerequisites

- **JDK 17+**
- **Gradle** (wrapper included)
- A JetBrains IDE (IntelliJ IDEA recommended)

## Development Setup

```bash
# Build the plugin
./gradlew build

# Run in a development IDE instance
./gradlew runIde

# Package as a distributable zip
./gradlew buildPlugin
```

## How to Contribute

### Reporting Issues

- Use [GitHub Issues](https://github.com/ferritelabs/jetbrains-ferrite/issues)
- Include your IDE name/version, OS, and plugin version

### Submitting Changes

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-change`)
3. Make your changes
4. Run `./gradlew build` to verify
5. Test with `./gradlew runIde`
6. Commit with a clear message and open a Pull Request

## Guidelines

- Follow existing Kotlin conventions and project structure
- Use the IntelliJ Platform APIs for UI and editor integration
- Register extensions in `src/main/resources/META-INF/plugin.xml`
- Support the widest practical range of IDE versions

## Commit Message Format

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <description>

Types: feat, fix, docs, chore, refactor, test
```

## Code of Conduct

Please be respectful, inclusive, and constructive in all interactions. See the [main project Code of Conduct](https://github.com/ferritelabs/ferrite/blob/main/CONTRIBUTING.md#code-of-conduct).

## License

By contributing, you agree that your contributions will be licensed under Apache-2.0.
