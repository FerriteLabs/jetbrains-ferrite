package dev.ferrite.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import dev.ferrite.jetbrains.service.FerriteConnectionManager

class RefreshKeysAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val connectionManager = FerriteConnectionManager.getInstance(project)

        if (!connectionManager.isConnected()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Ferrite")
                .createNotification("Not connected to Ferrite", NotificationType.WARNING)
                .notify(project)
            return
        }

        // Activate the Ferrite tool window to trigger refresh
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Ferrite")
        toolWindow?.activate(null)

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Ferrite")
            .createNotification("Keys refreshed", NotificationType.INFORMATION)
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
