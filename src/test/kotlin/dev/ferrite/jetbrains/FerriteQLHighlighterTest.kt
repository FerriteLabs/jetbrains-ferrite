package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.language.FerriteQLSyntaxHighlighter
import dev.ferrite.jetbrains.language.FerriteQLTokenTypes
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [FerriteQLSyntaxHighlighter].
 *
 * Tests the isCommand/isOption classification and the token-to-TextAttributesKey
 * mapping.  These tests exercise pure logic and do not require an IntelliJ
 * application environment.
 */
class FerriteQLHighlighterTest {

    private lateinit var highlighter: FerriteQLSyntaxHighlighter

    @Before
    fun setUp() {
        highlighter = FerriteQLSyntaxHighlighter()
    }

    // =======================================================================
    // isCommand tests
    // =======================================================================

    @Test
    fun `isCommand recognizes standard string commands`() {
        assertTrue(highlighter.isCommand("GET"))
        assertTrue(highlighter.isCommand("SET"))
        assertTrue(highlighter.isCommand("SETNX"))
        assertTrue(highlighter.isCommand("MGET"))
        assertTrue(highlighter.isCommand("MSET"))
        assertTrue(highlighter.isCommand("INCR"))
        assertTrue(highlighter.isCommand("DECR"))
        assertTrue(highlighter.isCommand("APPEND"))
        assertTrue(highlighter.isCommand("STRLEN"))
        assertTrue(highlighter.isCommand("GETRANGE"))
        assertTrue(highlighter.isCommand("SETRANGE"))
        assertTrue(highlighter.isCommand("GETDEL"))
        assertTrue(highlighter.isCommand("GETEX"))
    }

    @Test
    fun `isCommand recognizes hash commands`() {
        assertTrue(highlighter.isCommand("HSET"))
        assertTrue(highlighter.isCommand("HGET"))
        assertTrue(highlighter.isCommand("HMSET"))
        assertTrue(highlighter.isCommand("HMGET"))
        assertTrue(highlighter.isCommand("HGETALL"))
        assertTrue(highlighter.isCommand("HDEL"))
        assertTrue(highlighter.isCommand("HEXISTS"))
        assertTrue(highlighter.isCommand("HKEYS"))
        assertTrue(highlighter.isCommand("HVALS"))
        assertTrue(highlighter.isCommand("HLEN"))
        assertTrue(highlighter.isCommand("HSETNX"))
        assertTrue(highlighter.isCommand("HSCAN"))
        assertTrue(highlighter.isCommand("HRANDFIELD"))
    }

    @Test
    fun `isCommand recognizes list commands`() {
        assertTrue(highlighter.isCommand("LPUSH"))
        assertTrue(highlighter.isCommand("RPUSH"))
        assertTrue(highlighter.isCommand("LPOP"))
        assertTrue(highlighter.isCommand("RPOP"))
        assertTrue(highlighter.isCommand("LRANGE"))
        assertTrue(highlighter.isCommand("LLEN"))
        assertTrue(highlighter.isCommand("LINDEX"))
        assertTrue(highlighter.isCommand("LSET"))
        assertTrue(highlighter.isCommand("LINSERT"))
        assertTrue(highlighter.isCommand("LREM"))
        assertTrue(highlighter.isCommand("LTRIM"))
        assertTrue(highlighter.isCommand("BLPOP"))
        assertTrue(highlighter.isCommand("BRPOP"))
        assertTrue(highlighter.isCommand("LMOVE"))
        assertTrue(highlighter.isCommand("LPOS"))
    }

    @Test
    fun `isCommand recognizes set commands`() {
        assertTrue(highlighter.isCommand("SADD"))
        assertTrue(highlighter.isCommand("SREM"))
        assertTrue(highlighter.isCommand("SMEMBERS"))
        assertTrue(highlighter.isCommand("SISMEMBER"))
        assertTrue(highlighter.isCommand("SCARD"))
        assertTrue(highlighter.isCommand("SPOP"))
        assertTrue(highlighter.isCommand("SRANDMEMBER"))
        assertTrue(highlighter.isCommand("SDIFF"))
        assertTrue(highlighter.isCommand("SINTER"))
        assertTrue(highlighter.isCommand("SUNION"))
        assertTrue(highlighter.isCommand("SMISMEMBER"))
    }

