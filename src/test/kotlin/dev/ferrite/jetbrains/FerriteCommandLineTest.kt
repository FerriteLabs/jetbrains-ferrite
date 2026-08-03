package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.service.FerriteCommandLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Characterization tests for [FerriteCommandLine], pinning the exact
 * command-line tokenization behavior extracted from FerriteConnectionManager.
 * Pure Kotlin, no IntelliJ platform dependencies.
 */
class FerriteCommandLineTest {

    @Test
    fun `splits a simple command on whitespace`() {
        assertEquals(listOf("SET", "key", "value"), FerriteCommandLine.parse("SET key value"))
    }

    @Test
    fun `collapses runs of whitespace between parts`() {
        assertEquals(listOf("GET", "key"), FerriteCommandLine.parse("GET    key"))
    }

    @Test
    fun `trims leading and trailing whitespace`() {
        assertEquals(listOf("PING"), FerriteCommandLine.parse("   PING   "))
    }

    @Test
    fun `keeps double-quoted segment with spaces as one part`() {
        assertEquals(listOf("SET", "key", "hello world"), FerriteCommandLine.parse("""SET key "hello world""""))
    }

    @Test
    fun `keeps single-quoted segment with spaces as one part`() {
        assertEquals(listOf("SET", "key", "a b c"), FerriteCommandLine.parse("SET key 'a b c'"))
    }

    @Test
    fun `treats the other quote character literally inside a quoted segment`() {
        assertEquals(listOf("SET", "k", "he said \"hi\""), FerriteCommandLine.parse("""SET k 'he said "hi"'"""))
    }

    @Test
    fun `empty input yields no parts`() {
        assertTrue(FerriteCommandLine.parse("").isEmpty())
    }

    @Test
    fun `whitespace-only input yields no parts`() {
        assertTrue(FerriteCommandLine.parse("   \t  ").isEmpty())
    }

    @Test
    fun `empty quoted segment contributes no part`() {
        // Quotes toggle state but append nothing; an empty buffer is never flushed.
        assertEquals(listOf("SET", "k"), FerriteCommandLine.parse("""SET k """"))
    }

    @Test
    fun `unterminated quote captures the remainder as one part`() {
        assertEquals(listOf("SET", "k", "abc def"), FerriteCommandLine.parse("SET k \"abc def"))
    }

    @Test
    fun `adjacent quoted and unquoted text merge into a single part`() {
        assertEquals(listOf("pre mid post"), FerriteCommandLine.parse("""pre" mid "post"""))
    }
}
