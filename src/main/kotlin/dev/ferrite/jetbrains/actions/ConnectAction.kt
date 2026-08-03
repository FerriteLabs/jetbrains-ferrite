package dev.ferrite.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import dev.ferrite.jetbrains.service.FerriteConnectionManager
import dev.ferrite.jetbrains.toolwindow.AddConnectionDialog

class ConnectAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val connectionManager = FerriteConnectionManager.getInstance(project)

        val connections = connectionManager.getConnections()
        if (connections.isEmpty()) {
            val dialog = AddConnectionDialog(project)
            if (dialog.showAndGet()) {
                val config = dialog.getConnection()
                connectionManager.addConnection(config)
                connectTo(project, connectionManager, config.name)
            }
            return
        }

        // Connect to the first available connection
        val target = connections.first()
        connectTo(project, connectionManager, target.name)
    }

    @Suppress("TooGenericExceptionCaught") // any connect failure is surfaced as an error notification
    private fun connectTo(
        project: com.intellij.openapi.project.Project,
        connectionManager: FerriteConnectionManager,
        name: String
    ) {
        try {
            connectionManager.connect(name)
            notify(project, "Connected to $name", NotificationType.INFORMATION)
            ToolWindowManager.getInstance(project).getToolWindow("Ferrite")?.activate(null)
        } catch (ex: Exception) {
            notify(project, "Connection failed: ${ex.message}", NotificationType.ERROR)
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = !FerriteConnectionManager.getInstance(project).isConnected()
    }

    private fun notify(project: com.intellij.openapi.project.Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Ferrite")
            .createNotification(content, type)
            .notify(project)
    }
}