    @Test
    fun `isCommand recognizes sorted set commands`() {
        assertTrue(highlighter.isCommand("ZADD"))
        assertTrue(highlighter.isCommand("ZREM"))
        assertTrue(highlighter.isCommand("ZRANGE"))
        assertTrue(highlighter.isCommand("ZRANGEBYSCORE"))
        assertTrue(highlighter.isCommand("ZREVRANGE"))
        assertTrue(highlighter.isCommand("ZRANK"))
        assertTrue(highlighter.isCommand("ZSCORE"))
        assertTrue(highlighter.isCommand("ZCARD"))
        assertTrue(highlighter.isCommand("ZCOUNT"))
        assertTrue(highlighter.isCommand("ZINCRBY"))
        assertTrue(highlighter.isCommand("ZPOPMIN"))
        assertTrue(highlighter.isCommand("ZPOPMAX"))
        assertTrue(highlighter.isCommand("ZRANDMEMBER"))
    }

    @Test
    fun `isCommand recognizes stream commands`() {
        assertTrue(highlighter.isCommand("XADD"))
        assertTrue(highlighter.isCommand("XREAD"))
        assertTrue(highlighter.isCommand("XREADGROUP"))
        assertTrue(highlighter.isCommand("XRANGE"))
        assertTrue(highlighter.isCommand("XREVRANGE"))
        assertTrue(highlighter.isCommand("XLEN"))
        assertTrue(highlighter.isCommand("XINFO"))
        assertTrue(highlighter.isCommand("XTRIM"))
        assertTrue(highlighter.isCommand("XDEL"))
        assertTrue(highlighter.isCommand("XGROUP"))
        assertTrue(highlighter.isCommand("XACK"))
        assertTrue(highlighter.isCommand("XCLAIM"))
        assertTrue(highlighter.isCommand("XPENDING"))
        assertTrue(highlighter.isCommand("XAUTOCLAIM"))
        assertTrue(highlighter.isCommand("XSETID"))
    }

    @Test
    fun `isCommand recognizes key commands`() {
        assertTrue(highlighter.isCommand("DEL"))
        assertTrue(highlighter.isCommand("EXISTS"))
        assertTrue(highlighter.isCommand("EXPIRE"))
        assertTrue(highlighter.isCommand("TTL"))
        assertTrue(highlighter.isCommand("PTTL"))
        assertTrue(highlighter.isCommand("PERSIST"))
        assertTrue(highlighter.isCommand("TYPE"))
        assertTrue(highlighter.isCommand("RENAME"))
        assertTrue(highlighter.isCommand("KEYS"))
        assertTrue(highlighter.isCommand("SCAN"))
        assertTrue(highlighter.isCommand("TOUCH"))
        assertTrue(highlighter.isCommand("UNLINK"))
        assertTrue(highlighter.isCommand("COPY"))
    }

    @Test
    fun `isCommand recognizes server commands`() {
        assertTrue(highlighter.isCommand("PING"))
        assertTrue(highlighter.isCommand("INFO"))
        assertTrue(highlighter.isCommand("DBSIZE"))
        assertTrue(highlighter.isCommand("FLUSHDB"))
        assertTrue(highlighter.isCommand("FLUSHALL"))
        assertTrue(highlighter.isCommand("SELECT"))
        assertTrue(highlighter.isCommand("AUTH"))
        assertTrue(highlighter.isCommand("CONFIG"))
        assertTrue(highlighter.isCommand("CLIENT"))
        assertTrue(highlighter.isCommand("SHUTDOWN"))
        assertTrue(highlighter.isCommand("MONITOR"))
    }

    @Test
    fun `isCommand recognizes pub-sub commands`() {
        assertTrue(highlighter.isCommand("SUBSCRIBE"))
        assertTrue(highlighter.isCommand("UNSUBSCRIBE"))
        assertTrue(highlighter.isCommand("PUBLISH"))
        assertTrue(highlighter.isCommand("PSUBSCRIBE"))
        assertTrue(highlighter.isCommand("PUNSUBSCRIBE"))
    }

