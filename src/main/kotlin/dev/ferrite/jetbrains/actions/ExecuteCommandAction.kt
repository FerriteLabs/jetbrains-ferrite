package dev.ferrite.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.ferrite.jetbrains.service.FerriteConnectionManager

class ExecuteCommandAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val connectionManager = FerriteConnectionManager.getInstance(project)

        if (!connectionManager.isConnected()) {
            notify(project, "Not connected to Ferrite", NotificationType.WARNING)
            return
        }

        val document = editor.document
        val caretModel = editor.caretModel
        val lineNumber = caretModel.logicalPosition.line
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val command = document.getText(com.intellij.openapi.util.TextRange(lineStart, lineEnd)).trim()

        if (command.isBlank() || command.startsWith("#")) return

        val result = connectionManager.executeCommand(command)
        notify(project, result, NotificationType.INFORMATION)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
    }

    private fun notify(project: com.intellij.openapi.project.Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Ferrite")
            .createNotification(content, type)
            .notify(project)
    }
}
