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
