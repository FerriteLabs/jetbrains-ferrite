package dev.ferrite.jetbrains

import com.intellij.psi.tree.IElementType
import dev.ferrite.jetbrains.language.FerriteQLLexer
import dev.ferrite.jetbrains.language.FerriteQLTokenTypes
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Characterization tests for the per-token scan decisions of [FerriteQLLexer].
 *
 * Pins whitespace runs, negative/decimal numbers, CRLF newlines, option-vs-command
 * classification, brackets and bad characters ahead of (and after) splitting
 * advance() into dedicated scan helpers (SRP-002).
 */
class FerriteQLLexerScanTest {

    private lateinit var lexer: FerriteQLLexer

    @Before
    fun setUp() {
        lexer = FerriteQLLexer()
    }

    private data class Token(val type: IElementType?, val text: String)

    private fun tokenize(input: String): List<Token> {
        lexer.start(input, 0, input.length, 0)
        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            tokens.add(Token(lexer.tokenType, input.substring(lexer.tokenStart, lexer.tokenEnd)))
            lexer.advance()
        }
        return tokens
    }

    private fun tokenTypes(input: String): List<IElementType?> = tokenize(input).map { it.type }

    private fun tokenTexts(input: String): List<String> = tokenize(input).map { it.text }

    @Test
    fun `type sequence for a simple command line`() {
        assertEquals(
            listOf(
                FerriteQLTokenTypes.COMMAND,
                FerriteQLTokenTypes.WHITESPACE,
                FerriteQLTokenTypes.KEY,
            ),
            tokenTypes("GET mykey"),
        )
    }

    @Test
    fun `text sequence preserves original substrings`() {
        assertEquals(listOf("GET", " ", "mykey"), tokenTexts("GET mykey"))
    }

    @Test
    fun `a run of spaces is a single whitespace token`() {
        val tokens = tokenize("GET   k")
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[1].type)
        assertEquals("   ", tokens[1].text)
    }

    @Test
    fun `negative number is a single NUMBER token`() {
        val tokens = tokenize("-42")
        assertEquals(listOf(FerriteQLTokenTypes.NUMBER), tokens.map { it.type })
        assertEquals("-42", tokens[0].text)
    }

    @Test
    fun `decimal number keeps the dot`() {
        val tokens = tokenize("3.14")
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
        assertEquals("3.14", tokens[0].text)
    }

    @Test
    fun `carriage-return newline is one NEWLINE token`() {
        val tokens = tokenize("A\r\nB")
        assertEquals(FerriteQLTokenTypes.NEWLINE, tokens[1].type)
        assertEquals("\r\n", tokens[1].text)
    }

    @Test
    fun `an option after a command is recognized as OPTION`() {
        val types = tokenTypes("SET k v EX")
        assertEquals(FerriteQLTokenTypes.OPTION, types.last())
    }

    @Test
    fun `bracket characters are IDENTIFIER tokens`() {
        assertEquals(listOf(FerriteQLTokenTypes.IDENTIFIER), tokenTypes("["))
        assertEquals(listOf(FerriteQLTokenTypes.IDENTIFIER), tokenTypes("}"))
    }

    @Test
    fun `an unrecognized symbol is a BAD_CHARACTER token`() {
        assertEquals(listOf(FerriteQLTokenTypes.BAD_CHARACTER), tokenTypes("@"))
    }

    @Test
    fun `a plain word after the command is classified as KEY`() {
        val types = tokenTypes("GET mykey")
        assertEquals(FerriteQLTokenTypes.COMMAND, types.first())
        assertEquals(FerriteQLTokenTypes.KEY, types.last())
    }

    @Test
    fun `a dual command-option word after the command slot resolves to OPTION`() {
        // GET is registered as both command and option; once the leading command
        // slot is consumed, the option branch wins over the command branch.
        val types = tokenTypes("SET GET")
        assertEquals(FerriteQLTokenTypes.COMMAND, types.first())
        assertEquals(FerriteQLTokenTypes.OPTION, types.last())
    }
}
