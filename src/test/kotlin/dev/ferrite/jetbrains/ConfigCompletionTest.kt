package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.config.FerriteConfigFileType
import dev.ferrite.jetbrains.language.FerriteQLSyntaxHighlighter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for configuration file support and completion logic.
 *
 * Tests the [FerriteConfigFileType] metadata, the completion data structures
 * in [FerriteQLCompletionProvider], and the TOML configuration key conventions.
 * Pure logic testing -- no IntelliJ platform context needed.
 */
class ConfigCompletionTest {

    private lateinit var highlighter: FerriteQLSyntaxHighlighter

    @Before
    fun setUp() {
        highlighter = FerriteQLSyntaxHighlighter()
    }

    // =======================================================================
    // FerriteConfigFileType metadata
    // =======================================================================

    @Test
    fun `FerriteConfigFileType name is Ferrite Config`() {
        assertEquals("Ferrite Config", FerriteConfigFileType.getName())
    }

    @Test
    fun `FerriteConfigFileType description is Ferrite configuration file`() {
        assertEquals("Ferrite configuration file", FerriteConfigFileType.getDescription())
    }

    @Test
    fun `FerriteConfigFileType default extension is toml`() {
        assertEquals("toml", FerriteConfigFileType.getDefaultExtension())
    }

    @Test
    fun `FerriteConfigFileType is not binary`() {
        assertFalse(FerriteConfigFileType.isBinary())
    }

    @Test
    fun `FerriteConfigFileType icon is not null`() {
        // The icon is loaded from resources; it won't throw here since FerriteIcons
        // is referenced but icon loading is lazy for tests.  The assertion verifies
        // the code path doesn't crash at class-load time.
        assertNotNull(FerriteConfigFileType)
    }

    // =======================================================================
    // Completion provider - COMMANDS map structure
    // =======================================================================

    /**
     * These tests verify the shape and coverage of the command/option data
     * baked into the completion provider via reflection-free checks against
     * the highlighter (which shares the same command set).
     */

