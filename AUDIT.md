# Clean-Code / SRP Audit — jetbrains-ferrite

Branch: `refactor/clean-code-srp` · Baseline: `fe75e10`

## Summary

- The plugin is functionally healthy (`./gradlew test` green at baseline) but `./gradlew detekt` is **red** at baseline with 165 weighted issues; `detekt.yml` sets `build.maxIssues: 0`, so any single issue fails the gate.
- The overwhelming majority of issues (≈135) are pure **formatting** violations (`ArgumentListWrapping`, `Wrapping`, `NoMultipleSpaces`, `NoWildcardImports`, `ImportOrdering`, `PropertyWrapping`, `MultiLineIfElse`, spacing) that are safely auto-correctable and are isolated into a single formatting-only commit that touches **only** already-flagged files.
- The remaining ≈30 issues are **substantive**: two Single-Responsibility hotspots (`FerriteConnectionManager`, `FerriteQLLexer.advance`), an over-branching UI/state mix in `FerriteToolWindow`, generic/swallowed exception handling, dead private members, and excess `return`/complexity counts.
- SRP work is scoped **conservatively and actor-based**: only pure, coherent units are extracted (command-line parsing + dispatch-keyword decision; lexer token-scanning decisions; tool-window pool-status/key-model decisions). Characterization/unit tests are added **before** each extraction to pin behavior.
- **No public Kotlin types, serialization, `plugin.xml` contributions, plugin IDs/actions/settings/command behavior, or Lettuce behavior are changed.** Ferrite raw-dispatch and the exact error strings of `FerriteConnectionManager` are preserved verbatim. Anything requiring a public-API or behavior change is deferred (none required so far).

## Findings

| ID | Location | Issue | Actors (SRP) | Cost | Risk |
|----|----------|-------|--------------|------|------|
| STYLE-000 | 17 flagged files (main+test) | ~135 auto-correctable formatting violations (`ArgumentListWrapping`, `Wrapping`, `NoMultipleSpaces`, `NoWildcardImports`, `ImportOrdering`, `PropertyWrapping`, `MultiLineIfElse`, `Spacing*`, `NoBlankLineBeforeRbrace`) | n/a (mechanical) | Low | Low — autocorrect only rewrites flagged files; no semantic change |
| SRP-001 | `service/FerriteConnectionManager.kt` | God-class mixing (a) connection lifecycle, (b) RESP command-line parsing, (c) Lettuce-vs-raw dispatch decision, (d) exception→string mapping; `executeCommand` has 6 returns (`ReturnCount`); 5× `TooGenericExceptionCaught`; 2× `SwallowedException` | connection lifecycle owner · command-line parser · dispatch-keyword resolver | Med | Med — must preserve exact error strings and raw Ferrite dispatch |
| SRP-002 | `language/FerriteQLLexer.kt` | `advance()` is a 71-line, cyclomatic-46 god method (`LongMethod`, `CyclomaticComplexMethod`); 2× `ComplexCondition` | token scanner (per-token-kind scan decisions) | Med | Med — lexer output must be byte-for-byte identical |
| SRP-003 | `toolwindow/FerriteToolWindow.kt` | UI construction interleaved with state/decision logic (pool-status text, key-list model building) | UI builder · view-state deriver | Low | Low — pure helpers, no UI-contract change |
| QUAL-002 | `language/FerriteQLAnnotator.kt` | `annotate()` has 6 returns (`ReturnCount`) | annotator (kept single actor) | Low | Low — behavior preserved |
| EXC-001 | `actions/ConnectAction.kt` | Intentional catch-all on `connect()` needs an explicit behavior-preservation rationale | n/a | Low | Low — scoped suppression, same notification |
| QUAL-001 | `settings/FerriteSettingsConfigurable.kt`, `language/FerriteQLCompletionContributor.kt` | Dead private members: `GRID_INSET`, `addLabeledRow`, `buildCommandLookup` (`UnusedPrivateMember/Property`) | n/a | Low | Low — unreferenced code removal |
| QUAL-003 | `test/.../FerriteQLLexerTest.kt`, `test/.../ConnectionSettingsTest.kt` | Unused test helpers (`tokenTypes`/`tokenTexts`) now consumed by new tests; `DestructuringDeclarationWithTooManyEntries` on an intentional 6-component destructuring test | n/a | Low | Low — test-only |

## Ordered sequence

1. `docs`: add this AUDIT.md.
2. **STYLE-000** — `style`: apply detekt `--auto-correct` to flagged files only (formatting-only, isolated commit; no unflagged file touched).
3. **SRP-001** — `test` (characterize command parsing + `executeCommand` not-connected path), then `refactor` (extract `FerriteCommandLine` parser + `FerriteDispatch` keyword decision; fix returns + generic/swallowed exceptions; preserve exact strings & raw dispatch).
4. **SRP-002** — `test` (pin lexer tokenization), then `refactor` (split `advance()` into per-token-kind scan helpers; resolve complexity).
5. **SRP-003** — `refactor` (+test): extract pool-status/key-model decision helpers.
6. **QUAL-002** — `refactor`: reduce annotator return count.
7. **EXC-001** — `refactor`: document and scoped-suppress the intentional catch-all in `ConnectAction`.
8. **QUAL-001** — `refactor`: remove dead private members.
9. **QUAL-003** — `test`: consume lexer helpers, suppress the intentional destructuring test.
10. `build`: final `./gradlew build`.

`./gradlew test` and `./gradlew detekt` are run after every commit; detekt weighted-issue count decreases monotonically to **0** by the final substantive commit, at which point the gate is green.

## Out of scope (deferred / intentionally kept)

- **Dependency and IntelliJ platform version bumps** — explicitly forbidden; untouched.
- **Public surface**: public Kotlin types, `data class ConnectionConfig` shape/serialization, `plugin.xml` contributions, plugin IDs, action/settings/command behavior, and Lettuce dispatch semantics — all unchanged. Any refactor that would require altering them is **deferred**, not performed.
- **`FerriteQLCompletionProvider.COMMANDS`/`OPTIONS` catalog** — this large map/list is a single **data-table actor** (one reason to change: the command catalog evolves). Splitting it would be over-extraction with no cohesion benefit, so it is **kept as-is and documented here**. Only the genuinely dead `buildCommandLookup` helper is removed.
- **`FerriteQLAnnotator.commandArgRequirements`** — same rationale: a single command-metadata data table; kept.
- **`RespCodec`, live templates, run configuration, database-support contributions** — no SRP or correctness findings; untouched.
