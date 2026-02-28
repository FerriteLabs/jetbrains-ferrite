package dev.ferrite.jetbrains.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import dev.ferrite.jetbrains.service.FerriteConnectionManager
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class AddConnectionDialog(project: Project) : DialogWrapper(project) {

    private val nameField = JBTextField("Local")
    private val hostField = JBTextField("localhost")
    private val portSpinner = JSpinner(SpinnerNumberModel(6379, 1, 65535, 1))
    private val passwordField = JBPasswordField()
    private val databaseSpinner = JSpinner(SpinnerNumberModel(0, 0, 15, 1))
    private val tlsCheckbox = JBCheckBox("Use TLS")

    init {
        title = "New Ferrite Connection"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        // Name
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.0
        panel.add(JBLabel("Name:"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        panel.add(nameField, gbc)

        // Host
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.weightx = 0.0
        panel.add(JBLabel("Host:"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        panel.add(hostField, gbc)

        // Port
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.weightx = 0.0
        panel.add(JBLabel("Port:"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        panel.add(portSpinner, gbc)

        // Password
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.weightx = 0.0
        panel.add(JBLabel("Password:"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        panel.add(passwordField, gbc)

        // Database
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.weightx = 0.0
        panel.add(JBLabel("Database:"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        panel.add(databaseSpinner, gbc)

        // TLS
        gbc.gridx = 0
        gbc.gridy = 5
        gbc.gridwidth = 2
        panel.add(tlsCheckbox, gbc)

        return panel
    }

    fun getConnection(): FerriteConnectionManager.ConnectionConfig {
        return FerriteConnectionManager.ConnectionConfig(
            name = nameField.text,
            host = hostField.text,
            port = portSpinner.value as Int,
            password = String(passwordField.password),
            database = databaseSpinner.value as Int,
            useTls = tlsCheckbox.isSelected
        )
    }

    override fun doValidate(): com.intellij.openapi.ui.ValidationInfo? {
        if (nameField.text.isBlank()) {
            return com.intellij.openapi.ui.ValidationInfo("Name is required", nameField)
        }
        if (hostField.text.isBlank()) {
            return com.intellij.openapi.ui.ValidationInfo("Host is required", hostField)
        }
        return null
    }
}