    @Test
    fun `isCommand recognizes transaction commands`() {
        assertTrue(highlighter.isCommand("MULTI"))
        assertTrue(highlighter.isCommand("EXEC"))
        assertTrue(highlighter.isCommand("DISCARD"))
        assertTrue(highlighter.isCommand("WATCH"))
        assertTrue(highlighter.isCommand("UNWATCH"))
    }

    @Test
    fun `isCommand recognizes scripting commands`() {
        assertTrue(highlighter.isCommand("EVAL"))
        assertTrue(highlighter.isCommand("EVALSHA"))
        assertTrue(highlighter.isCommand("SCRIPT"))
        assertTrue(highlighter.isCommand("FUNCTION"))
        assertTrue(highlighter.isCommand("FCALL"))
    }

    @Test
    fun `isCommand recognizes cluster commands`() {
        assertTrue(highlighter.isCommand("CLUSTER"))
        assertTrue(highlighter.isCommand("READONLY"))
        assertTrue(highlighter.isCommand("READWRITE"))
        assertTrue(highlighter.isCommand("ASKING"))
    }

    @Test
    fun `isCommand recognizes Ferrite vector commands`() {
        assertTrue(highlighter.isCommand("VECTOR.CREATE"))
        assertTrue(highlighter.isCommand("VECTOR.ADD"))
        assertTrue(highlighter.isCommand("VECTOR.GET"))
        assertTrue(highlighter.isCommand("VECTOR.DEL"))
        assertTrue(highlighter.isCommand("VECTOR.SEARCH"))
        assertTrue(highlighter.isCommand("VECTOR.INFO"))
        assertTrue(highlighter.isCommand("VECTOR.DIM"))
        assertTrue(highlighter.isCommand("VECTOR.COUNT"))
    }

    @Test
    fun `isCommand recognizes Ferrite timeseries commands`() {
        assertTrue(highlighter.isCommand("TS.CREATE"))
        assertTrue(highlighter.isCommand("TS.ADD"))
        assertTrue(highlighter.isCommand("TS.GET"))
        assertTrue(highlighter.isCommand("TS.RANGE"))
        assertTrue(highlighter.isCommand("TS.REVRANGE"))
        assertTrue(highlighter.isCommand("TS.MRANGE"))
        assertTrue(highlighter.isCommand("TS.INFO"))
        assertTrue(highlighter.isCommand("TS.DEL"))
    }

    @Test
    fun `isCommand recognizes Ferrite document commands`() {
        assertTrue(highlighter.isCommand("DOC.SET"))
        assertTrue(highlighter.isCommand("DOC.GET"))
        assertTrue(highlighter.isCommand("DOC.DEL"))
        assertTrue(highlighter.isCommand("DOC.SEARCH"))
        assertTrue(highlighter.isCommand("DOC.INDEX"))
        assertTrue(highlighter.isCommand("DOC.QUERY"))
    }

    @Test
    fun `isCommand recognizes Ferrite graph commands`() {
        assertTrue(highlighter.isCommand("GRAPH.QUERY"))
        assertTrue(highlighter.isCommand("GRAPH.CREATE"))
        assertTrue(highlighter.isCommand("GRAPH.DELETE"))
        assertTrue(highlighter.isCommand("GRAPH.EXPLAIN"))
    }

    @Test
    fun `isCommand recognizes Ferrite search commands`() {
        assertTrue(highlighter.isCommand("FT.CREATE"))
        assertTrue(highlighter.isCommand("FT.SEARCH"))
        assertTrue(highlighter.isCommand("FT.AGGREGATE"))
        assertTrue(highlighter.isCommand("FT.INFO"))
    }

    @Test
    fun `isCommand recognizes Ferrite CRDT commands`() {
        assertTrue(highlighter.isCommand("CRDT.COUNTER.INCR"))
        assertTrue(highlighter.isCommand("CRDT.COUNTER.GET"))
        assertTrue(highlighter.isCommand("CRDT.LWWREG.SET"))
        assertTrue(highlighter.isCommand("CRDT.ORSET.ADD"))
        assertTrue(highlighter.isCommand("CRDT.SYNC"))
        assertTrue(highlighter.isCommand("CRDT.INFO"))
    }

