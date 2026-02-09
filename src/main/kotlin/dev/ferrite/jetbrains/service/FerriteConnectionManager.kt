package dev.ferrite.jetbrains.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands

@Service(Service.Level.PROJECT)
class FerriteConnectionManager(private val project: Project) {

    private val connections = mutableMapOf<String, ConnectionConfig>()
    private var currentConnection: StatefulRedisConnection<String, String>? = null
    private var currentClient: RedisClient? = null
    private var currentConnectionName: String? = null

    fun addConnection(config: ConnectionConfig) {
        connections[config.name] = config
    }

    fun removeConnection(name: String) {
        if (currentConnectionName == name) {
            disconnect()
        }
        connections.remove(name)
    }

    fun getConnections(): List<ConnectionConfig> = connections.values.toList()

    fun connect(name: String): Boolean {
        val config = connections[name] ?: return false

        try {
            disconnect()

            val uriBuilder = RedisURI.builder()
                .withHost(config.host)
                .withPort(config.port)
                .withDatabase(config.database)

            if (config.password.isNotBlank()) {
                uriBuilder.withPassword(config.password.toCharArray())
            }

            if (config.useTls) {
                uriBuilder.withSsl(true)
            }

            currentClient = RedisClient.create(uriBuilder.build())
            currentConnection = currentClient?.connect()
            currentConnectionName = name

            // Test connection
            currentConnection?.sync()?.ping()

            return true
        } catch (e: Exception) {
            disconnect()
            throw e
        }
    }

    fun disconnect() {
        try {
            currentConnection?.close()
            currentClient?.shutdown()
        } finally {
            currentConnection = null
            currentClient = null
            currentConnectionName = null
        }
    }

    fun isConnected(): Boolean = currentConnection?.isOpen == true

    fun getCurrentConnectionName(): String? = currentConnectionName

    fun getCommands(): RedisCommands<String, String>? = currentConnection?.sync()

    fun executeCommand(command: String): String {
        val commands = getCommands() ?: return "Error: Not connected"

        try {
            val parts = parseCommand(command)
            if (parts.isEmpty()) return ""

            val cmd = parts[0].uppercase()
            val args = parts.drop(1).toTypedArray()

            val result = commands.dispatch(
                io.lettuce.core.protocol.CommandType.valueOf(cmd),
                io.lettuce.core.output.StatusOutput(io.lettuce.core.codec.StringCodec.UTF8),
                io.lettuce.core.protocol.CommandArgs(io.lettuce.core.codec.StringCodec.UTF8).apply {
                    args.forEach { add(it) }
                }
            )

            return result?.toString() ?: "(nil)"
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }

    fun scanKeys(pattern: String, count: Int): List<String> {
        val commands = getCommands() ?: return emptyList()

        try {
            val keys = mutableListOf<String>()
            var cursor = io.lettuce.core.ScanCursor.INITIAL

            while (!cursor.isFinished && keys.size < count) {
                val scanArgs = io.lettuce.core.ScanArgs().match(pattern).limit(100)
                val result = commands.scan(cursor, scanArgs)
                keys.addAll(result.keys)
                cursor = result
            }

            return keys.take(count)
        } catch (e: Exception) {
            return emptyList()
        }
    }

    fun getKeyType(key: String): String {
        val commands = getCommands() ?: return "unknown"
        return try {
            commands.type(key) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun getServerInfo(): String {
        val commands = getCommands() ?: return "Not connected"
        return try {
            commands.info() ?: "No info available"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun parseCommand(command: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inQuote = false
        var quoteChar = ' '

        for (char in command) {
            when {
                !inQuote && (char == '"' || char == '\'') -> {
                    inQuote = true
                    quoteChar = char
                }
                inQuote && char == quoteChar -> {
                    inQuote = false
                }
                !inQuote && char.isWhitespace() -> {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current = StringBuilder()
                    }
                }
                else -> {
                    current.append(char)
                }
            }
        }

        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }

        return parts
    }

    companion object {
        fun getInstance(project: Project): FerriteConnectionManager = project.service()
    }

    data class ConnectionConfig(
        val name: String,
        val host: String,
        val port: Int = 6379,
        val password: String = "",
        val database: Int = 0,
        val useTls: Boolean = false
    )
}
