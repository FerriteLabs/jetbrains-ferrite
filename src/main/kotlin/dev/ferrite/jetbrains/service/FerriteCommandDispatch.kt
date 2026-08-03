package dev.ferrite.jetbrains.service

import io.lettuce.core.protocol.CommandType
import io.lettuce.core.protocol.ProtocolKeyword

/**
 * Resolves a command name to the Lettuce [ProtocolKeyword] used to dispatch it.
 *
 * Known Redis commands map to the built-in [CommandType] enum; Ferrite-specific
 * commands (VECTOR.*, SEMANTIC.*, TS.*, DOC.*, ...) that are absent from that
 * enum are dispatched as raw custom keywords. Extracted from
 * [FerriteConnectionManager] to isolate the pure dispatch decision.
 *
 * The caller supplies the command in the exact form used for dispatch
 * ([FerriteConnectionManager] upper-cases it first); this preserves the original
 * raw-dispatch behavior byte-for-byte.
 */
internal object FerriteCommandDispatch {

    fun resolveKeyword(command: String): ProtocolKeyword {
        val commandType = try {
            CommandType.valueOf(command)
        } catch (_: IllegalArgumentException) {
            null
        }
        return commandType ?: rawKeyword(command)
    }

    private fun rawKeyword(command: String): ProtocolKeyword = object : ProtocolKeyword {
        override fun getBytes(): ByteArray = command.toByteArray(Charsets.US_ASCII)
        override fun name(): String = command
    }
}
