package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.toolwindow.FerriteToolWindowState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [FerriteToolWindowState], the pure view-state decisions of the
 * Ferrite tool window. Pure Kotlin, no IntelliJ platform dependencies.
 */
class FerriteToolWindowStateTest {

    @Test
    fun `pool status text reflects a live connection`() {
        assertEquals("Pool: active (1 conn)", FerriteToolWindowState.poolStatusText(true))
    }

    @Test
    fun `pool status text reflects an idle pool`() {
        assertEquals("Pool: idle", FerriteToolWindowState.poolStatusText(false))
    }

    @Test
    fun `a blank filter pattern matches everything`() {
        assertEquals("*", FerriteToolWindowState.effectiveScanPattern(""))
        assertEquals("*", FerriteToolWindowState.effectiveScanPattern("   "))
    }

    @Test
    fun `a non-blank filter pattern is used verbatim`() {
        assertEquals("user:*", FerriteToolWindowState.effectiveScanPattern("user:*"))
    }
}