    @Test
    fun `isCommand recognizes Ferrite semantic commands`() {
        assertTrue(highlighter.isCommand("SEMANTIC.SET"))
        assertTrue(highlighter.isCommand("SEMANTIC.GET"))
        assertTrue(highlighter.isCommand("SEMANTIC.SEARCH"))
        assertTrue(highlighter.isCommand("SEMANTIC.DEL"))
        assertTrue(highlighter.isCommand("SEMANTIC.CACHE"))
        assertTrue(highlighter.isCommand("SEMANTIC.INVALIDATE"))
    }

    @Test
    fun `isCommand is case-insensitive`() {
        assertTrue(highlighter.isCommand("get"))
        assertTrue(highlighter.isCommand("Get"))
        assertTrue(highlighter.isCommand("sEt"))
        assertTrue(highlighter.isCommand("hset"))
        assertTrue(highlighter.isCommand("vector.create"))
        assertTrue(highlighter.isCommand("crdt.counter.incr"))
    }

    @Test
    fun `isCommand rejects unknown commands`() {
        assertFalse(highlighter.isCommand("FOOBAR"))
        assertFalse(highlighter.isCommand("NOTACMD"))
        assertFalse(highlighter.isCommand("SETS"))
        assertFalse(highlighter.isCommand("GETTING"))
        assertFalse(highlighter.isCommand(""))
    }

    // =======================================================================
    // isOption tests
    // =======================================================================

    @Test
    fun `isOption recognizes SET options`() {
        assertTrue(highlighter.isOption("EX"))
        assertTrue(highlighter.isOption("PX"))
        assertTrue(highlighter.isOption("EXAT"))
        assertTrue(highlighter.isOption("PXAT"))
        assertTrue(highlighter.isOption("NX"))
        assertTrue(highlighter.isOption("XX"))
        assertTrue(highlighter.isOption("KEEPTTL"))
        assertTrue(highlighter.isOption("GET"))
    }

    @Test
    fun `isOption recognizes sorted set options`() {
        assertTrue(highlighter.isOption("WITHSCORES"))
        assertTrue(highlighter.isOption("LIMIT"))
        assertTrue(highlighter.isOption("CH"))
        assertTrue(highlighter.isOption("GT"))
        assertTrue(highlighter.isOption("LT"))
        assertTrue(highlighter.isOption("REV"))
        assertTrue(highlighter.isOption("BYLEX"))
        assertTrue(highlighter.isOption("BYSCORE"))
    }

    @Test
    fun `isOption recognizes general options`() {
        assertTrue(highlighter.isOption("MATCH"))
        assertTrue(highlighter.isOption("COUNT"))
        assertTrue(highlighter.isOption("ASC"))
        assertTrue(highlighter.isOption("DESC"))
        assertTrue(highlighter.isOption("BY"))
        assertTrue(highlighter.isOption("ALPHA"))
        assertTrue(highlighter.isOption("STORE"))
        assertTrue(highlighter.isOption("AGGREGATE"))
    }

    @Test
    fun `isOption recognizes stream options`() {
        assertTrue(highlighter.isOption("BLOCK"))
        assertTrue(highlighter.isOption("STREAMS"))
        assertTrue(highlighter.isOption("GROUP"))
        assertTrue(highlighter.isOption("CONSUMER"))
        assertTrue(highlighter.isOption("NOACK"))
        assertTrue(highlighter.isOption("ID"))
        assertTrue(highlighter.isOption("IDLE"))
        assertTrue(highlighter.isOption("MAXLEN"))
        assertTrue(highlighter.isOption("MINID"))
        assertTrue(highlighter.isOption("NOMKSTREAM"))
    }

    @Test
    fun `isOption is case-insensitive`() {
        assertTrue(highlighter.isOption("ex"))
        assertTrue(highlighter.isOption("Ex"))
        assertTrue(highlighter.isOption("nX"))
        assertTrue(highlighter.isOption("withscores"))
    }

