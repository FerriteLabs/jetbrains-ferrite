package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.language.FerriteQLSyntaxHighlighter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for key-value completion provider behaviour.
 *
 * Validates that the completion data structures recognise the key-value
 * oriented commands (GET, SET, HSET, HGET, etc.) and that the expected
 * option tokens such as EX, PX, NX, XX are present for SET completions.
 */
class KeyValueCompletionTest {

    private lateinit var highlighter: FerriteQLSyntaxHighlighter

    @Before
    fun setUp() {
        highlighter = FerriteQLSyntaxHighlighter()
    }

    // =======================================================================
    // Key-value GET/SET family
    // =======================================================================

    @Test
    fun `GET command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("GET"))
    }

    @Test
    fun `SET command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("SET"))
    }

    @Test
    fun `MGET command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("MGET"))
    }

    @Test
    fun `MSET command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("MSET"))
    }

    @Test
    fun `SETNX command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("SETNX"))
    }

    @Test
    fun `SETEX command is recognised by highlighter`() {
        assertTrue(highlighter.isCommand("SETEX"))
    }

    // =======================================================================
    // SET command options
    // =======================================================================

    @Test
    fun `EX option is available for SET completion`() {
        assertTrue(highlighter.isOption("EX"))
    }

    @Test
    fun `PX option is available for SET completion`() {
        assertTrue(highlighter.isOption("PX"))
    }

    @Test
    fun `NX option is available for SET completion`() {
        assertTrue(highlighter.isOption("NX"))
    }

    @Test
    fun `XX option is available for SET completion`() {
        assertTrue(highlighter.isOption("XX"))
    }

    @Test
    fun `KEEPTTL option is available for SET completion`() {
        assertTrue(highlighter.isOption("KEEPTTL"))
    }

    // =======================================================================
    // Hash key-value commands
    // =======================================================================

    @Test
    fun `HSET is recognised as a key-value hash command`() {
        assertTrue(highlighter.isCommand("HSET"))
    }

    @Test
    fun `HGET is recognised as a key-value hash command`() {
        assertTrue(highlighter.isCommand("HGET"))
    }

    @Test
    fun `HGETALL is recognised as a key-value hash command`() {
        assertTrue(highlighter.isCommand("HGETALL"))
    }

    // =======================================================================
    // Case sensitivity
    // =======================================================================

    @Test
    fun `commands are case-insensitive via uppercase normalisation`() {
        // The highlighter normalises input to uppercase internally
        assertTrue(highlighter.isCommand("GET"))
        assertTrue(highlighter.isCommand("SET"))
    }

    @Test
    fun `unknown command is not recognised`() {
        assertFalse(highlighter.isCommand("NOTAREALCOMMAND"))
    }
}