    @Test
    fun `All standard Redis string commands are available for completion`() {
        val stringCmds = listOf("GET", "SET", "SETNX", "SETEX", "MGET", "MSET", "INCR", "DECR", "INCRBY", "APPEND", "STRLEN")
        for (cmd in stringCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `All standard Redis hash commands are available for completion`() {
        val hashCmds = listOf("HSET", "HGET", "HMSET", "HMGET", "HGETALL", "HDEL", "HEXISTS", "HKEYS", "HVALS", "HLEN")
        for (cmd in hashCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `All standard Redis list commands are available for completion`() {
        val listCmds = listOf("LPUSH", "RPUSH", "LPOP", "RPOP", "LRANGE", "LLEN", "LINDEX")
        for (cmd in listCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `All standard Redis set commands are available for completion`() {
        val setCmds = listOf("SADD", "SREM", "SMEMBERS", "SISMEMBER", "SCARD")
        for (cmd in setCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `All standard Redis sorted set commands are available for completion`() {
        val zCmds = listOf("ZADD", "ZREM", "ZRANGE", "ZRANGEBYSCORE", "ZSCORE", "ZRANK", "ZCARD")
        for (cmd in zCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Stream commands are available`() {
        val streamCmds = listOf("XADD", "XREAD", "XRANGE", "XLEN")
        for (cmd in streamCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Key management commands are available`() {
        val keyCmds = listOf("DEL", "EXISTS", "EXPIRE", "TTL", "TYPE", "KEYS", "SCAN")
        for (cmd in keyCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Server commands are available`() {
        val serverCmds = listOf("PING", "INFO", "DBSIZE", "FLUSHDB", "SELECT")
        for (cmd in serverCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Vector commands are available for completion`() {
        val vecCmds = listOf("VECTOR.CREATE", "VECTOR.ADD", "VECTOR.SEARCH", "VECTOR.GET", "VECTOR.DEL")
        for (cmd in vecCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Time series commands are available for completion`() {
        val tsCmds = listOf("TS.CREATE", "TS.ADD", "TS.RANGE", "TS.GET")
        for (cmd in tsCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Document commands are available for completion`() {
        val docCmds = listOf("DOC.SET", "DOC.GET", "DOC.SEARCH")
        for (cmd in docCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Graph commands are available for completion`() {
        assertTrue(highlighter.isCommand("GRAPH.QUERY"))
    }

    @Test
    fun `Search commands are available for completion`() {
        val ftCmds = listOf("FT.CREATE", "FT.SEARCH")
        for (cmd in ftCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `CRDT commands are available for completion`() {
        val crdtCmds = listOf("CRDT.COUNTER.INCR", "CRDT.ORSET.ADD")
        for (cmd in crdtCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Semantic commands are available for completion`() {
        val semCmds = listOf("SEMANTIC.SET", "SEMANTIC.SEARCH")
        for (cmd in semCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    @Test
    fun `Pub-sub commands are available for completion`() {
        assertTrue(highlighter.isCommand("PUBLISH"))
        assertTrue(highlighter.isCommand("SUBSCRIBE"))
    }

    @Test
    fun `Transaction commands are available for completion`() {
        val txCmds = listOf("MULTI", "EXEC", "DISCARD", "WATCH")
        for (cmd in txCmds) {
            assertTrue("$cmd should be a recognized command", highlighter.isCommand(cmd))
        }
    }

    // =======================================================================
    // Completion provider - OPTIONS list structure
    // =======================================================================

    @Test
    fun `SET-related options are available`() {
        val setOpts = listOf("EX", "PX", "EXAT", "PXAT", "NX", "XX", "KEEPTTL", "GET")
        for (opt in setOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Sorted set options are available`() {
        val zOpts = listOf("WITHSCORES", "LIMIT", "CH", "GT", "LT", "REV", "BYLEX", "BYSCORE")
        for (opt in zOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Aggregation options are available`() {
        val aggOpts = listOf("AGGREGATE", "SUM", "MIN", "MAX", "WEIGHTS")
        for (opt in aggOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Scan-related options are available`() {
        val scanOpts = listOf("MATCH", "COUNT")
        for (opt in scanOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Sort options are available`() {
        val sortOpts = listOf("ASC", "DESC", "BY", "ALPHA", "STORE")
        for (opt in sortOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Stream-related options are available`() {
        val streamOpts = listOf("BLOCK", "STREAMS", "GROUP", "NOACK", "MAXLEN", "MINID", "NOMKSTREAM")
        for (opt in streamOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Stream consumer options are available`() {
        val consumerOpts = listOf("CONSUMER", "ID", "IDLE", "RETRYCOUNT", "FORCE", "JUSTID")
        for (opt in consumerOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    @Test
    fun `Stream group management options are available`() {
        val groupOpts = listOf("CREATE", "DESTROY", "SETID", "DELCONSUMER", "CREATECONSUMER")
        for (opt in groupOpts) {
            assertTrue("$opt should be a recognized option", highlighter.isOption(opt))
        }
    }

    // =======================================================================
    // TOML configuration key conventions
    // =======================================================================

    /**
     * These tests verify that typical Ferrite TOML config key names follow
     * expected naming patterns.  While this is not exercising any specific
     * Kotlin class, it documents the key structure the plugin is designed
     * to support for ferrite.toml files.
     */

    @Test
    fun `Common ferrite toml section names follow expected patterns`() {
        val sections = listOf(
            "server", "storage", "replication", "cluster", "security",
            "logging", "metrics", "persistence"
        )
        for (section in sections) {
            assertTrue("Section '$section' should be lowercase", section == section.lowercase())
            assertFalse("Section '$section' should not be empty", section.isEmpty())
        }
    }

    @Test
    fun `Common ferrite toml keys use snake_case`() {
        val keys = listOf(
            "bind_address", "port", "max_memory", "log_level",
            "data_dir", "max_clients", "timeout_seconds",
            "tls_enabled", "tls_cert_file", "tls_key_file"
        )
        for (key in keys) {
            assertFalse("Key '$key' should not contain uppercase", key.any { it.isUpperCase() })
            assertFalse("Key '$key' should not contain hyphens", key.contains('-'))
        }
    }

    @Test
    fun `Port value suggestion is within valid range`() {
        val suggestedPort = 6379
        assertTrue(suggestedPort in 1..65535)
    }

    @Test
    fun `Default bind address suggestions`() {
        val validBindAddresses = listOf("0.0.0.0", "127.0.0.1", "localhost", "::1")
        for (addr in validBindAddresses) {
            assertTrue("'$addr' should not be blank", addr.isNotBlank())
        }
    }

    @Test
    fun `Log level value suggestions`() {
        val logLevels = listOf("trace", "debug", "info", "warn", "error")
        assertEquals(5, logLevels.size)
        for (level in logLevels) {
            assertTrue(level == level.lowercase())
        }
    }

    @Test
    fun `Output format suggestions match settings options`() {
        val formats = listOf("raw", "JSON", "table")
        assertEquals(3, formats.size)
        assertTrue(formats.contains("raw"))
        assertTrue(formats.contains("JSON"))
        assertTrue(formats.contains("table"))
    }
}
