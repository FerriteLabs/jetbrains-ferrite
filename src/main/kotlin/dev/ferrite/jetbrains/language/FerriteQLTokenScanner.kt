package dev.ferrite.jetbrains.language

import com.intellij.psi.tree.IElementType

/**
 * Owns the FerriteQL tokenization decisions extracted from [FerriteQLLexer].
 *
 * Given a buffer and a start offset, [scan] recognizes the token beginning at
 * that offset and reports its end offset and element type. Branch order in
 * [scanToken] is significant and mirrors the original monolithic advance().
 * [isFirstToken] state (used to classify the leading command word) is owned
 * here and reset via [reset] whenever the lexer restarts or crosses a newline.
 */
internal class FerriteQLTokenScanner {

    private val highlighter = FerriteQLSyntaxHighlighter()

    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var isFirstToken: Boolean = true

    data class Token(val end: Int, val type: IElementType)

    fun reset() {
        isFirstToken = true
    }

    fun scan(buffer: CharSequence, bufferEnd: Int, start: Int): Token {
        this.buffer = buffer
        this.bufferEnd = bufferEnd
        this.tokenStart = start
        this.tokenEnd = start
        val type = scanToken(buffer[start])
        return Token(tokenEnd, type)
    }

    private fun scanToken(c: Char): IElementType = when {
        c == '#' -> {
            tokenEnd = findEndOfLine()
            FerriteQLTokenTypes.COMMENT
        }
        c == '"' -> scanString('"')
        c == '\'' -> scanString('\'')
        isInlineWhitespace(c) -> scanWhitespace()
        isNewline(c) -> scanNewline(c)
        isNumberStart(c) -> scanNumber()
        isIdentifierStart(c) -> scanIdentifier()
        isBracket(c) -> scanSingleChar(FerriteQLTokenTypes.IDENTIFIER)
        else -> scanSingleChar(FerriteQLTokenTypes.BAD_CHARACTER)
    }

    private fun scanString(quote: Char): IElementType {
        tokenEnd = findEndOfString(quote)
        return FerriteQLTokenTypes.STRING
    }

    private fun scanWhitespace(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd && isInlineWhitespace(buffer[tokenEnd])) {
            tokenEnd++
        }
        return FerriteQLTokenTypes.WHITESPACE
    }

    private fun scanNewline(c: Char): IElementType {
        tokenEnd = tokenStart + 1
        if (c == '\r' && tokenEnd < bufferEnd && buffer[tokenEnd] == '\n') {
            tokenEnd++
        }
        isFirstToken = true
        return FerriteQLTokenTypes.NEWLINE
    }

    private fun scanNumber(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '.')) {
            tokenEnd++
        }
        return FerriteQLTokenTypes.NUMBER
    }

    private fun scanIdentifier(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) {
            tokenEnd++
        }
        val text = buffer.subSequence(tokenStart, tokenEnd).toString()
        return when {
            isFirstToken && highlighter.isCommand(text) -> {
                isFirstToken = false
                FerriteQLTokenTypes.COMMAND
            }
            highlighter.isOption(text) -> FerriteQLTokenTypes.OPTION
            highlighter.isCommand(text) -> FerriteQLTokenTypes.COMMAND
            else -> FerriteQLTokenTypes.KEY
        }
    }

    private fun scanSingleChar(type: IElementType): IElementType {
        tokenEnd = tokenStart + 1
        return type
    }

    private fun isInlineWhitespace(c: Char): Boolean = c.isWhitespace() && !isNewline(c)

    private fun isNewline(c: Char): Boolean = c == '\n' || c == '\r'

    private fun isNumberStart(c: Char): Boolean = c.isDigit() || isNegativeNumberStart(c)

    private fun isNegativeNumberStart(c: Char): Boolean =
        c == '-' && tokenStart + 1 < bufferEnd && buffer[tokenStart + 1].isDigit()

    private fun isIdentifierStart(c: Char): Boolean = c.isLetter() || c in IDENTIFIER_START_SYMBOLS

    private fun isIdentifierPart(c: Char): Boolean = c.isLetterOrDigit() || c in IDENTIFIER_PART_SYMBOLS

    private fun isBracket(c: Char): Boolean = c in BRACKET_SYMBOLS

    private fun findEndOfLine(): Int {
        var end = tokenStart + 1
        while (end < bufferEnd && buffer[end] != '\n' && buffer[end] != '\r') {
            end++
        }
        return end
    }

    private fun findEndOfString(quote: Char): Int {
        var end = tokenStart + 1
        while (end < bufferEnd) {
            val c = buffer[end]
            if (c == quote) {
                return end + 1
            }
            if (c == '\\' && end + 1 < bufferEnd) {
                end += 2
                continue
            }
            if (c == '\n' || c == '\r') {
                return end
            }
            end++
        }
        return end
    }

    private companion object {
        private const val IDENTIFIER_START_SYMBOLS = "_:."
        private const val IDENTIFIER_PART_SYMBOLS = "_:.-"
        private const val BRACKET_SYMBOLS = "[]{}()"
    }
}
