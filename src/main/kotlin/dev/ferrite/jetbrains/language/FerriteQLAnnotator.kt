package dev.ferrite.jetbrains.language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class FerriteQLAnnotator : Annotator {

    private val highlighter = FerriteQLSyntaxHighlighter()

    // Commands that require a minimum argument count
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
