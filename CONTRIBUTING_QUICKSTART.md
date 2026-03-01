# Contributing Quickstart — jetbrains-ferrite

Get up and running in 5 minutes.

## Prerequisites

- [JDK 17+](https://adoptium.net/) (required by IntelliJ Platform)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) Community or Ultimate (for testing)
- A running Ferrite or Redis instance (for integration testing)

## Fork & Clone

```bash
gh repo fork ferritelabs/jetbrains-ferrite --clone
cd jetbrains-ferrite
```

## Build & Run

```bash
# Build the plugin
./gradlew build

# Launch a sandboxed IDE with the plugin installed
./gradlew runIde

# Build the distributable zip
./gradlew buildPlugin
```

## Test

```bash
./gradlew test
```

## What to Work On

- Look for [good first issues](https://github.com/ferritelabs/jetbrains-ferrite/labels/good%20first%20issue)
- Improve FerriteQL language support in `src/main/kotlin/.../language/`
- Enhance the connection manager in `src/main/kotlin/.../service/`
- Add live templates for new languages
- Improve the tool window UI in `src/main/kotlin/.../toolwindow/`

## Project Structure

```
src/main/kotlin/dev/ferrite/jetbrains/
├── actions/       # IDE actions (connect, execute, refresh)
├── language/      # FerriteQL parser, lexer, highlighter, completions
├── service/       # Connection management, RESP codec
└── toolwindow/    # Tool window UI, key browser, connection dialog
```

## Submitting Changes

1. Create a feature branch: `git checkout -b my-change`
2. Make your changes
3. Test with `./gradlew runIde` + `./gradlew test`
4. Commit using [Conventional Commits](https://www.conventionalcommits.org/)
5. Push and open a PR

See [CONTRIBUTING.md](CONTRIBUTING.md) for full guidelines.
