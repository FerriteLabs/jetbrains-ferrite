package dev.ferrite.jetbrains.language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class FerriteQLAnnotator : Annotator {

    private val highlighter = FerriteQLSyntaxHighlighter()

    // Commands that require a minimum argument count
    private val commandArgRequirements = mapOf(
        // String commands
        "GET" to 1, "SET" to 2, "SETNX" to 2, "SETEX" to 3,
        "GETSET" to 2, "GETDEL" to 1, "GETEX" to 1, "MGET" to 1, "MSET" to 2,
        "APPEND" to 2, "INCR" to 1, "DECR" to 1, "INCRBY" to 2, "DECRBY" to 2,
        "INCRBYFLOAT" to 2, "STRLEN" to 1, "GETRANGE" to 3, "SETRANGE" to 3,
        // Hash commands
        "HSET" to 3, "HGET" to 2, "HDEL" to 2, "HEXISTS" to 2,
        "HGETALL" to 1, "HKEYS" to 1, "HVALS" to 1, "HLEN" to 1,
        "HINCRBY" to 3, "HINCRBYFLOAT" to 3, "HSETNX" to 3, "HMSET" to 3, "HMGET" to 2,
        // List commands
        "LPUSH" to 2, "RPUSH" to 2, "LPOP" to 1, "RPOP" to 1,
        "LRANGE" to 3, "LINDEX" to 2, "LSET" to 3, "LLEN" to 1,
        "LINSERT" to 4, "LTRIM" to 3, "LREM" to 3, "LPOS" to 2,
        "BLPOP" to 2, "BRPOP" to 2, "LMPOP" to 2,
        // Set commands
        "SADD" to 2, "SREM" to 2, "SISMEMBER" to 2, "SMISMEMBER" to 2,
        "SMEMBERS" to 1, "SCARD" to 1, "SPOP" to 1, "SRANDMEMBER" to 1,
        "SINTER" to 1, "SUNION" to 1, "SDIFF" to 1,
        "SINTERSTORE" to 2, "SUNIONSTORE" to 2, "SDIFFSTORE" to 2,
        "SMOVE" to 3,
        // Sorted set commands
        "ZADD" to 3, "ZREM" to 2, "ZSCORE" to 2, "ZRANK" to 2,
        "ZREVRANK" to 2, "ZRANGE" to 3, "ZREVRANGE" to 3,
        "ZRANGEBYSCORE" to 3, "ZCOUNT" to 3, "ZCARD" to 1,
        "ZINCRBY" to 3, "ZRANGESTORE" to 4,
        "ZUNIONSTORE" to 2, "ZINTERSTORE" to 2,
        // Key commands
        "DEL" to 1, "EXISTS" to 1, "TYPE" to 1, "TTL" to 1, "PTTL" to 1,
        "EXPIRE" to 2, "EXPIREAT" to 2, "PEXPIRE" to 2, "PEXPIREAT" to 2,
        "PERSIST" to 1, "RENAME" to 2, "RENAMENX" to 2,
        "KEYS" to 1, "SCAN" to 1, "SORT" to 1, "DUMP" to 1,
        "RESTORE" to 3, "OBJECT" to 2, "UNLINK" to 1, "COPY" to 2,
        "SELECT" to 1, "MOVE" to 2, "RANDOMKEY" to 0, "TOUCH" to 1,
        // Stream commands
        "XADD" to 4, "XRANGE" to 3, "XREVRANGE" to 3, "XLEN" to 1,
        "XREAD" to 3, "XINFO" to 2, "XTRIM" to 2,
        "XGROUP" to 3, "XACK" to 3, "XCLAIM" to 5, "XPENDING" to 2,
        // Pub/Sub
        "PUBLISH" to 2, "SUBSCRIBE" to 1, "UNSUBSCRIBE" to 0,
        "PSUBSCRIBE" to 1, "PUNSUBSCRIBE" to 0,
        // Transaction
        "WATCH" to 1, "MULTI" to 0, "EXEC" to 0, "DISCARD" to 0, "UNWATCH" to 0,
        // Scripting
        "EVAL" to 2, "EVALSHA" to 2,
        // Server
        "PING" to 0, "INFO" to 0, "DBSIZE" to 0, "FLUSHDB" to 0, "FLUSHALL" to 0,
        "CONFIG" to 1, "CLIENT" to 1, "SLOWLOG" to 1,
        // Geo
        "GEOADD" to 4, "GEODIST" to 3, "GEOPOS" to 2, "GEOHASH" to 2,
        "GEOSEARCH" to 3,
        // HyperLogLog
        "PFADD" to 2, "PFCOUNT" to 1, "PFMERGE" to 2,
        // Bitmap
        "SETBIT" to 3, "GETBIT" to 2, "BITCOUNT" to 1, "BITOP" to 3,
        // Ferrite: Vector Search
        "VECTOR.CREATE" to 3, "VECTOR.ADD" to 3, "VECTOR.SEARCH" to 3,
        "VECTOR.DELETE" to 2, "VECTOR.INFO" to 1,
        "VECTOR.INDEX.CREATE" to 3, "VECTOR.INDEX.DROP" to 1,
        // Ferrite: Semantic Cache
        "SEMANTIC.SET" to 2, "SEMANTIC.GET" to 1, "SEMANTIC.DEL" to 1,
        "SEMANTIC.SEARCH" to 1, "SEMANTIC.STATS" to 0,
        // Ferrite: Time Series
        "TS.CREATE" to 1, "TS.ADD" to 3, "TS.RANGE" to 3,
        "TS.GET" to 1, "TS.INFO" to 1, "TS.DEL" to 3,
        "TS.MRANGE" to 3, "TS.MGET" to 1,
        // Ferrite: Documents
        "DOC.INSERT" to 3, "DOC.GET" to 2, "DOC.DELETE" to 2,
        "DOC.FIND" to 2, "DOC.SEARCH" to 2, "DOC.SET" to 3,
        "DOC.INDEX" to 2, "DOC.COUNT" to 1,
        // Ferrite: Graph
        "GRAPH.QUERY" to 2, "GRAPH.CREATE" to 1, "GRAPH.DELETE" to 1,
        // Ferrite: Full-Text Search
        "FT.CREATE" to 2, "FT.SEARCH" to 2, "FT.AGGREGATE" to 2,
        "FT.INFO" to 1, "FT.DROPINDEX" to 1,
        // Ferrite: CRDT
        "CRDT.COUNTER" to 2, "CRDT.ORSET" to 2, "CRDT.LWWREG" to 2,
        // Ferrite: FerriteQL
        "QUERY" to 1,
    )

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return

        // Only annotate on full lines starting from COMMAND tokens
        if (node.elementType != FerriteQLTokenTypes.COMMAND) return

        val lineText = getLineText(element) ?: return
        val trimmed = lineText.trim()
        if (trimmed.isBlank() || trimmed.startsWith("#")) return

        val parts = parseCommandLine(trimmed)
        if (parts.isEmpty()) return

        val command = parts[0].uppercase()

        // Check if command is known
        if (!highlighter.isCommand(command)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown command: $command")
                .range(element)
                .create()
            return
        }

        // Validate argument count
        val requiredArgs = commandArgRequirements[command]
        if (requiredArgs != null) {
            val argCount = parts.size - 1
            if (argCount < requiredArgs) {
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    "$command requires at least $requiredArgs argument(s), got $argCount"
                ).range(element).create()
            }
        }
    }

    private fun getLineText(element: PsiElement): String? {
        val file = element.containingFile ?: return null
        val document = com.intellij.psi.PsiDocumentManager.getInstance(file.project)
            .getDocument(file) ?: return null
        val lineNumber = document.getLineNumber(element.textOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        return document.getText(com.intellij.openapi.util.TextRange(lineStart, lineEnd))
    }

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
}
