package dev.ferrite.jetbrains.service

/**
 * Encodes and decodes messages using the Redis Serialization Protocol (RESP).
 *
 * This codec handles RESP2 and RESP3 framing so that higher-level service code
 * does not need to deal with raw byte manipulation. Currently the plugin still
 * delegates to the Lettuce driver for actual I/O; this class is intended for
 * future use when we add direct socket support for Ferrite-specific extensions.
 */
class RespCodec {

    companion object {
        const val SIMPLE_STRING_PREFIX = '+'
        const val ERROR_PREFIX = '-'
        const val INTEGER_PREFIX = ':'
        const val BULK_STRING_PREFIX = '$'
        const val ARRAY_PREFIX = '*'
        const val CRLF = "\r\n"
    }

    /**
     * Encode a list of command arguments into a RESP array request.
     */
    fun encode(args: List<String>): String {
        val sb = StringBuilder()
        sb.append(ARRAY_PREFIX).append(args.size).append(CRLF)
        for (arg in args) {
            sb.append(BULK_STRING_PREFIX).append(arg.length).append(CRLF)
            sb.append(arg).append(CRLF)
        }
        return sb.toString()
    }
}