    @Test
    fun `isOption rejects unknown options`() {
        assertFalse(highlighter.isOption("NOTANOPTION"))
        assertFalse(highlighter.isOption("FOO"))
        assertFalse(highlighter.isOption(""))
    }

    // =======================================================================
    // getTokenHighlights - mapping from token type to text attributes
    // =======================================================================

    @Test
    fun `COMMAND token maps to keyword highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.COMMAND)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.COMMAND, attrs[0])
        // Verify fallback key is KEYWORD
        assertEquals(DefaultLanguageHighlighterColors.KEYWORD, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `STRING token maps to string highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.STRING)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.STRING, attrs[0])
        assertEquals(DefaultLanguageHighlighterColors.STRING, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `NUMBER token maps to number highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.NUMBER)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.NUMBER, attrs[0])
        assertEquals(DefaultLanguageHighlighterColors.NUMBER, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `COMMENT token maps to comment highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.COMMENT)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.COMMENT, attrs[0])
        assertEquals(DefaultLanguageHighlighterColors.LINE_COMMENT, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `KEY token maps to instance field highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.KEY)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.KEY, attrs[0])
        assertEquals(DefaultLanguageHighlighterColors.INSTANCE_FIELD, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `OPTION token maps to metadata highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.OPTION)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.OPTION, attrs[0])
        assertEquals(DefaultLanguageHighlighterColors.METADATA, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `BAD_CHARACTER token maps to bad character highlighting`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.BAD_CHARACTER)
        assertEquals(1, attrs.size)
        assertEquals(FerriteQLSyntaxHighlighter.BAD_CHARACTER, attrs[0])
        assertEquals(HighlighterColors.BAD_CHARACTER, attrs[0].fallbackAttributeKey)
    }

    @Test
    fun `WHITESPACE token returns empty array`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.WHITESPACE)
        assertTrue(attrs.isEmpty())
    }

    @Test
    fun `NEWLINE token returns empty array`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.NEWLINE)
        assertTrue(attrs.isEmpty())
    }

    @Test
    fun `IDENTIFIER token returns empty array`() {
        val attrs = highlighter.getTokenHighlights(FerriteQLTokenTypes.IDENTIFIER)
        assertTrue(attrs.isEmpty())
    }

    // =======================================================================
    // TextAttributesKey identity checks
    // =======================================================================

    @Test
    fun `COMMAND attribute key has correct external name`() {
        assertEquals("FERRITE_COMMAND", FerriteQLSyntaxHighlighter.COMMAND.externalName)
    }

    @Test
    fun `STRING attribute key has correct external name`() {
        assertEquals("FERRITE_STRING", FerriteQLSyntaxHighlighter.STRING.externalName)
    }

    @Test
    fun `NUMBER attribute key has correct external name`() {
        assertEquals("FERRITE_NUMBER", FerriteQLSyntaxHighlighter.NUMBER.externalName)
    }

    @Test
    fun `COMMENT attribute key has correct external name`() {
        assertEquals("FERRITE_COMMENT", FerriteQLSyntaxHighlighter.COMMENT.externalName)
    }

    @Test
    fun `KEY attribute key has correct external name`() {
        assertEquals("FERRITE_KEY", FerriteQLSyntaxHighlighter.KEY.externalName)
    }

    @Test
    fun `OPTION attribute key has correct external name`() {
        assertEquals("FERRITE_OPTION", FerriteQLSyntaxHighlighter.OPTION.externalName)
    }

    @Test
    fun `BAD_CHARACTER attribute key has correct external name`() {
        assertEquals("FERRITE_BAD_CHARACTER", FerriteQLSyntaxHighlighter.BAD_CHARACTER.externalName)
    }

    // =======================================================================
    // getHighlightingLexer
    // =======================================================================

    @Test
    fun `getHighlightingLexer returns a FerriteQLLexer instance`() {
        val lexer = highlighter.highlightingLexer
        assertNotNull(lexer)
        assertTrue(lexer is dev.ferrite.jetbrains.language.FerriteQLLexer)
    }
}
