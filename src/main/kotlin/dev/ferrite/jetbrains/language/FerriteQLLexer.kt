package dev.ferrite.jetbrains.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * Thin [LexerBase] adapter that owns the IntelliJ lexer contract and token
 * cursor, delegating every tokenization decision to [FerriteQLTokenScanner].
 */
class FerriteQLLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null

    private val scanner = FerriteQLTokenScanner()

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        scanner.reset()
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
        val token = scanner.scan(buffer, bufferEnd, tokenStart)
        tokenEnd = token.end
        tokenType = token.type
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd
}
