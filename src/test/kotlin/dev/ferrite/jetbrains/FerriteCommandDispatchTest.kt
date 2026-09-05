package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.service.FerriteCommandDispatch
import io.lettuce.core.protocol.CommandType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Characterization tests for [FerriteCommandDispatch], pinning the
 * Lettuce-vs-raw dispatch decision extracted from FerriteConnectionManager.
 */
class FerriteCommandDispatchTest {

    @Test
    fun `known Redis command resolves to the built-in CommandType`() {
        assertSame(CommandType.GET, FerriteCommandDispatch.resolveKeyword("GET"))
        assertSame(CommandType.SET, FerriteCommandDispatch.resolveKeyword("SET"))
        assertSame(CommandType.PING, FerriteCommandDispatch.resolveKeyword("PING"))
    }

    @Test
    fun `Ferrite-specific command resolves to a raw keyword`() {
        val keyword = FerriteCommandDispatch.resolveKeyword("VECTOR.SEARCH")
        assertFalse(keyword is CommandType)
        assertEquals("VECTOR.SEARCH", keyword.name())
        assertArrayEquals("VECTOR.SEARCH".toByteArray(Charsets.US_ASCII), keyword.bytes)
    }

    @Test
    fun `raw keyword name and bytes are consistent for another Ferrite command`() {
        val keyword = FerriteCommandDispatch.resolveKeyword("TS.RANGE")
        assertEquals("TS.RANGE", keyword.name())
        assertArrayEquals("TS.RANGE".toByteArray(Charsets.US_ASCII), keyword.bytes)
    }

    @Test
    fun `resolution is case-sensitive and lowercase falls through to raw`() {
        // CommandType enum constants are upper-case; a lower-case name is not a match.
        val keyword = FerriteCommandDispatch.resolveKeyword("get")
        assertFalse(keyword is CommandType)
        assertEquals("get", keyword.name())
    }

    @Test
    fun `unknown token resolves to a raw keyword rather than throwing`() {
        val keyword = FerriteCommandDispatch.resolveKeyword("NOT_A_REAL_COMMAND")
        assertTrue(keyword.name() == "NOT_A_REAL_COMMAND")
    }
}
