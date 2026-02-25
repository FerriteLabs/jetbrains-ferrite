package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.language.FerriteQLLexer
import dev.ferrite.jetbrains.language.FerriteQLSyntaxHighlighter
import dev.ferrite.jetbrains.language.FerriteQLTokenTypes
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Integration tests for command completion.
 *
 * Verifies that the lexer correctly tokenises commands that the completion
 * provider would suggest, ensuring end-to-end consistency between the
 * tokenisation layer and the completion data structures.
 */
class CommandCompletionIntegrationTest {

    private lateinit var lexer: FerriteQLLexer
    private lateinit var highlighter: FerriteQLSyntaxHighlighter

    @Before
    fun setUp() {
        lexer = FerriteQLLexer()
        highlighter = FerriteQLSyntaxHighlighter()
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private fun firstTokenType(input: String): com.intellij.psi.tree.IElementType? {
        lexer.start(input, 0, input.length, 0)
        return lexer.tokenType
    }

    // -----------------------------------------------------------------------
    // Every completable command must tokenise as COMMAND
    // -----------------------------------------------------------------------

    @Test
    fun `all string commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("GET", "SET", "SETNX", "SETEX", "MGET", "MSET", "INCR", "DECR", "INCRBY", "APPEND", "STRLEN")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `all hash commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("HSET", "HGET", "HMSET", "HMGET", "HGETALL", "HDEL", "HEXISTS", "HKEYS", "HVALS", "HLEN")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `all sorted set commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("ZADD", "ZREM", "ZRANGE", "ZRANGEBYSCORE", "ZSCORE", "ZRANK", "ZCARD")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `all vector commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("VECTOR.CREATE", "VECTOR.ADD", "VECTOR.SEARCH", "VECTOR.GET", "VECTOR.DEL")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `all semantic commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("SEMANTIC.SET", "SEMANTIC.SEARCH", "SEMANTIC.GET")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `transaction commands tokenise as COMMAND and are completable`() {
        val cmds = listOf("MULTI", "EXEC", "DISCARD", "WATCH")
        for (cmd in cmds) {
            assertEquals("$cmd should tokenise as COMMAND", FerriteQLTokenTypes.COMMAND, firstTokenType(cmd))
            assertTrue("$cmd should be recognised by highlighter", highlighter.isCommand(cmd))
        }
    }
}
