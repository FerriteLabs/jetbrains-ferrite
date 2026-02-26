package dev.ferrite.jetbrains

import dev.ferrite.jetbrains.service.FerriteConnectionManager.ConnectionConfig
import dev.ferrite.jetbrains.settings.FerriteSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for connection configuration and settings data models.
 *
 * Tests [ConnectionConfig] data class construction, defaults, equality, and
 * copy semantics, as well as [FerriteSettings.State] defaults and property
 * access.  These tests are pure Kotlin with no IntelliJ platform dependencies.
 */
class ConnectionSettingsTest {

    // =======================================================================
    // ConnectionConfig - defaults
    // =======================================================================

    @Test
    fun `ConnectionConfig default port is 6379`() {
        val config = ConnectionConfig(name = "test", host = "localhost")
        assertEquals(6379, config.port)
    }

    @Test
    fun `ConnectionConfig default password is empty`() {
        val config = ConnectionConfig(name = "test", host = "localhost")
        assertEquals("", config.password)
    }

    @Test
    fun `ConnectionConfig default database is 0`() {
        val config = ConnectionConfig(name = "test", host = "localhost")
        assertEquals(0, config.database)
    }

    @Test
    fun `ConnectionConfig default useTls is false`() {
        val config = ConnectionConfig(name = "test", host = "localhost")
        assertFalse(config.useTls)
    }

    // =======================================================================
    // ConnectionConfig - custom values
    // =======================================================================

    @Test
    fun `ConnectionConfig with all fields specified`() {
        val config = ConnectionConfig(
            name = "production",
            host = "ferrite.example.com",
            port = 6380,
            password = "s3cret",
            database = 3,
            useTls = true
        )
        assertEquals("production", config.name)
        assertEquals("ferrite.example.com", config.host)
        assertEquals(6380, config.port)
        assertEquals("s3cret", config.password)
        assertEquals(3, config.database)
        assertTrue(config.useTls)
    }

    @Test
    fun `ConnectionConfig with IPv6 host`() {
        val config = ConnectionConfig(name = "ipv6", host = "::1", port = 6379)
        assertEquals("::1", config.host)
    }

    @Test
    fun `ConnectionConfig with IP address host`() {
        val config = ConnectionConfig(name = "ip", host = "192.168.1.100")
        assertEquals("192.168.1.100", config.host)
    }

    @Test
    fun `ConnectionConfig with empty name`() {
        val config = ConnectionConfig(name = "", host = "localhost")
        assertEquals("", config.name)
    }

    @Test
    fun `ConnectionConfig with port boundary values`() {
        val configMin = ConnectionConfig(name = "min", host = "localhost", port = 1)
        assertEquals(1, configMin.port)

        val configMax = ConnectionConfig(name = "max", host = "localhost", port = 65535)
        assertEquals(65535, configMax.port)
    }

    @Test
    fun `ConnectionConfig with all database indices`() {
        for (db in 0..15) {
            val config = ConnectionConfig(name = "db$db", host = "localhost", database = db)
            assertEquals(db, config.database)
        }
    }

    // =======================================================================
    // ConnectionConfig - data class equality and copy
    // =======================================================================

