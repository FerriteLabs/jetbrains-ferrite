package dev.ferrite.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.ferrite.jetbrains.service.FerriteConnectionManager

class ExecuteSelectionAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val connectionManager = FerriteConnectionManager.getInstance(project)

        if (!connectionManager.isConnected()) {
            notify(project, "Not connected to Ferrite", NotificationType.WARNING)
            return
        }

        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrBlank()) {
            notify(project, "No text selected", NotificationType.WARNING)
            return
        }

        val results = StringBuilder()
        selectedText.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .forEach { command ->
                val result = connectionManager.executeCommand(command)
                results.appendLine("> $command")
                results.appendLine(result)
            }

        if (results.isNotEmpty()) {
            notify(project, results.toString(), NotificationType.INFORMATION)
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }

    private fun notify(project: com.intellij.openapi.project.Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Ferrite")
            .createNotification(content, type)
            .notify(project)
    }
}
