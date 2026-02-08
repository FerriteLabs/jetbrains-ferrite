package dev.ferrite.jetbrains.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class FerriteQLLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null
    private var isFirstToken: Boolean = true

    private val highlighter = FerriteQLSyntaxHighlighter()

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.isFirstToken = true
        advance()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd

        if (tokenStart >= bufferEnd) {
            tokenType = null
            return
        }

        val c = buffer[tokenStart]

        when {
            // Comment
            c == '#' -> {
                tokenEnd = findEndOfLine()
                tokenType = FerriteQLTokenTypes.COMMENT
            }

            // String (double-quoted)
            c == '"' -> {
                tokenEnd = findEndOfString('"')
                tokenType = FerriteQLTokenTypes.STRING
            }

            // String (single-quoted)
            c == '\'' -> {
                tokenEnd = findEndOfString('\'')
                tokenType = FerriteQLTokenTypes.STRING
            }

            // Whitespace
            c.isWhitespace() && c != '\n' && c != '\r' -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace() &&
                    buffer[tokenEnd] != '\n' && buffer[tokenEnd] != '\r') {
                    tokenEnd++
                }
                tokenType = FerriteQLTokenTypes.WHITESPACE
            }

            // Newline
            c == '\n' || c == '\r' -> {
                tokenEnd = tokenStart + 1
                if (c == '\r' && tokenEnd < bufferEnd && buffer[tokenEnd] == '\n') {
                    tokenEnd++
                }
                tokenType = FerriteQLTokenTypes.NEWLINE
                isFirstToken = true
            }

            // Number
            c.isDigit() || (c == '-' && tokenStart + 1 < bufferEnd && buffer[tokenStart + 1].isDigit()) -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '.')) {
                    tokenEnd++
                }
                tokenType = FerriteQLTokenTypes.NUMBER
            }

            // Identifier/Command/Option/Key
            c.isLetter() || c == '_' || c == ':' || c == '.' -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd &&
                    (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] == '_' ||
                        buffer[tokenEnd] == ':' || buffer[tokenEnd] == '.' || buffer[tokenEnd] == '-')) {
                    tokenEnd++
                }

                val text = buffer.subSequence(tokenStart, tokenEnd).toString()

                tokenType = when {
                    isFirstToken && highlighter.isCommand(text) -> {
                        isFirstToken = false
                        FerriteQLTokenTypes.COMMAND
                    }
                    highlighter.isOption(text) -> FerriteQLTokenTypes.OPTION
                    highlighter.isCommand(text) -> FerriteQLTokenTypes.COMMAND
                    else -> FerriteQLTokenTypes.KEY
                }
            }

            // Array/Object markers
            c == '[' || c == ']' || c == '{' || c == '}' || c == '(' || c == ')' -> {
                tokenEnd = tokenStart + 1
                tokenType = FerriteQLTokenTypes.IDENTIFIER
            }

            // Bad character
            else -> {
                tokenEnd = tokenStart + 1
                tokenType = FerriteQLTokenTypes.BAD_CHARACTER
            }
        }
    }

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

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd
}
