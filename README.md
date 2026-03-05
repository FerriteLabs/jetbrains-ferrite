# Ferrite for JetBrains IDEs

[![CI](https://github.com/ferritelabs/jetbrains-ferrite/actions/workflows/ci.yml/badge.svg)](https://github.com/ferritelabs/jetbrains-ferrite/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)
[![JetBrains Marketplace](https://img.shields.io/badge/JetBrains-Marketplace-000000)](https://plugins.jetbrains.com/plugin/ferritelabs-ferrite)

Official JetBrains IDE plugin for [Ferrite](https://ferrite.dev) - a high-performance, tiered-storage key-value store.

## Features

### FerriteQL Language Support

- **Syntax Highlighting** - Full highlighting for all Redis and Ferrite commands
- **Code Completion** - Intelligent completion for commands, options, and arguments
- **Command Execution** - Run commands directly from the editor with Ctrl+Enter
- **Error Detection** - Real-time validation of command syntax

### Database Tool Integration

- **Connection Management** - Save and manage multiple Ferrite connections
- **Key Browser** - Browse keys with pattern filtering
- **Value Inspector** - View and edit values for all data types
- **Server Info** - Display server statistics and configuration

### Configuration File Support

- **Syntax Highlighting** - Full highlighting for ferrite.toml files
- **Validation** - Check configuration values against valid ranges
- **Completion** - Autocomplete for configuration keys

### Live Templates

Code snippets for common operations in all supported languages:

- **FerriteQL** - Commands for strings, hashes, lists, sets, sorted sets, streams, vectors, time series
- **Rust** - Client setup, connection pools, async operations
- **Python** - Sync and async clients, decorators, pipelines
- **TypeScript** - Client setup, caching patterns
- **Go** - Client setup, context handling
- **Java** - Client setup, Spring integration

## Installation

### From JetBrains Marketplace

1. Open your JetBrains IDE (IntelliJ IDEA, PyCharm, WebStorm, etc.)
2. Go to **Settings → Plugins → Marketplace**
3. Search for "Ferrite"
4. Click **Install**
5. Restart the IDE

### From Disk

1. Download the plugin ZIP from [releases](https://github.com/ferritelabs/ferrite/releases)
2. Go to **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the downloaded ZIP file
4. Restart the IDE

## Quick Start

### Connect to Ferrite

1. Open the **Ferrite** tool window (View → Tool Windows → Ferrite)
2. Click **Add** to create a new connection
3. Enter connection details:
   - **Name**: Display name (e.g., "Local")
   - **Host**: Server hostname (e.g., "localhost")
   - **Port**: Server port (default: 6379)
   - **Password**: Optional authentication
   - **Database**: Database number (0-15)
   - **Use TLS**: Enable for secure connections
4. Click **OK**, then **Connect**

### Create a FerriteQL File

1. Create a new file with `.fql` or `.ferriteql` extension
2. Start writing commands with syntax highlighting

```fql
# Set some values
SET user:1:name "Alice"
SET user:1:email "alice@example.com" EX 3600

# Get values
GET user:1:name

# Hash operations
HSET user:1 name "Alice" email "alice@example.com" age 30
HGETALL user:1

# Vector search
VECTOR.CREATE embeddings HNSW DIM 1536 DISTANCE COSINE
VECTOR.SEARCH embeddings [0.1, 0.2, ...] TOP_K 10
```

### Execute Commands

- **Ctrl+Enter** (Cmd+Enter on macOS): Execute current line
- **Ctrl+Shift+Enter**: Execute selection

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+Enter` | Execute current line |
| `Ctrl+Shift+Enter` | Execute selection |
| `Ctrl+Space` | Show completions |
| `Alt+Enter` | Show quick fixes |

## Live Templates

Type a prefix and press **Tab** to expand:

### FerriteQL

| Prefix | Expansion |
|--------|-----------|
| `get` | `GET key` |
| `set` | `SET key value` |
| `setex` | `SET key value EX seconds` |
| `hset` | `HSET key field value` |
| `hgetall` | `HGETALL key` |
| `lpush` | `LPUSH list value` |
| `lrange` | `LRANGE list 0 -1` |
| `zadd` | `ZADD zset score member` |
| `zrange` | `ZRANGE zset 0 -1 WITHSCORES` |
| `xadd` | `XADD stream * field value` |
| `vectorcreate` | `VECTOR.CREATE index HNSW DIM 1536 DISTANCE COSINE` |
| `vectorsearch` | `VECTOR.SEARCH index [vector] TOP_K 10` |
| `tsadd` | `TS.ADD key * value` |
| `tsrange` | `TS.RANGE key - + AGGREGATION avg bucket` |
| `semanticset` | `SEMANTIC.SET key "text" TTL seconds` |
| `semanticsearch` | `SEMANTIC.SEARCH "query" TOP_K k THRESHOLD score` |

## Configuration

### Plugin Settings

Open **Settings → Tools → Ferrite** to configure:

- **Default Connection**: Auto-connect on startup
- **Max Keys**: Maximum keys to display in browser
- **Output Format**: JSON, table, or raw

### Color Scheme

Customize highlighting in **Settings → Editor → Color Scheme → FerriteQL**:

- Commands
- Strings
- Numbers
- Comments
- Keys
- Options

## Building from Source

### Prerequisites

- JDK 17+
- IntelliJ IDEA (for development)

### Build

```bash
cd jetbrains-ferrite
./gradlew build
```

### Run in Development

```bash
./gradlew runIde
```

### Package

```bash
./gradlew buildPlugin
# Output: build/distributions/ferrite-*.zip
```

## Supported IDEs

Compatible with all JetBrains IDEs version 2023.3+:

- IntelliJ IDEA (Community & Ultimate)
- PyCharm (Community & Professional)
- WebStorm
- PhpStorm
- RubyMine
- GoLand
- Rider
- CLion
- DataGrip
- Android Studio

## Troubleshooting

### Connection Failed

1. Verify Ferrite server is running
2. Check host and port settings
3. If using TLS, ensure certificate is valid
4. Check firewall rules

### Syntax Highlighting Not Working

1. Verify file extension is `.fql` or `.ferriteql`
2. Check Settings → Editor → File Types
3. Try invalidating caches: File → Invalidate Caches

### Commands Not Executing

1. Ensure you're connected (check tool window status)
2. Verify command syntax
3. Check Output panel for errors

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/ferritelabs/ferrite/blob/main/CONTRIBUTING.md).

## License

Apache 2.0 - See [LICENSE](https://github.com/ferritelabs/ferrite/blob/main/LICENSE) for details.

## 🌐 FerriteLabs Ecosystem

| Repository | Description |
|-----------|-------------|
| [ferrite](https://github.com/ferritelabs/ferrite) | Core database engine (Rust, 12 crates) |
| [ferrite-docs](https://github.com/ferritelabs/ferrite-docs) | Documentation website |
| [ferrite-ops](https://github.com/ferritelabs/ferrite-ops) | Docker, Helm, Grafana, packaging |
| [ferrite-bench](https://github.com/ferritelabs/ferrite-bench) | Performance benchmarks |
| [vscode-ferrite](https://github.com/ferritelabs/vscode-ferrite) | VS Code extension |
| **jetbrains-ferrite** | 📍 You are here |
| [homebrew-tap](https://github.com/ferritelabs/homebrew-tap) | Homebrew formula |

## Resources

- [Ferrite Documentation](https://ferrite.dev/docs)
- [VS Code Extension](https://marketplace.visualstudio.com/items?itemName=ferrite.ferrite-vscode)
- [GitHub Repository](https://github.com/ferritelabs/ferrite)
- [Issue Tracker](https://github.com/ferritelabs/ferrite/issues)
