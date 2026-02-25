package dev.ferrite.jetbrains.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "dev.ferrite.settings",
    storages = [Storage("FerriteSettings.xml")]
)
class FerriteSettings : PersistentStateComponent<FerriteSettings.State> {

    /**
     * Persistent state for Ferrite plugin connection settings.
     *
     * @property defaultHost The hostname or IP address of the Ferrite server. Defaults to "localhost".
     * @property defaultPort The port number the Ferrite server listens on. Defaults to 6379 (Redis-compatible).
     * @property outputFormat Display format for command output: "raw", "JSON", or "table".
     * @property maxKeysToDisplay Upper limit on the number of keys shown in the key browser tree.
     * @property autoConnectOnOpen When true, the plugin automatically connects on project open.
     * @property connectionTimeoutMs Timeout in milliseconds for establishing a connection.
     */
    data class State(
        var defaultHost: String = "localhost",
        var defaultPort: Int = 6379,
        var outputFormat: String = "raw",
        var maxKeysToDisplay: Int = 1000,
        var autoConnectOnOpen: Boolean = false,
        var connectionTimeoutMs: Int = 5000
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var defaultHost: String
        get() = state.defaultHost
        set(value) { state.defaultHost = value }

    var defaultPort: Int
        get() = state.defaultPort
        set(value) { state.defaultPort = value }

    var outputFormat: String
        get() = state.outputFormat
        set(value) { state.outputFormat = value }

    var maxKeysToDisplay: Int
        get() = state.maxKeysToDisplay
        set(value) { state.maxKeysToDisplay = value }

    var autoConnectOnOpen: Boolean
        get() = state.autoConnectOnOpen
        set(value) { state.autoConnectOnOpen = value }

    var connectionTimeoutMs: Int
        get() = state.connectionTimeoutMs
        set(value) { state.connectionTimeoutMs = value }

    companion object {
        fun getInstance(): FerriteSettings =
            ApplicationManager.getApplication().getService(FerriteSettings::class.java)
    }
}
