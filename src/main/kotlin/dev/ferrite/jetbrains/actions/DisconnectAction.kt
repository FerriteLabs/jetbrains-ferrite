package dev.ferrite.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import dev.ferrite.jetbrains.service.FerriteConnectionManager

class DisconnectAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val connectionManager = FerriteConnectionManager.getInstance(project)
        val name = connectionManager.getCurrentConnectionName() ?: "Ferrite"

        connectionManager.disconnect()

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Ferrite")
            .createNotification("Disconnected from $name", NotificationType.INFORMATION)
            .notify(project)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = FerriteConnectionManager.getInstance(project).isConnected()
    }
}
