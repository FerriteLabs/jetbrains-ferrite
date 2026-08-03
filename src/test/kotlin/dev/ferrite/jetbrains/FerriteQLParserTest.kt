package dev.ferrite.jetbrains

import com.intellij.psi.tree.IElementType
import dev.ferrite.jetbrains.language.FerriteQLLexer
import dev.ferrite.jetbrains.language.FerriteQLTokenTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for FerriteQL parsing logic.
 *
 * The actual [FerriteQLParser] is a thin wrapper that feeds lexer tokens into a
 * PsiBuilder, so its logic is trivial. Instead, these tests focus on the
 * semantic parsing concerns that matter to the plugin:
 *
 * 1. The command-line parsing helper used by [FerriteQLAnnotator] and
 *    [FerriteConnectionManager] (tested via a local re-implementation since the
 *    originals are private).
 * 2. The lexer's ability to produce the correct token stream for various
 *    command structures (simple, complex, multi-arg, multi-line).
 * 3. Error-recovery characteristics -- the lexer never panics and always
 *    produces tokens that cover the entire input.
 */
class FerriteQLParserTest {

    private lateinit var lexer: FerriteQLLexer

    // -----------------------------------------------------------------------
    // Re-implementation of parseCommandLine from FerriteQLAnnotator and
    // FerriteConnectionManager -- both use the same algorithm.  Testing the
    // algorithm here as a unit.
    // -----------------------------------------------------------------------

