package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.language.FerriteQLLexer
import dev.ferrite.jetbrains.language.FerriteQLTokenTypes
import com.intellij.psi.tree.IElementType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [FerriteQLLexer].
 *
 * Exercises tokenization of FerriteQL input including commands, strings,
 * numbers, comments, whitespace, newlines, options, keys, brackets,
 * and bad characters.
 */
class FerriteQLLexerTest {

    private lateinit var lexer: FerriteQLLexer

    @Before
    fun setUp() {
        lexer = FerriteQLLexer()
    }

    // -----------------------------------------------------------------------
    // Helper: collect every token produced for the given input
    // -----------------------------------------------------------------------

    private data class Token(val type: IElementType?, val text: String, val start: Int, val end: Int)

    private fun tokenize(input: String): List<Token> {
        lexer.start(input, 0, input.length, 0)
        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            tokens.add(
                Token(
                    lexer.tokenType,
                    input.substring(lexer.tokenStart, lexer.tokenEnd),
                    lexer.tokenStart,
                    lexer.tokenEnd
                )
            )
            lexer.advance()
        }
        return tokens
    }

    private fun tokenTypes(input: String): List<IElementType?> = tokenize(input).map { it.type }

    private fun tokenTexts(input: String): List<String> = tokenize(input).map { it.text }

    // -----------------------------------------------------------------------
    // Command tokenization
    // -----------------------------------------------------------------------

    @Test
    fun `SET command is recognized as COMMAND token`() {
        val tokens = tokenize("SET")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals("SET", tokens[0].text)
    }

    @Test
    fun `GET command is recognized as COMMAND token`() {
        val tokens = tokenize("GET")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `HSET command is recognized`() {
        val tokens = tokenize("HSET")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `LPUSH command is recognized`() {
        val tokens = tokenize("LPUSH")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `ZADD command is recognized`() {
        val tokens = tokenize("ZADD")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `XADD stream command is recognized`() {
        val tokens = tokenize("XADD")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `PING server command is recognized`() {
        val tokens = tokenize("PING")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `DEL key command is recognized`() {
        val tokens = tokenize("DEL")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `MULTI transaction command is recognized`() {
        val tokens = tokenize("MULTI")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific VECTOR_CREATE command is recognized`() {
        val tokens = tokenize("VECTOR.CREATE")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals("VECTOR.CREATE", tokens[0].text)
    }

    @Test
    fun `Ferrite-specific TS_ADD command is recognized`() {
        val tokens = tokenize("TS.ADD")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific DOC_SET command is recognized`() {
        val tokens = tokenize("DOC.SET")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific GRAPH_QUERY command is recognized`() {
        val tokens = tokenize("GRAPH.QUERY")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific FT_CREATE command is recognized`() {
        val tokens = tokenize("FT.CREATE")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific CRDT_COUNTER_INCR command is recognized`() {
        val tokens = tokenize("CRDT.COUNTER.INCR")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Ferrite-specific SEMANTIC_SET command is recognized`() {
        val tokens = tokenize("SEMANTIC.SET")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
    }

    @Test
    fun `Command after newline resets isFirstToken and is recognized as COMMAND`() {
        val tokens = tokenize("GET mykey\nSET")
        // GET -> COMMAND, ' ' -> WS, mykey -> KEY, '\n' -> NEWLINE, SET -> COMMAND
        val setToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.COMMAND, setToken.type)
        assertEquals("SET", setToken.text)
    }

    @Test
    fun `Known command word in non-first position is still COMMAND`() {
        // After the first token has been consumed, subsequent known commands
        // fall into the `highlighter.isCommand(text)` branch (second check)
        val tokens = tokenize("SET mykey GET")
        // SET=COMMAND, ' '=WS, mykey=KEY, ' '=WS, GET=COMMAND
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[4].type)
    }

    // -----------------------------------------------------------------------
    // String literals
    // -----------------------------------------------------------------------

    @Test
    fun `Double-quoted string is tokenized as STRING`() {
        val tokens = tokenize("\"hello world\"")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"hello world\"", tokens[0].text)
    }

    @Test
    fun `Single-quoted string is tokenized as STRING`() {
        val tokens = tokenize("'hello world'")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("'hello world'", tokens[0].text)
    }

    @Test
    fun `Double-quoted string with escaped quote`() {
        val tokens = tokenize("\"say \\\"hi\\\"\"")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"say \\\"hi\\\"\"", tokens[0].text)
    }

    @Test
    fun `Single-quoted string with escaped quote`() {
        val tokens = tokenize("'it\\'s'")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("'it\\'s'", tokens[0].text)
    }

    @Test
    fun `Unterminated double-quoted string is consumed until end of buffer`() {
        val tokens = tokenize("\"unterminated")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"unterminated", tokens[0].text)
    }

    @Test
    fun `Unterminated double-quoted string stops at newline`() {
        val tokens = tokenize("\"unterminated\nSET")
        // string stops at newline boundary
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"unterminated", tokens[0].text)
        // After newline, SET should be recognized
        val setToken = tokens.find { it.text == "SET" }
        assertNotNull(setToken)
        assertEquals(FerriteQLTokenTypes.COMMAND, setToken!!.type)
    }

    @Test
    fun `Empty double-quoted string`() {
        val tokens = tokenize("\"\"")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"\"", tokens[0].text)
    }

    @Test
    fun `Empty single-quoted string`() {
        val tokens = tokenize("''")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("''", tokens[0].text)
    }

    @Test
    fun `String with unicode characters`() {
        val tokens = tokenize("\"hello \u4e16\u754c\"")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
    }

    @Test
    fun `String in command context`() {
        val tokens = tokenize("SET mykey \"my value\"")
        val stringToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.STRING, stringToken.type)
        assertEquals("\"my value\"", stringToken.text)
    }

    // -----------------------------------------------------------------------
    // Numeric literals
    // -----------------------------------------------------------------------

    @Test
    fun `Integer literal is tokenized as NUMBER`() {
        val tokens = tokenize("42")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
        assertEquals("42", tokens[0].text)
    }

    @Test
    fun `Float literal is tokenized as NUMBER`() {
        val tokens = tokenize("3.14")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
        assertEquals("3.14", tokens[0].text)
    }

    @Test
    fun `Negative integer is tokenized as NUMBER`() {
        val tokens = tokenize("-100")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
        assertEquals("-100", tokens[0].text)
    }

    @Test
    fun `Negative float is tokenized as NUMBER`() {
        val tokens = tokenize("-0.5")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
        assertEquals("-0.5", tokens[0].text)
    }

    @Test
    fun `Zero is tokenized as NUMBER`() {
        val tokens = tokenize("0")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NUMBER, tokens[0].type)
    }

    @Test
    fun `Number in command context`() {
        val tokens = tokenize("EXPIRE mykey 3600")
        val numToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.NUMBER, numToken.type)
        assertEquals("3600", numToken.text)
    }

    // -----------------------------------------------------------------------
    // Comments
    // -----------------------------------------------------------------------

    @Test
    fun `Hash comment is tokenized as COMMENT`() {
        val tokens = tokenize("# this is a comment")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMENT, tokens[0].type)
        assertEquals("# this is a comment", tokens[0].text)
    }

    @Test
    fun `Comment stops at newline`() {
        val tokens = tokenize("# comment\nGET")
        assertEquals(FerriteQLTokenTypes.COMMENT, tokens[0].type)
        assertEquals("# comment", tokens[0].text)
        // After newline, GET should be a COMMAND
        val getToken = tokens.find { it.text == "GET" }
        assertNotNull(getToken)
        assertEquals(FerriteQLTokenTypes.COMMAND, getToken!!.type)
    }

    @Test
    fun `Empty comment (just hash)`() {
        val tokens = tokenize("#")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMENT, tokens[0].type)
        assertEquals("#", tokens[0].text)
    }

    @Test
    fun `Comment with special characters`() {
        val tokens = tokenize("# @!$%^&*() comment with symbols")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMENT, tokens[0].type)
    }

    // -----------------------------------------------------------------------
    // Whitespace handling
    // -----------------------------------------------------------------------

    @Test
    fun `Spaces are tokenized as WHITESPACE`() {
        val tokens = tokenize("   ")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[0].type)
        assertEquals("   ", tokens[0].text)
    }

    @Test
    fun `Tab characters are tokenized as WHITESPACE`() {
        val tokens = tokenize("\t\t")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[0].type)
    }

    @Test
    fun `Mixed spaces and tabs are one WHITESPACE token`() {
        val tokens = tokenize(" \t ")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[0].type)
    }

    @Test
    fun `Newline (LF) is tokenized as NEWLINE`() {
        val tokens = tokenize("\n")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NEWLINE, tokens[0].type)
    }

    @Test
    fun `Newline (CRLF) is tokenized as single NEWLINE token`() {
        val tokens = tokenize("\r\n")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NEWLINE, tokens[0].type)
        assertEquals("\r\n", tokens[0].text)
    }

    @Test
    fun `Carriage return alone is tokenized as NEWLINE`() {
        val tokens = tokenize("\r")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.NEWLINE, tokens[0].type)
    }

    @Test
    fun `Whitespace between command and arguments`() {
        val tokens = tokenize("SET  mykey")
        // SET -> COMMAND, '  ' -> WHITESPACE, mykey -> KEY
        assertEquals(3, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[1].type)
        assertEquals("  ", tokens[1].text)
        assertEquals(FerriteQLTokenTypes.KEY, tokens[2].type)
    }

    // -----------------------------------------------------------------------
    // Options
    // -----------------------------------------------------------------------

    @Test
    fun `Known option EX is tokenized as OPTION`() {
        val tokens = tokenize("SET mykey myvalue EX")
        val exToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.OPTION, exToken.type)
        assertEquals("EX", exToken.text)
    }

    @Test
    fun `Known option NX is tokenized as OPTION`() {
        val tokens = tokenize("SET mykey myvalue NX")
        val nxToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.OPTION, nxToken.type)
    }

    @Test
    fun `Known option WITHSCORES is tokenized as OPTION`() {
        val tokens = tokenize("ZRANGE myzset 0 -1 WITHSCORES")
        val wsToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.OPTION, wsToken.type)
    }

    // -----------------------------------------------------------------------
    // Keys / identifiers
    // -----------------------------------------------------------------------

    @Test
    fun `Unknown identifier after command is tokenized as KEY`() {
        val tokens = tokenize("GET mykey")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("mykey", keyToken.text)
    }

    @Test
    fun `Key with colons (namespaced) is a single token`() {
        val tokens = tokenize("GET user:123:profile")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("user:123:profile", keyToken.text)
    }

    @Test
    fun `Key with dots is a single token`() {
        val tokens = tokenize("GET config.server.port")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("config.server.port", keyToken.text)
    }

    @Test
    fun `Key with underscores`() {
        val tokens = tokenize("GET my_key_name")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("my_key_name", keyToken.text)
    }

    @Test
    fun `Key with hyphens`() {
        val tokens = tokenize("GET my-key-name")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("my-key-name", keyToken.text)
    }

    @Test
    fun `Key starting with underscore`() {
        val tokens = tokenize("GET _internal")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("_internal", keyToken.text)
    }

    @Test
    fun `Key starting with colon`() {
        val tokens = tokenize("GET :special")
        // colon starts an identifier token
        val keyToken = tokens.last()
        assertEquals("The colon-prefixed token should be KEY or COMMAND", true,
            keyToken.type == FerriteQLTokenTypes.KEY || keyToken.type == FerriteQLTokenTypes.COMMAND)
        assertEquals(":special", keyToken.text)
    }

    // -----------------------------------------------------------------------
    // Bracket/brace/paren characters
    // -----------------------------------------------------------------------

    @Test
    fun `Square brackets are tokenized as IDENTIFIER`() {
        val tokens = tokenize("[]")
        assertEquals(2, tokens.size)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[0].type)
        assertEquals("[", tokens[0].text)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[1].type)
        assertEquals("]", tokens[1].text)
    }

    @Test
    fun `Curly braces are tokenized as IDENTIFIER`() {
        val tokens = tokenize("{}")
        assertEquals(2, tokens.size)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[0].type)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[1].type)
    }

    @Test
    fun `Parentheses are tokenized as IDENTIFIER`() {
        val tokens = tokenize("()")
        assertEquals(2, tokens.size)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[0].type)
        assertEquals(FerriteQLTokenTypes.IDENTIFIER, tokens[1].type)
    }

    // -----------------------------------------------------------------------
    // Bad characters
    // -----------------------------------------------------------------------

    @Test
    fun `Unknown symbol is tokenized as BAD_CHARACTER`() {
        val tokens = tokenize("@")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
    }

    @Test
    fun `Exclamation mark is BAD_CHARACTER`() {
        val tokens = tokenize("!")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
    }

    @Test
    fun `Dollar sign is BAD_CHARACTER`() {
        val tokens = tokenize("$")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
    }

    @Test
    fun `Percent sign is BAD_CHARACTER`() {
        val tokens = tokenize("%")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
    }

    // -----------------------------------------------------------------------
    // Full command-line tokenization
    // -----------------------------------------------------------------------

    @Test
    fun `SET key value produces correct token sequence`() {
        val tokens = tokenize("SET mykey myvalue")
        assertEquals(5, tokens.size)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)    // SET
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[1].type) // ' '
        assertEquals(FerriteQLTokenTypes.KEY, tokens[2].type)        // mykey
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[3].type) // ' '
        assertEquals(FerriteQLTokenTypes.KEY, tokens[4].type)        // myvalue
    }

    @Test
    fun `SET key value EX seconds NX`() {
        val tokens = tokenize("SET mykey myvalue EX 60 NX")
        val types = tokens.map { it.type }
        assertEquals(FerriteQLTokenTypes.COMMAND, types[0])     // SET
        assertEquals(FerriteQLTokenTypes.WHITESPACE, types[1])
        assertEquals(FerriteQLTokenTypes.KEY, types[2])         // mykey
        assertEquals(FerriteQLTokenTypes.WHITESPACE, types[3])
        assertEquals(FerriteQLTokenTypes.KEY, types[4])         // myvalue
        assertEquals(FerriteQLTokenTypes.WHITESPACE, types[5])
        assertEquals(FerriteQLTokenTypes.OPTION, types[6])      // EX
        assertEquals(FerriteQLTokenTypes.WHITESPACE, types[7])
        assertEquals(FerriteQLTokenTypes.NUMBER, types[8])      // 60
        assertEquals(FerriteQLTokenTypes.WHITESPACE, types[9])
        assertEquals(FerriteQLTokenTypes.OPTION, types[10])     // NX
    }

    @Test
    fun `HSET with quoted value`() {
        val tokens = tokenize("HSET user:1 name \"Alice\"")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)  // HSET
        assertEquals(FerriteQLTokenTypes.KEY, tokens[2].type)      // user:1
        assertEquals(FerriteQLTokenTypes.KEY, tokens[4].type)      // name
        assertEquals(FerriteQLTokenTypes.STRING, tokens[6].type)   // "Alice"
    }

    @Test
    fun `Multiple lines each starting with a command`() {
        val input = "SET a b\nGET a\nDEL a"
        val tokens = tokenize(input)
        val commands = tokens.filter { it.type == FerriteQLTokenTypes.COMMAND }
        assertEquals(3, commands.size)
        assertEquals("SET", commands[0].text)
        assertEquals("GET", commands[1].text)
        assertEquals("DEL", commands[2].text)
    }

    // -----------------------------------------------------------------------
    // Edge cases
    // -----------------------------------------------------------------------

    @Test
    fun `Empty input produces no tokens`() {
        val tokens = tokenize("")
        assertTrue(tokens.isEmpty())
    }

    @Test
    fun `Whitespace-only input`() {
        val tokens = tokenize("   \t  ")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.WHITESPACE, tokens[0].type)
    }

    @Test
    fun `Very long key name`() {
        val longKey = "a".repeat(10_000)
        val tokens = tokenize("GET $longKey")
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals(longKey, keyToken.text)
    }

    @Test
    fun `Very long string literal`() {
        val longVal = "x".repeat(10_000)
        val input = "\"$longVal\""
        val tokens = tokenize(input)
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
    }

    @Test
    fun `Token offsets are contiguous and cover entire input`() {
        val input = "SET mykey \"hello\" EX 60"
        val tokens = tokenize(input)
        assertEquals(0, tokens.first().start)
        assertEquals(input.length, tokens.last().end)
        for (i in 1 until tokens.size) {
            assertEquals(
                "Token ${i - 1} end should equal token $i start",
                tokens[i - 1].end, tokens[i].start
            )
        }
    }

    @Test
    fun `Start with partial buffer range`() {
        // Lexer supports startOffset != 0
        val full = "XXX SET mykey"
        lexer.start(full, 4, full.length, 0)
        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            tokens.add(
                Token(lexer.tokenType, full.substring(lexer.tokenStart, lexer.tokenEnd), lexer.tokenStart, lexer.tokenEnd)
            )
            lexer.advance()
        }
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals("SET", tokens[0].text)
    }

    @Test
    fun `getState always returns 0`() {
        lexer.start("SET key", 0, 7, 0)
        assertEquals(0, lexer.state)
        lexer.advance()
        assertEquals(0, lexer.state)
    }

    @Test
    fun `getBufferSequence returns the original buffer`() {
        val input = "PING"
        lexer.start(input, 0, input.length, 0)
        assertSame(input, lexer.bufferSequence)
    }

    @Test
    fun `getBufferEnd returns the end offset`() {
        val input = "PING"
        lexer.start(input, 0, input.length, 0)
        assertEquals(input.length, lexer.bufferEnd)
    }

    @Test
    fun `Minus sign alone (not followed by digit) is BAD_CHARACTER`() {
        val tokens = tokenize("-")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
    }

    @Test
    fun `Minus followed by non-digit letter is BAD_CHARACTER`() {
        val tokens = tokenize("-abc")
        // '-' cannot start a number (next char is 'a'), so it falls to BAD_CHARACTER
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
        assertEquals("-", tokens[0].text)
        // 'abc' is then an identifier
        assertEquals(FerriteQLTokenTypes.KEY, tokens[1].type)
    }

    @Test
    fun `String with escaped backslash before closing quote`() {
        // Input: "test\\" -> the backslash is escaped, so the second quote closes the string
        val tokens = tokenize("\"test\\\\\"")
        assertEquals(1, tokens.size)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"test\\\\\"", tokens[0].text)
    }

    @Test
    fun `PUBLISH command with channel and message`() {
        val tokens = tokenize("PUBLISH mychannel \"hello subscribers\"")
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[0].type)
        assertEquals("PUBLISH", tokens[0].text)
        assertEquals(FerriteQLTokenTypes.KEY, tokens[2].type)
        assertEquals("mychannel", tokens[2].text)
        assertEquals(FerriteQLTokenTypes.STRING, tokens[4].type)
    }

    @Test
    fun `Comment followed by command on next line`() {
        val input = "# This is a comment\nGET mykey"
        val tokens = tokenize(input)
        assertEquals(FerriteQLTokenTypes.COMMENT, tokens[0].type)
        assertEquals("# This is a comment", tokens[0].text)
        assertEquals(FerriteQLTokenTypes.NEWLINE, tokens[1].type)
        assertEquals(FerriteQLTokenTypes.COMMAND, tokens[2].type)
        assertEquals("GET", tokens[2].text)
    }
}
