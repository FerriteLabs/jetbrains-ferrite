# Changelog

All notable changes to Ferrite for JetBrains will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[Unreleased]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/ferritelabs/jetbrains-ferrite/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/ferritelabs/jetbrains-ferrite/releases/tag/v1.0.0