    private fun parseCommandLine(line: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inQuote = false
        var quoteChar = ' '

        for (char in line) {
            when {
                !inQuote && (char == '"' || char == '\'') -> {
                    inQuote = true
                    quoteChar = char
                }
                inQuote && char == quoteChar -> inQuote = false
                !inQuote && char.isWhitespace() -> {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current = StringBuilder()
                    }
                }
                else -> current.append(char)
            }
        }
        if (current.isNotEmpty()) parts.add(current.toString())
        return parts
    }

    // -----------------------------------------------------------------------
    // Helpers for lexer-based stream assertions
    // -----------------------------------------------------------------------

    private data class Token(val type: IElementType?, val text: String)

    private fun tokenize(input: String): List<Token> {
        lexer = FerriteQLLexer()
        lexer.start(input, 0, input.length, 0)
        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            tokens.add(Token(lexer.tokenType, input.substring(lexer.tokenStart, lexer.tokenEnd)))
            lexer.advance()
        }
        return tokens
    }

    @Before
    fun setUp() {
        lexer = FerriteQLLexer()
    }

    // =======================================================================
    // parseCommandLine tests
    // =======================================================================

    @Test
    fun `parse simple SET key value`() {
        val parts = parseCommandLine("SET mykey myvalue")
        assertEquals(listOf("SET", "mykey", "myvalue"), parts)
    }

    @Test
    fun `parse command with double-quoted argument`() {
        val parts = parseCommandLine("SET mykey \"hello world\"")
        assertEquals(listOf("SET", "mykey", "hello world"), parts)
    }

    @Test
    fun `parse command with single-quoted argument`() {
        val parts = parseCommandLine("SET mykey 'hello world'")
        assertEquals(listOf("SET", "mykey", "hello world"), parts)
    }

    @Test
    fun `parse command with multiple quoted arguments`() {
        val parts = parseCommandLine("MSET \"key1\" \"val1\" \"key2\" \"val2\"")
        assertEquals(listOf("MSET", "key1", "val1", "key2", "val2"), parts)
    }

    @Test
    fun `parse with extra whitespace`() {
        val parts = parseCommandLine("  SET   mykey   myvalue  ")
        assertEquals(listOf("SET", "mykey", "myvalue"), parts)
    }

    @Test
    fun `parse with tabs`() {
        val parts = parseCommandLine("SET\tmykey\tmyvalue")
        assertEquals(listOf("SET", "mykey", "myvalue"), parts)
    }

    @Test
    fun `parse empty string`() {
        val parts = parseCommandLine("")
        assertTrue(parts.isEmpty())
    }

    @Test
    fun `parse whitespace only`() {
        val parts = parseCommandLine("   ")
        assertTrue(parts.isEmpty())
    }

    @Test
    fun `parse single command without arguments`() {
        val parts = parseCommandLine("PING")
        assertEquals(listOf("PING"), parts)
    }

    @Test
    fun `parse HSET with multiple field-value pairs`() {
        val parts = parseCommandLine("HSET user:1 name Alice age 30 city \"New York\"")
        assertEquals(listOf("HSET", "user:1", "name", "Alice", "age", "30", "city", "New York"), parts)
    }

    @Test
    fun `parse ZADD with score-member pairs`() {
        val parts = parseCommandLine("ZADD leaderboard 100 player1 200 player2 150 player3")
        assertEquals(
            listOf("ZADD", "leaderboard", "100", "player1", "200", "player2", "150", "player3"),
            parts
        )
    }

    @Test
    fun `parse with quoted string containing special characters`() {
        val parts = parseCommandLine("SET key \"value with 'quotes' and spaces\"")
        assertEquals(listOf("SET", "key", "value with 'quotes' and spaces"), parts)
    }

    @Test
    fun `parse with adjacent quote and word`() {
        // Quote attached to subsequent text: "hello"world -> helloworld
        val parts = parseCommandLine("SET key \"hello\"world")
        assertEquals(listOf("SET", "key", "helloworld"), parts)
    }

    @Test
    fun `parse with unclosed quote consumes rest of line`() {
        val parts = parseCommandLine("SET key \"unclosed")
        assertEquals(listOf("SET", "key", "unclosed"), parts)
    }

    @Test
    fun `parse Ferrite vector command`() {
        val parts = parseCommandLine("VECTOR.SEARCH embeddings [0.1,0.2,0.3] TOP_K 10")
        assertEquals(listOf("VECTOR.SEARCH", "embeddings", "[0.1,0.2,0.3]", "TOP_K", "10"), parts)
    }

    @Test
    fun `parse Ferrite CRDT command`() {
        val parts = parseCommandLine("CRDT.COUNTER.INCR mycounter 5")
        assertEquals(listOf("CRDT.COUNTER.INCR", "mycounter", "5"), parts)
    }

    // =======================================================================
    // Lexer token stream structure tests
    // =======================================================================

    @Test
    fun `simple GET key produces COMMAND WS KEY`() {
        val tokens = tokenize("GET mykey")
        val types = tokens.map { it.type }
        assertEquals(
            listOf(FerriteQLTokenTypes.COMMAND, FerriteQLTokenTypes.WHITESPACE, FerriteQLTokenTypes.KEY),
            types
        )
    }

    @Test
    fun `SET key value string produces COMMAND WS KEY WS STRING`() {
        val tokens = tokenize("SET mykey \"myvalue\"")
        val types = tokens.map { it.type }
        assertEquals(
            listOf(
                FerriteQLTokenTypes.COMMAND,
                FerriteQLTokenTypes.WHITESPACE,
                FerriteQLTokenTypes.KEY,
                FerriteQLTokenTypes.WHITESPACE,
                FerriteQLTokenTypes.STRING
            ),
            types
        )
    }

    @Test
    fun `EXPIRE key seconds produces COMMAND WS KEY WS NUMBER`() {
        val tokens = tokenize("EXPIRE mykey 300")
        val types = tokens.map { it.type }
        assertEquals(
            listOf(
                FerriteQLTokenTypes.COMMAND,
                FerriteQLTokenTypes.WHITESPACE,
                FerriteQLTokenTypes.KEY,
                FerriteQLTokenTypes.WHITESPACE,
                FerriteQLTokenTypes.NUMBER
            ),
            types
        )
    }

    @Test
    fun `Complex ZADD with options`() {
        val tokens = tokenize("ZADD myset NX GT 1.5 member1")
        val types = tokens.filter { it.type != FerriteQLTokenTypes.WHITESPACE }.map { it.type }
        assertEquals(FerriteQLTokenTypes.COMMAND, types[0]) // ZADD
        assertEquals(FerriteQLTokenTypes.KEY, types[1]) // myset
        assertEquals(FerriteQLTokenTypes.OPTION, types[2]) // NX
        assertEquals(FerriteQLTokenTypes.OPTION, types[3]) // GT
        assertEquals(FerriteQLTokenTypes.NUMBER, types[4]) // 1.5
        assertEquals(FerriteQLTokenTypes.KEY, types[5]) // member1
    }

    @Test
    fun `Multi-line script produces correct structure`() {
        val input = """
            SET key1 "value1"
            SET key2 "value2"
            GET key1
        """.trimIndent()
        val tokens = tokenize(input)
        val commands = tokens.filter { it.type == FerriteQLTokenTypes.COMMAND }
        assertEquals(3, commands.size)
        assertEquals("SET", commands[0].text)
        assertEquals("SET", commands[1].text)
        assertEquals("GET", commands[2].text)
    }

    @Test
    fun `Input with comment lines interleaved`() {
        val input = """
            # Set a key
            SET mykey myvalue
            # Get it back
            GET mykey
        """.trimIndent()
        val tokens = tokenize(input)
        val comments = tokens.filter { it.type == FerriteQLTokenTypes.COMMENT }
        val commands = tokens.filter { it.type == FerriteQLTokenTypes.COMMAND }
        assertEquals(2, comments.size)
        assertEquals(2, commands.size)
    }

    @Test
    fun `Tokens cover entire input without gaps`() {
        val input = "HSET user:1 name \"Alice\" age 30"
        val tokens = tokenize(input)
        val reconstructed = tokens.joinToString("") { it.text }
        assertEquals(input, reconstructed)
    }

    @Test
    fun `Error recovery - bad characters do not prevent subsequent parsing`() {
        val input = "@ GET mykey"
        val tokens = tokenize(input)
        assertEquals(FerriteQLTokenTypes.BAD_CHARACTER, tokens[0].type)
        // There should still be further tokens
        val getToken = tokens.find { it.text == "GET" }
        assertNotNull("GET should still be tokenized after bad character", getToken)
        assertEquals(FerriteQLTokenTypes.COMMAND, getToken!!.type)
    }

    @Test
    fun `Error recovery - multiple bad characters between valid tokens`() {
        val input = "SET @!$ mykey"
        val tokens = tokenize(input)
        val commandToken = tokens[0]
        assertEquals(FerriteQLTokenTypes.COMMAND, commandToken.type)
        val keyToken = tokens.last()
        assertEquals(FerriteQLTokenTypes.KEY, keyToken.type)
        assertEquals("mykey", keyToken.text)
    }

    @Test
    fun `Error recovery - unterminated string does not swallow everything`() {
        val input = "\"unclosed\nGET mykey"
        val tokens = tokenize(input)
        // The string should stop at the newline
        assertEquals(FerriteQLTokenTypes.STRING, tokens[0].type)
        assertEquals("\"unclosed", tokens[0].text)
        // GET should follow on the next line
        val getToken = tokens.find { it.text == "GET" }
        assertNotNull(getToken)
        assertEquals(FerriteQLTokenTypes.COMMAND, getToken!!.type)
    }

    @Test
    fun `Lexer never returns null mid-stream (no gaps in token coverage)`() {
        val input = "SET key \"val\" # comment\nGET key2"
        lexer.start(input, 0, input.length, 0)
        var prevEnd = 0
        while (lexer.tokenType != null) {
            assertEquals("Token should start where previous ended", prevEnd, lexer.tokenStart)
            assertTrue("Token should have positive length", lexer.tokenEnd > lexer.tokenStart)
            prevEnd = lexer.tokenEnd
            lexer.advance()
        }
        assertEquals("All tokens should cover full input", input.length, prevEnd)
    }

    // =======================================================================
    // Argument count validation logic (mirrors FerriteQLAnnotator logic)
    // =======================================================================

    private val commandArgRequirements = mapOf(
        "GET" to 1, "SET" to 2, "SETNX" to 2, "SETEX" to 3,
        "HSET" to 3, "HGET" to 2, "HDEL" to 2, "HEXISTS" to 2,
        "LPUSH" to 2, "RPUSH" to 2, "LRANGE" to 3, "LINDEX" to 2,
        "SADD" to 2, "SREM" to 2, "SISMEMBER" to 2,
        "ZADD" to 3, "ZREM" to 2, "ZSCORE" to 2, "ZRANK" to 2,
        "EXPIRE" to 2, "EXPIREAT" to 2, "RENAME" to 2,
        "XADD" to 4, "XRANGE" to 3,
        "PUBLISH" to 2, "SUBSCRIBE" to 1,
        "DEL" to 1, "EXISTS" to 1, "TYPE" to 1, "TTL" to 1, "PTTL" to 1,
        "SELECT" to 1, "APPEND" to 2, "INCR" to 1, "DECR" to 1,
        "INCRBY" to 2, "DECRBY" to 2,
        "KEYS" to 1, "SCAN" to 1,
    )

    private fun validateArgCount(line: String): String? {
        val parts = parseCommandLine(line.trim())
        if (parts.isEmpty()) return null
        val command = parts[0].uppercase()
        val required = commandArgRequirements[command] ?: return null
        val argCount = parts.size - 1
        return if (argCount < required) {
            "$command requires at least $required argument(s), got $argCount"
        } else {
            null
        }
    }

    @Test
    fun `GET without key triggers warning`() {
        val warning = validateArgCount("GET")
        assertNotNull(warning)
        assertTrue(warning!!.contains("requires at least 1"))
    }

    @Test
    fun `GET with key is valid`() {
        assertNull(validateArgCount("GET mykey"))
    }

    @Test
    fun `SET without enough arguments triggers warning`() {
        assertNotNull(validateArgCount("SET mykey"))
        assertNull(validateArgCount("SET mykey myvalue"))
    }

    @Test
    fun `HSET needs 3 arguments`() {
        assertNotNull(validateArgCount("HSET myhash field"))
        assertNull(validateArgCount("HSET myhash field value"))
    }

    @Test
    fun `SETEX needs 3 arguments`() {
        assertNotNull(validateArgCount("SETEX mykey 60"))
        assertNull(validateArgCount("SETEX mykey 60 myvalue"))
    }

    @Test
    fun `XADD needs 4 arguments minimum`() {
        assertNotNull(validateArgCount("XADD stream * field"))
        assertNull(validateArgCount("XADD stream * field value"))
    }

    @Test
    fun `LRANGE needs 3 arguments`() {
        assertNotNull(validateArgCount("LRANGE mylist 0"))
        assertNull(validateArgCount("LRANGE mylist 0 -1"))
    }

    @Test
    fun `Unknown command returns null (no validation)`() {
        assertNull(validateArgCount("UNKNOWNCMD foo bar"))
    }

    @Test
    fun `PING with no arguments returns null (no minimum args defined)`() {
        assertNull(validateArgCount("PING"))
    }

    @Test
    fun `Extra arguments beyond minimum are fine`() {
        assertNull(validateArgCount("SET mykey myvalue EX 60 NX"))
        assertNull(validateArgCount("DEL key1 key2 key3"))
    }

    @Test
    fun `Quoted arguments count correctly`() {
        assertNull(validateArgCount("SET mykey \"hello world\""))
        assertNotNull(validateArgCount("SET \"mykey\""))
    }
}