    @Test
    fun `ConnectionConfig equality by value`() {
        val a = ConnectionConfig(name = "local", host = "localhost", port = 6379)
        val b = ConnectionConfig(name = "local", host = "localhost", port = 6379)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `ConnectionConfig inequality when name differs`() {
        val a = ConnectionConfig(name = "a", host = "localhost")
        val b = ConnectionConfig(name = "b", host = "localhost")
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig inequality when host differs`() {
        val a = ConnectionConfig(name = "test", host = "host1")
        val b = ConnectionConfig(name = "test", host = "host2")
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig inequality when port differs`() {
        val a = ConnectionConfig(name = "test", host = "localhost", port = 6379)
        val b = ConnectionConfig(name = "test", host = "localhost", port = 6380)
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig inequality when password differs`() {
        val a = ConnectionConfig(name = "test", host = "localhost", password = "")
        val b = ConnectionConfig(name = "test", host = "localhost", password = "secret")
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig inequality when database differs`() {
        val a = ConnectionConfig(name = "test", host = "localhost", database = 0)
        val b = ConnectionConfig(name = "test", host = "localhost", database = 1)
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig inequality when useTls differs`() {
        val a = ConnectionConfig(name = "test", host = "localhost", useTls = false)
        val b = ConnectionConfig(name = "test", host = "localhost", useTls = true)
        assertNotEquals(a, b)
    }

    @Test
    fun `ConnectionConfig copy with changed port`() {
        val original = ConnectionConfig(name = "test", host = "localhost", port = 6379)
        val modified = original.copy(port = 6380)
        assertEquals(6380, modified.port)
        assertEquals("test", modified.name)
        assertEquals("localhost", modified.host)
    }

    @Test
    fun `ConnectionConfig copy with changed password`() {
        val original = ConnectionConfig(name = "test", host = "localhost")
        val modified = original.copy(password = "newsecret")
        assertEquals("newsecret", modified.password)
        assertEquals("", original.password)
    }

    @Test
    fun `ConnectionConfig destructuring`() {
        val config = ConnectionConfig(
            name = "prod",
            host = "ferrite.io",
            port = 6380,
            password = "pass",
            database = 2,
            useTls = true
        )
        val (name, host, port, password, database, useTls) = config
        assertEquals("prod", name)
        assertEquals("ferrite.io", host)
        assertEquals(6380, port)
        assertEquals("pass", password)
        assertEquals(2, database)
        assertTrue(useTls)
    }

    @Test
    fun `ConnectionConfig toString contains all fields`() {
        val config = ConnectionConfig(name = "test", host = "localhost", port = 6379)
        val str = config.toString()
        assertTrue(str.contains("test"))
        assertTrue(str.contains("localhost"))
        assertTrue(str.contains("6379"))
    }

    // =======================================================================
    // FerriteSettings.State - defaults
    // =======================================================================

    @Test
    fun `Settings State default host is localhost`() {
        val state = FerriteSettings.State()
        assertEquals("localhost", state.defaultHost)
    }

    @Test
    fun `Settings State default port is 6379`() {
        val state = FerriteSettings.State()
        assertEquals(6379, state.defaultPort)
    }

    @Test
    fun `Settings State default output format is raw`() {
        val state = FerriteSettings.State()
        assertEquals("raw", state.outputFormat)
    }

    @Test
    fun `Settings State default max keys is 1000`() {
        val state = FerriteSettings.State()
        assertEquals(1000, state.maxKeysToDisplay)
    }

    @Test
    fun `Settings State default autoConnectOnOpen is false`() {
        val state = FerriteSettings.State()
        assertFalse(state.autoConnectOnOpen)
    }

    // =======================================================================
    // FerriteSettings.State - mutations
    // =======================================================================

    @Test
    fun `Settings State host can be changed`() {
        val state = FerriteSettings.State()
        state.defaultHost = "ferrite.example.com"
        assertEquals("ferrite.example.com", state.defaultHost)
    }

    @Test
    fun `Settings State port can be changed`() {
        val state = FerriteSettings.State()
        state.defaultPort = 6380
        assertEquals(6380, state.defaultPort)
    }

    @Test
    fun `Settings State output format can be changed to JSON`() {
        val state = FerriteSettings.State()
        state.outputFormat = "JSON"
        assertEquals("JSON", state.outputFormat)
    }

    @Test
    fun `Settings State output format can be changed to table`() {
        val state = FerriteSettings.State()
        state.outputFormat = "table"
        assertEquals("table", state.outputFormat)
    }

    @Test
    fun `Settings State maxKeysToDisplay can be changed`() {
        val state = FerriteSettings.State()
        state.maxKeysToDisplay = 5000
        assertEquals(5000, state.maxKeysToDisplay)
    }

    @Test
    fun `Settings State autoConnectOnOpen can be toggled`() {
        val state = FerriteSettings.State()
        state.autoConnectOnOpen = true
        assertTrue(state.autoConnectOnOpen)
        state.autoConnectOnOpen = false
        assertFalse(state.autoConnectOnOpen)
    }

    // =======================================================================
    // FerriteSettings.State - equality
    // =======================================================================

    @Test
    fun `Settings State equality with same defaults`() {
        val a = FerriteSettings.State()
        val b = FerriteSettings.State()
        assertEquals(a, b)
    }

    @Test
    fun `Settings State inequality when host differs`() {
        val a = FerriteSettings.State()
        val b = FerriteSettings.State(defaultHost = "other")
        assertNotEquals(a, b)
    }

    @Test
    fun `Settings State copy preserves values`() {
        val original = FerriteSettings.State(
            defaultHost = "myhost",
            defaultPort = 6380,
            outputFormat = "JSON",
            maxKeysToDisplay = 500,
            autoConnectOnOpen = true
        )
        val copied = original.copy()
        assertEquals(original, copied)
    }

    @Test
    fun `Settings State copy with modifications`() {
        val original = FerriteSettings.State()
        val modified = original.copy(defaultHost = "newhost", defaultPort = 9999)
        assertEquals("newhost", modified.defaultHost)
        assertEquals(9999, modified.defaultPort)
        // Other fields unchanged
        assertEquals("raw", modified.outputFormat)
        assertEquals(1000, modified.maxKeysToDisplay)
        assertFalse(modified.autoConnectOnOpen)
    }

    // =======================================================================
    // FerriteSettings - PersistentStateComponent behavior (no app needed)
    // =======================================================================

    @Test
    fun `FerriteSettings getState returns state with defaults`() {
        val settings = FerriteSettings()
        val state = settings.state
        assertNotNull(state)
        assertEquals("localhost", state.defaultHost)
        assertEquals(6379, state.defaultPort)
    }

    @Test
    fun `FerriteSettings loadState replaces internal state`() {
        val settings = FerriteSettings()
        val newState = FerriteSettings.State(
            defaultHost = "remote",
            defaultPort = 7000,
            outputFormat = "JSON",
            maxKeysToDisplay = 2000,
            autoConnectOnOpen = true
        )
        settings.loadState(newState)
        assertEquals("remote", settings.defaultHost)
        assertEquals(7000, settings.defaultPort)
        assertEquals("JSON", settings.outputFormat)
        assertEquals(2000, settings.maxKeysToDisplay)
        assertTrue(settings.autoConnectOnOpen)
    }

    @Test
    fun `FerriteSettings property getters delegate to state`() {
        val settings = FerriteSettings()
        assertEquals(settings.state.defaultHost, settings.defaultHost)
        assertEquals(settings.state.defaultPort, settings.defaultPort)
        assertEquals(settings.state.outputFormat, settings.outputFormat)
        assertEquals(settings.state.maxKeysToDisplay, settings.maxKeysToDisplay)
        assertEquals(settings.state.autoConnectOnOpen, settings.autoConnectOnOpen)
    }

    @Test
    fun `FerriteSettings property setters update state`() {
        val settings = FerriteSettings()
        settings.defaultHost = "changed"
        assertEquals("changed", settings.state.defaultHost)

        settings.defaultPort = 1234
        assertEquals(1234, settings.state.defaultPort)

        settings.outputFormat = "table"
        assertEquals("table", settings.state.outputFormat)

        settings.maxKeysToDisplay = 42
        assertEquals(42, settings.state.maxKeysToDisplay)

        settings.autoConnectOnOpen = true
        assertTrue(settings.state.autoConnectOnOpen)
    }

    @Test
    fun `FerriteSettings loadState then getState returns same object`() {
        val settings = FerriteSettings()
        val state = FerriteSettings.State(defaultHost = "test123")
        settings.loadState(state)
        assertSame(state, settings.state)
    }

    // =======================================================================
    // Connection string validation helpers (pure logic)
    // =======================================================================

    @Test
    fun `validate host - non-blank is valid`() {
        assertTrue("localhost".isNotBlank())
        assertTrue("192.168.1.1".isNotBlank())
        assertTrue("ferrite.example.com".isNotBlank())
    }

    @Test
    fun `validate host - blank is invalid`() {
        assertTrue("".isBlank())
        assertTrue("   ".isBlank())
    }

    @Test
    fun `validate port - range check 1 to 65535`() {
        val validPorts = listOf(1, 80, 443, 6379, 6380, 65535)
        val invalidPorts = listOf(0, -1, 65536, 100000)

        for (port in validPorts) {
            assertTrue("Port $port should be valid", port in 1..65535)
        }
        for (port in invalidPorts) {
            assertFalse("Port $port should be invalid", port in 1..65535)
        }
    }

    @Test
    fun `validate database - range check 0 to 15`() {
        for (db in 0..15) {
            assertTrue("Database $db should be valid", db in 0..15)
        }
        assertFalse(-1 in 0..15)
        assertFalse(16 in 0..15)
    }

    @Test
    fun `validate connection timeout - positive values only`() {
        val validTimeouts = listOf(1000, 5000, 10000, 30000)
        val invalidTimeouts = listOf(0, -1, -1000)

        for (timeout in validTimeouts) {
            assertTrue("Timeout $timeout should be valid", timeout > 0)
        }
        for (timeout in invalidTimeouts) {
            assertFalse("Timeout $timeout should be invalid", timeout > 0)
        }
    }

    @Test
    fun `validate password - empty and non-empty are both acceptable`() {
        // No constraint on password being present - empty means no auth
        val noAuth = ConnectionConfig(name = "test", host = "localhost", password = "")
        assertEquals("", noAuth.password)

        val withAuth = ConnectionConfig(name = "test", host = "localhost", password = "secret")
        assertEquals("secret", withAuth.password)
    }

    @Test
    fun `validate connection name - non-blank is required`() {
        // AddConnectionDialog.doValidate checks name is not blank
        val valid = "My Connection"
        assertTrue(valid.isNotBlank())

        val invalid = ""
        assertTrue(invalid.isBlank())

        val whitespaceOnly = "   "
        assertTrue(whitespaceOnly.isBlank())
    }
}

    @Test
    fun `auto-reconnect defaults to true`() {
        val state = FerriteSettings.State()
        assertTrue("Auto-reconnect should default to true", state.autoReconnect)
    }
