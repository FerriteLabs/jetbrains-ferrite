package dev.ferrite.jetbrains.service

/**
 * Pure tokenizer for a single FerriteQL/RESP command line.
 *
 * Splits a raw command string into whitespace-separated parts while honoring
 * single- and double-quoted segments. Extracted verbatim from
 * [FerriteConnectionManager] so command-line parsing has a single owner and can
 * be unit-tested in isolation. Behavior is intentionally identical to the
 * original private implementation, including its handling of empty quoted
 * segments and unterminated quotes.
 */
internal object FerriteCommandLine {

    fun parse(command: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inQuote = false
        var quoteChar = ' '

        for (char in command) {
            when {
                !inQuote && (char == '"' || char == '\'') -> {
                    inQuote = true
                    quoteChar = char
                }
                inQuote && char == quoteChar -> {
                    inQuote = false
                }
                !inQuote && char.isWhitespace() -> {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current = StringBuilder()
                    }
                }
                else -> {
                    current.append(char)
                }
            }
        }

        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }

        return parts
    }
}
