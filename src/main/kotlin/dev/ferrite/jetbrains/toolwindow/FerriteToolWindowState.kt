package dev.ferrite.jetbrains.toolwindow

/**
 * Pure view-state decisions for the Ferrite tool window, extracted from
 * [FerriteToolWindow] so they can be reasoned about and unit-tested without a
 * Swing/IntelliJ runtime. Holds no mutable state and performs no I/O.
 */
internal object FerriteToolWindowState {

    const val SCAN_ALL_PATTERN = "*"

    private const val POOL_ACTIVE = "Pool: active (1 conn)"
    private const val POOL_IDLE = "Pool: idle"

    /** Status-bar text describing the connection pool for the given state. */
    fun poolStatusText(connected: Boolean): String = if (connected) POOL_ACTIVE else POOL_IDLE

    /** A blank filter means "match everything"; otherwise the raw pattern is used. */
    fun effectiveScanPattern(input: String): String = input.ifBlank { SCAN_ALL_PATTERN }
}
