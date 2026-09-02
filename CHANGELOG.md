# Changelog

All notable changes to Ferrite for JetBrains will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.0] - Unreleased (planned)

### Changed

- Isolated command-line parsing, Ferrite-specific dispatch decisions, lexer token scanning, and tool-window state decisions
- Hardened exception handling while preserving Lettuce and raw Ferrite command behavior
- Aligned annotations on the canonical `VECTOR.DEL` command
- Restored deterministic tests, Detekt, build, and configured plugin-verifier gates
- Use reachable GitHub project and documentation links until the hosted documentation endpoint is deployed and verified
- Expanded the verified JetBrains compatibility range through the latest available 2025.3 platform line
- Updated the Gradle wrapper and IntelliJ Platform Gradle plugin to support the unified 2025.3 distribution
- Attach the packaged plugin ZIP to GitHub releases for persistent manual installation
- Block Marketplace publication until a valid monitored vendor email is configured

## [1.3.1] - 2026-04-22

### Added

- User-configurable TLS handshake timeout
- Ferrite registration in the JetBrains Database tool window

### Fixed

- Added CI build timeouts and corrected stale documentation links

## [1.3.0] - 2026-04-20

### Added

- Dev container configuration for zero-setup contributor onboarding
- CodeQL security analysis workflow

## [1.2.0] - 2026-03-09

### Added

- Cluster node visualization panel in database tool window

### Fixed

- Increased default connection timeout to 15s for large cluster environments

## [1.1.0] - 2026-02-28

### Added

- FerriteQL live templates for SELECT, COUNT, DELETE, top keys, expiring keys queries
- Vector search live templates (create index, similarity search)
- Semantic cache live templates (set, get)
- Time-travel history live template
- FerriteQL SQL-like keyword completions (SELECT, FROM, WHERE, ORDER BY, etc.)
- Additional command completions: SEMANTIC.GET, HISTORY, DIFF, RESTORE.FROM, QUERY

## [1.0.0] - 2025-01-23

### Added

- **FerriteQL Language**: Full syntax highlighting with customizable color schemes
- **FerriteQL Language**: Real-time error detection and syntax validation
- **FerriteQL Language**: Intelligent code completion for commands, options, and data types
- **FerriteQL Language**: Execute commands with `Ctrl+Enter` / `Cmd+Enter`
- **Configuration**: Syntax highlighting and validation for `ferrite.toml` files
- **Database Tools**: Connection manager for multiple Ferrite servers
- **Database Tools**: Key browser with glob pattern filtering
- **Database Tools**: Value inspector for all data types (strings, hashes, lists, sets, sorted sets, streams)
- **Database Tools**: Server info display with statistics and configuration
- **Live Templates (FerriteQL)**: Strings, hashes, lists, sets, sorted sets, streams, vectors, time series
- **Live Templates (Rust)**: Client initialization and connection patterns
- **Live Templates (Python)**: Sync and async client patterns
- **Live Templates (TypeScript)**: Client setup with ioredis
- **Live Templates (Go)**: Client setup with go-redis
- **Live Templates (Java)**: Client setup with Lettuce and Jedis
- **IDE Support**: Compatible with all JetBrains IDEs 2023.3+ (IntelliJ IDEA, PyCharm, WebStorm, PhpStorm, RubyMine, GoLand, Rider, CLion, DataGrip, Android Studio)

[Unreleased]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.4.0...HEAD
[1.4.0]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.3.1...v1.4.0
[1.3.1]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/ferritelabs/jetbrains-ferrite/releases/tag/v1.0.0
