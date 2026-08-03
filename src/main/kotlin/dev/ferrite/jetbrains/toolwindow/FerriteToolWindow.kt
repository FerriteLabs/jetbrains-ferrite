package dev.ferrite.jetbrains.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import dev.ferrite.jetbrains.language.FerriteIcons
import dev.ferrite.jetbrains.service.FerriteConnectionManager
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

class FerriteToolWindow(private val project: Project) {
    private val panel = SimpleToolWindowPanel(true, true)
    private val connectionManager = FerriteConnectionManager.getInstance(project)

    private val connectionsList = JBList<ConnectionItem>()
    private val keysList = JBList<KeyItem>()
    private val serverInfoArea = JTextArea()
    private val poolStatusLabel = JLabel("Pool: idle")

    init {
        setupUI()
    }

    private fun setupUI() {
        val tabbedPane = JBTabbedPane()

        // Connections tab
        val connectionsPanel = JPanel(BorderLayout())
        connectionsList.cellRenderer = ConnectionCellRenderer()
        connectionsPanel.add(JBScrollPane(connectionsList), BorderLayout.CENTER)
        connectionsPanel.add(createConnectionToolbar(), BorderLayout.NORTH)
        connectionsPanel.add(poolStatusLabel, BorderLayout.SOUTH)
        tabbedPane.addTab("Connections", connectionsPanel)

        // Keys tab
        val keysPanel = JPanel(BorderLayout())
        keysList.cellRenderer = KeyCellRenderer()
        keysPanel.add(JBScrollPane(keysList), BorderLayout.CENTER)
        keysPanel.add(createKeysToolbar(), BorderLayout.NORTH)
        tabbedPane.addTab("Keys", keysPanel)

        // Server Info tab
        val infoPanel = JPanel(BorderLayout())
        serverInfoArea.isEditable = false
        serverInfoArea.font = java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12)
        infoPanel.add(JBScrollPane(serverInfoArea), BorderLayout.CENTER)
        infoPanel.add(createInfoToolbar(), BorderLayout.NORTH)
        tabbedPane.addTab("Server Info", infoPanel)

        panel.setContent(tabbedPane)
    }

    private fun createConnectionToolbar(): JPanel {
        val toolbar = JPanel()
        toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)

        val addButton = JButton("Add")
        addButton.addActionListener { showAddConnectionDialog() }
        toolbar.add(addButton)

        val connectButton = JButton("Connect")
        connectButton.addActionListener { connectToSelected() }
        toolbar.add(connectButton)

        val disconnectButton = JButton("Disconnect")
        disconnectButton.addActionListener { disconnectFromSelected() }
        toolbar.add(disconnectButton)

        val removeButton = JButton("Remove")
        removeButton.addActionListener { removeSelected() }
        toolbar.add(removeButton)

        return toolbar
    }

    private fun createKeysToolbar(): JPanel {
        val toolbar = JPanel()
        toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)

        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener { refreshKeys() }
        toolbar.add(refreshButton)

        val filterField = JTextField(20)
        filterField.toolTipText = "Filter pattern (e.g., user:*)"
        toolbar.add(filterField)

        val filterButton = JButton("Filter")
        filterButton.addActionListener { filterKeys(filterField.text) }
        toolbar.add(filterButton)

        return toolbar
    }

    private fun createInfoToolbar(): JPanel {
        val toolbar = JPanel()
        toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)

        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener { refreshServerInfo() }
        toolbar.add(refreshButton)

        return toolbar
    }

    private fun showAddConnectionDialog() {
        val dialog = AddConnectionDialog(project)
        if (dialog.showAndGet()) {
            val connection = dialog.getConnection()
            connectionManager.addConnection(connection)
            updateConnectionsList()
        }
    }

    private fun connectToSelected() {
        val selected = connectionsList.selectedValue ?: return
        connectionManager.connect(selected.name)
        updatePoolStatus()
        updateConnectionsList()
        refreshKeys()
        refreshServerInfo()
    }

    private fun disconnectFromSelected() {
        connectionManager.disconnect()
        updateConnectionsList()
        keysList.model = DefaultListModel()
        serverInfoArea.text = ""
        updatePoolStatus()
    }

    private fun removeSelected() {
        val selected = connectionsList.selectedValue ?: return
        connectionManager.removeConnection(selected.name)
        updateConnectionsList()
    }

    private fun refreshKeys() {
        val keys = connectionManager.scanKeys("*", 1000)
        val model = DefaultListModel<KeyItem>()
        keys.forEach { key ->
            val type = connectionManager.getKeyType(key)
            model.addElement(KeyItem(key, type))
        }
        keysList.model = model
    }

    private fun filterKeys(pattern: String) {
        val effectivePattern = pattern.ifBlank { "*" }
        val keys = connectionManager.scanKeys(effectivePattern, 1000)
        val model = DefaultListModel<KeyItem>()
        keys.forEach { key ->
            val type = connectionManager.getKeyType(key)
            model.addElement(KeyItem(key, type))
        }
        keysList.model = model
    }

    private fun refreshServerInfo() {
        val info = connectionManager.getServerInfo()
        serverInfoArea.text = info
    }

    private fun updateConnectionsList() {
        val model = DefaultListModel<ConnectionItem>()
        connectionManager.getConnections().forEach { conn ->
            model.addElement(
                ConnectionItem(
                    conn.name,
                    conn.host,
                    conn.port,
                    connectionManager.isConnected() && connectionManager.getCurrentConnectionName() == conn.name
                )
            )
        }
        connectionsList.model = model
    }

    private fun updatePoolStatus() {
        val status = if (connectionManager.isConnected()) "Pool: active (1 conn)" else "Pool: idle"
        poolStatusLabel.text = status
    }
    fun getContent(): JComponent = panel

    data class ConnectionItem(
        val name: String,
        val host: String,
        val port: Int,
        val connected: Boolean
    )

    data class KeyItem(
        val key: String,
        val type: String
    )

    class ConnectionCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is ConnectionItem) {
                text = "${value.name} (${value.host}:${value.port})"
                icon = if (value.connected) FerriteIcons.CONNECTED else FerriteIcons.DISCONNECTED
            }
            return this
        }
    }

    class KeyCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is KeyItem) {
                text = "${value.key} [${value.type}]"
                icon = when (value.type.lowercase()) {
                    "string" -> FerriteIcons.STRING
                    "hash" -> FerriteIcons.HASH
                    "list" -> FerriteIcons.LIST
                    "set" -> FerriteIcons.SET
                    "zset" -> FerriteIcons.ZSET
                    "stream" -> FerriteIcons.STREAM
                    else -> FerriteIcons.KEY
                }
            }
            return this
        }
    }
}
