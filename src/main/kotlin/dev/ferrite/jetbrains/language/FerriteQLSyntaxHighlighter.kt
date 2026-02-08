package dev.ferrite.jetbrains.language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

class FerriteQLSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        // Text attribute keys
        val COMMAND = createTextAttributesKey("FERRITE_COMMAND", DefaultLanguageHighlighterColors.KEYWORD)
        val STRING = createTextAttributesKey("FERRITE_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER = createTextAttributesKey("FERRITE_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT = createTextAttributesKey("FERRITE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val KEY = createTextAttributesKey("FERRITE_KEY", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val OPTION = createTextAttributesKey("FERRITE_OPTION", DefaultLanguageHighlighterColors.METADATA)
        val BAD_CHARACTER = createTextAttributesKey("FERRITE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        // Command categories
        private val STRING_COMMANDS = setOf(
            "GET", "SET", "SETNX", "SETEX", "PSETEX", "MGET", "MSET", "MSETNX",
            "INCR", "INCRBY", "INCRBYFLOAT", "DECR", "DECRBY", "APPEND", "STRLEN",
            "GETRANGE", "SETRANGE", "GETSET", "GETEX", "GETDEL"
        )

        private val HASH_COMMANDS = setOf(
            "HSET", "HGET", "HMSET", "HMGET", "HGETALL", "HDEL", "HEXISTS",
            "HINCRBY", "HINCRBYFLOAT", "HKEYS", "HVALS", "HLEN", "HSETNX",
            "HSCAN", "HRANDFIELD"
        )

        private val LIST_COMMANDS = setOf(
            "LPUSH", "RPUSH", "LPOP", "RPOP", "LRANGE", "LLEN", "LINDEX",
            "LSET", "LINSERT", "LREM", "LTRIM", "BLPOP", "BRPOP", "LMOVE",
            "BLMOVE", "LPOS", "LMPOP"
        )

        private val SET_COMMANDS = setOf(
            "SADD", "SREM", "SMEMBERS", "SISMEMBER", "SCARD", "SPOP",
            "SRANDMEMBER", "SDIFF", "SINTER", "SUNION", "SDIFFSTORE",
            "SINTERSTORE", "SUNIONSTORE", "SMOVE", "SSCAN", "SMISMEMBER"
        )

        private val SORTED_SET_COMMANDS = setOf(
            "ZADD", "ZREM", "ZRANGE", "ZRANGEBYSCORE", "ZRANGEBYLEX",
            "ZREVRANGE", "ZREVRANGEBYSCORE", "ZREVRANGEBYLEX", "ZRANK",
            "ZREVRANK", "ZSCORE", "ZCARD", "ZCOUNT", "ZINCRBY", "ZUNIONSTORE",
            "ZINTERSTORE", "ZSCAN", "ZPOPMIN", "ZPOPMAX", "BZPOPMIN", "BZPOPMAX",
            "ZRANDMEMBER", "ZRANGESTORE", "ZMPOP", "BZMPOP", "ZINTER", "ZUNION"
        )

        private val STREAM_COMMANDS = setOf(
            "XADD", "XREAD", "XREADGROUP", "XRANGE", "XREVRANGE", "XLEN",
            "XINFO", "XTRIM", "XDEL", "XGROUP", "XACK", "XCLAIM", "XPENDING",
            "XAUTOCLAIM", "XSETID"
        )

        private val KEY_COMMANDS = setOf(
            "DEL", "EXISTS", "EXPIRE", "EXPIREAT", "PEXPIRE", "PEXPIREAT",
            "TTL", "PTTL", "PERSIST", "TYPE", "RENAME", "RENAMENX", "KEYS",
            "SCAN", "RANDOMKEY", "TOUCH", "UNLINK", "OBJECT", "DUMP", "RESTORE",
            "SORT", "COPY", "EXPIRETIME", "PEXPIRETIME"
        )

        private val SERVER_COMMANDS = setOf(
            "PING", "INFO", "DBSIZE", "FLUSHDB", "FLUSHALL", "SELECT",
            "AUTH", "CONFIG", "CLIENT", "SLOWLOG", "DEBUG", "MEMORY",
            "COMMAND", "TIME", "LASTSAVE", "BGSAVE", "BGREWRITEAOF",
            "SHUTDOWN", "MONITOR", "SLAVEOF", "REPLICAOF", "WAIT", "ACL"
        )

        private val PUBSUB_COMMANDS = setOf(
            "SUBSCRIBE", "UNSUBSCRIBE", "PUBLISH", "PSUBSCRIBE", "PUNSUBSCRIBE",
            "PUBSUB", "SSUBSCRIBE", "SUNSUBSCRIBE", "SPUBLISH"
        )

        private val TRANSACTION_COMMANDS = setOf(
            "MULTI", "EXEC", "DISCARD", "WATCH", "UNWATCH"
        )

        private val SCRIPTING_COMMANDS = setOf(
            "EVAL", "EVALSHA", "SCRIPT", "EVALSHA_RO", "EVAL_RO",
            "FUNCTION", "FCALL", "FCALL_RO"
        )

        private val CLUSTER_COMMANDS = setOf(
            "CLUSTER", "READONLY", "READWRITE", "ASKING"
        )

        // Ferrite-specific commands
        private val VECTOR_COMMANDS = setOf(
            "VECTOR.CREATE", "VECTOR.ADD", "VECTOR.GET", "VECTOR.DEL",
            "VECTOR.SEARCH", "VECTOR.INFO", "VECTOR.DIM", "VECTOR.COUNT"
        )

        private val TIMESERIES_COMMANDS = setOf(
            "TS.CREATE", "TS.ADD", "TS.GET", "TS.RANGE", "TS.REVRANGE",
            "TS.MRANGE", "TS.MREVRANGE", "TS.INFO", "TS.DEL", "TS.ALTER",
            "TS.CREATERULE", "TS.DELETERULE", "TS.MADD", "TS.INCRBY", "TS.DECRBY"
        )

        private val DOCUMENT_COMMANDS = setOf(
            "DOC.SET", "DOC.GET", "DOC.DEL", "DOC.SEARCH", "DOC.INDEX",
            "DOC.QUERY", "DOC.AGGREGATE", "DOC.UPDATE", "DOC.MGET"
        )

        private val GRAPH_COMMANDS = setOf(
            "GRAPH.QUERY", "GRAPH.CREATE", "GRAPH.DELETE", "GRAPH.EXPLAIN",
            "GRAPH.PROFILE", "GRAPH.LIST", "GRAPH.CONSTRAINT", "GRAPH.SLOWLOG"
        )

        private val SEARCH_COMMANDS = setOf(
            "FT.CREATE", "FT.SEARCH", "FT.AGGREGATE", "FT.INFO", "FT.DROPINDEX",
            "FT.ALIASADD", "FT.ALIASDEL", "FT.ALIASUPDATE", "FT.TAGVALS",
            "FT.SUGADD", "FT.SUGGET", "FT.SUGDEL", "FT.SUGLEN", "FT.SYNDUMP"
        )

        private val CRDT_COMMANDS = setOf(
            "CRDT.COUNTER.INCR", "CRDT.COUNTER.GET", "CRDT.COUNTER.MERGE",
            "CRDT.LWWREG.SET", "CRDT.LWWREG.GET", "CRDT.LWWREG.MERGE",
            "CRDT.ORSET.ADD", "CRDT.ORSET.REM", "CRDT.ORSET.GET", "CRDT.ORSET.MERGE",
            "CRDT.SYNC", "CRDT.INFO"
        )

        private val SEMANTIC_COMMANDS = setOf(
            "SEMANTIC.SET", "SEMANTIC.GET", "SEMANTIC.SEARCH", "SEMANTIC.DEL",
            "SEMANTIC.CACHE", "SEMANTIC.INVALIDATE"
        )

        private val ALL_COMMANDS = STRING_COMMANDS + HASH_COMMANDS + LIST_COMMANDS +
            SET_COMMANDS + SORTED_SET_COMMANDS + STREAM_COMMANDS + KEY_COMMANDS +
            SERVER_COMMANDS + PUBSUB_COMMANDS + TRANSACTION_COMMANDS +
            SCRIPTING_COMMANDS + CLUSTER_COMMANDS + VECTOR_COMMANDS +
            TIMESERIES_COMMANDS + DOCUMENT_COMMANDS + GRAPH_COMMANDS +
            SEARCH_COMMANDS + CRDT_COMMANDS + SEMANTIC_COMMANDS

        // Options
        private val OPTIONS = setOf(
            "EX", "PX", "EXAT", "PXAT", "NX", "XX", "KEEPTTL", "GET", "IFEQ", "IFGT",
            "WITHSCORES", "LIMIT", "MATCH", "COUNT", "ASC", "DESC", "BY", "ALPHA",
            "STORE", "STOREDIST", "WITHCOORD", "WITHDIST", "WITHHASH", "ANY",
            "CH", "INCR", "GT", "LT", "REV", "BYLEX", "BYSCORE", "WEIGHTS", "AGGREGATE",
            "SUM", "MIN", "MAX", "BLOCK", "STREAMS", "GROUP", "CONSUMER", "NOACK",
            "ID", "IDLE", "TIME", "RETRYCOUNT", "FORCE", "JUSTID", "MINID", "MAXLEN",
            "NOMKSTREAM", "CREATE", "DESTROY", "SETID", "DELCONSUMER", "CREATECONSUMER"
        )
    }

    override fun getHighlightingLexer(): Lexer = FerriteQLLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            FerriteQLTokenTypes.COMMAND -> arrayOf(COMMAND)
            FerriteQLTokenTypes.STRING -> arrayOf(STRING)
            FerriteQLTokenTypes.NUMBER -> arrayOf(NUMBER)
            FerriteQLTokenTypes.COMMENT -> arrayOf(COMMENT)
            FerriteQLTokenTypes.KEY -> arrayOf(KEY)
            FerriteQLTokenTypes.OPTION -> arrayOf(OPTION)
            FerriteQLTokenTypes.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)
            else -> emptyArray()
        }
    }

    fun isCommand(text: String): Boolean = text.uppercase() in ALL_COMMANDS
    fun isOption(text: String): Boolean = text.uppercase() in OPTIONS
}

class FerriteQLSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return FerriteQLSyntaxHighlighter()
    }
}
