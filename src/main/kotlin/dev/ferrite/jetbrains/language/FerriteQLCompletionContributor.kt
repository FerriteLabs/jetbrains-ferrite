package dev.ferrite.jetbrains.language

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class FerriteQLCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            FerriteQLCompletionProvider()
        )
    }
}

class FerriteQLCompletionProvider : CompletionProvider<CompletionParameters>() {

    companion object {
        private val COMMANDS = mapOf(
            // String commands
            "GET" to "GET key - Get the value of a key",
            "SET" to "SET key value [EX seconds] [PX ms] [NX|XX] - Set key to hold value",
            "SETNX" to "SETNX key value - Set key only if it does not exist",
            "SETEX" to "SETEX key seconds value - Set key with expiration",
            "MGET" to "MGET key [key ...] - Get values of multiple keys",
            "MSET" to "MSET key value [key value ...] - Set multiple keys",
            "INCR" to "INCR key - Increment the integer value of a key",
            "DECR" to "DECR key - Decrement the integer value of a key",
            "INCRBY" to "INCRBY key increment - Increment by a specific amount",
            "APPEND" to "APPEND key value - Append value to a key",
            "STRLEN" to "STRLEN key - Get the length of the value",

            // Hash commands
            "HSET" to "HSET key field value [field value ...] - Set hash field(s)",
            "HGET" to "HGET key field - Get hash field value",
            "HMSET" to "HMSET key field value [field value ...] - Set multiple hash fields",
            "HMGET" to "HMGET key field [field ...] - Get multiple hash field values",
            "HGETALL" to "HGETALL key - Get all fields and values in a hash",
            "HDEL" to "HDEL key field [field ...] - Delete hash field(s)",
            "HEXISTS" to "HEXISTS key field - Check if hash field exists",
            "HKEYS" to "HKEYS key - Get all field names in a hash",
            "HVALS" to "HVALS key - Get all values in a hash",
            "HLEN" to "HLEN key - Get number of fields in a hash",

            // List commands
            "LPUSH" to "LPUSH key value [value ...] - Prepend values to a list",
            "RPUSH" to "RPUSH key value [value ...] - Append values to a list",
            "LPOP" to "LPOP key [count] - Remove and get the first element(s)",
            "RPOP" to "RPOP key [count] - Remove and get the last element(s)",
            "LRANGE" to "LRANGE key start stop - Get range of elements",
            "LLEN" to "LLEN key - Get list length",
            "LINDEX" to "LINDEX key index - Get element by index",

            // Set commands
            "SADD" to "SADD key member [member ...] - Add members to a set",
            "SREM" to "SREM key member [member ...] - Remove members from a set",
            "SMEMBERS" to "SMEMBERS key - Get all members of a set",
            "SISMEMBER" to "SISMEMBER key member - Check if member exists",
            "SCARD" to "SCARD key - Get set cardinality",

            // Sorted set commands
            "ZADD" to "ZADD key [NX|XX] [GT|LT] [CH] score member [score member ...] - Add to sorted set",
            "ZREM" to "ZREM key member [member ...] - Remove members from sorted set",
            "ZRANGE" to "ZRANGE key start stop [WITHSCORES] - Get range by index",
            "ZRANGEBYSCORE" to "ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]",
            "ZSCORE" to "ZSCORE key member - Get member score",
            "ZRANK" to "ZRANK key member - Get member rank",
            "ZCARD" to "ZCARD key - Get sorted set cardinality",

            // Stream commands
            "XADD" to "XADD key [MAXLEN|MINID] ID field value [field value ...] - Add to stream",
            "XREAD" to "XREAD [COUNT count] [BLOCK ms] STREAMS key [key ...] ID [ID ...]",
            "XRANGE" to "XRANGE key start end [COUNT count] - Get range of stream entries",
            "XLEN" to "XLEN key - Get stream length",

            // Key commands
            "DEL" to "DEL key [key ...] - Delete keys",
            "EXISTS" to "EXISTS key [key ...] - Check if keys exist",
            "EXPIRE" to "EXPIRE key seconds - Set key expiration",
            "TTL" to "TTL key - Get time to live",
            "TYPE" to "TYPE key - Get key type",
            "KEYS" to "KEYS pattern - Find keys matching pattern",
            "SCAN" to "SCAN cursor [MATCH pattern] [COUNT count] - Incrementally iterate keys",

            // Server commands
            "PING" to "PING [message] - Test connection",
            "INFO" to "INFO [section] - Get server information",
            "DBSIZE" to "DBSIZE - Get number of keys in database",
            "FLUSHDB" to "FLUSHDB [ASYNC] - Remove all keys from current database",
            "SELECT" to "SELECT index - Select database",

            // Vector commands
            "VECTOR.CREATE" to "VECTOR.CREATE index [HNSW|FLAT] DIM dims [DISTANCE L2|COSINE|IP]",
            "VECTOR.ADD" to "VECTOR.ADD index id vector [field value ...]",
            "VECTOR.SEARCH" to "VECTOR.SEARCH index vector TOP_K k [FILTER expr]",
            "VECTOR.GET" to "VECTOR.GET index id - Get vector by ID",
            "VECTOR.DEL" to "VECTOR.DEL index id [id ...] - Delete vectors",

            // Time series commands
            "TS.CREATE" to "TS.CREATE key [RETENTION ms] [ENCODING enc] [LABELS label value ...]",
            "TS.ADD" to "TS.ADD key timestamp value [LABELS label value ...]",
            "TS.RANGE" to "TS.RANGE key fromTimestamp toTimestamp [COUNT count] [AGGREGATION agg timeBucket]",
            "TS.GET" to "TS.GET key - Get latest sample",

            // Document commands
            "DOC.SET" to "DOC.SET key path value - Set document field",
            "DOC.GET" to "DOC.GET key [path ...] - Get document or fields",
            "DOC.SEARCH" to "DOC.SEARCH index query - Search documents",

            // Graph commands
            "GRAPH.QUERY" to "GRAPH.QUERY graph query - Execute Cypher query",

            // Search commands
            "FT.CREATE" to "FT.CREATE index [ON HASH|JSON] PREFIX count prefix SCHEMA field type ...",
            "FT.SEARCH" to "FT.SEARCH index query [LIMIT offset num] [RETURN num field ...]",

            // CRDT commands
            "CRDT.COUNTER.INCR" to "CRDT.COUNTER.INCR key [amount] - Increment CRDT counter",
            "CRDT.ORSET.ADD" to "CRDT.ORSET.ADD key member [member ...] - Add to OR-Set",

            // Semantic commands
            "SEMANTIC.SET" to "SEMANTIC.SET key text [TTL seconds] - Set semantic cache entry",
            "SEMANTIC.SEARCH" to "SEMANTIC.SEARCH query [TOP_K k] [THRESHOLD score] - Semantic search",
            "SEMANTIC.GET" to "SEMANTIC.GET query [threshold] - Search semantic cache by similarity",

            // Time-travel commands
            "HISTORY" to "HISTORY key [SINCE duration] - View key version history",
            "DIFF" to "DIFF key version1 version2 - Compare key versions",
            "RESTORE.FROM" to "RESTORE.FROM key version - Restore key from version",

            // FerriteQL query
            "QUERY" to "QUERY \"sql\" - Execute FerriteQL SQL-like query",

            // Pub/Sub
            "PUBLISH" to "PUBLISH channel message - Publish message to channel",
            "SUBSCRIBE" to "SUBSCRIBE channel [channel ...] - Subscribe to channels",

            // Transactions
            "MULTI" to "MULTI - Start transaction",
            "EXEC" to "EXEC - Execute transaction",
            "DISCARD" to "DISCARD - Discard transaction",
            "WATCH" to "WATCH key [key ...] - Watch keys for changes",
        )

        private val OPTIONS = listOf(
            "EX", "PX", "EXAT", "PXAT", "NX", "XX", "KEEPTTL", "GET",
            "WITHSCORES", "LIMIT", "MATCH", "COUNT", "ASC", "DESC",
            "BY", "ALPHA", "STORE", "WITHCOORD", "WITHDIST", "CH",
            "INCR", "GT", "LT", "REV", "BYLEX", "BYSCORE", "AGGREGATE",
            "SUM", "MIN", "MAX", "BLOCK", "STREAMS", "GROUP", "NOACK",
            "TOP_K", "FILTER", "DISTANCE", "L2", "COSINE", "IP", "HNSW", "FLAT",
            // FerriteQL SQL-like keywords
            "SELECT", "FROM", "WHERE", "ORDER", "GROUP BY", "ORDER BY",
            "DELETE FROM", "AS OF", "BETWEEN", "AND", "OR", "NOT", "IN",
            "LIKE", "IS", "NULL", "TRUE", "FALSE"
        )
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val prefix = result.prefixMatcher.prefix.uppercase()

        // Add command completions
        COMMANDS.forEach { (command, description) ->
            if (command.startsWith(prefix) || prefix.isEmpty()) {
                result.addElement(
                    LookupElementBuilder.create(command)
                        .withTypeText(description.substringBefore(" - "))
                        .withTailText(" - ${description.substringAfter(" - ")}", true)
                        .withIcon(FerriteIcons.FERRITE)
                        .bold()
                )
            }
        }

        // Add option completions
        OPTIONS.forEach { option ->
            if (option.startsWith(prefix) || prefix.isEmpty()) {
                result.addElement(
                    LookupElementBuilder.create(option)
                        .withTypeText("Option")
                        .withIcon(FerriteIcons.KEY)
                )
            }
        }
    }
}
