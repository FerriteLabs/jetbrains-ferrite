package dev.ferrite.jetbrains.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class FerriteSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private val hostField = JBTextField()
    private val portSpinner = JSpinner(SpinnerNumberModel(6379, 1, 65535, 1))
    private val outputFormatCombo = JComboBox(arrayOf("raw", "JSON", "table"))
    private val maxKeysSpinner = JSpinner(SpinnerNumberModel(1000, 1, 100000, 100))
    private val autoConnectCheckbox = JBCheckBox("Auto-connect on project open")

    override fun getDisplayName(): String = "Ferrite"

    override fun createComponent(): JComponent {
        val p = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0
        p.add(JBLabel("Default host:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        p.add(hostField, gbc)

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0
        p.add(JBLabel("Default port:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        p.add(portSpinner, gbc)

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0
        p.add(JBLabel("Output format:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        p.add(outputFormatCombo, gbc)

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0
        p.add(JBLabel("Max keys to display:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        p.add(maxKeysSpinner, gbc)

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2
        p.add(autoConnectCheckbox, gbc)

        // Filler
        gbc.gridx = 0; gbc.gridy = 5; gbc.weighty = 1.0; gbc.gridwidth = 2
        p.add(JPanel(), gbc)

        panel = p
        reset()
        return p
    }

    override fun isModified(): Boolean {
        val settings = FerriteSettings.getInstance()
        return hostField.text != settings.defaultHost ||
            portSpinner.value as Int != settings.defaultPort ||
            outputFormatCombo.selectedItem as String != settings.outputFormat ||
            maxKeysSpinner.value as Int != settings.maxKeysToDisplay ||
            autoConnectCheckbox.isSelected != settings.autoConnectOnOpen
    }

    override fun apply() {
        val settings = FerriteSettings.getInstance()
        settings.defaultHost = hostField.text
        settings.defaultPort = portSpinner.value as Int
        settings.outputFormat = outputFormatCombo.selectedItem as String
        settings.maxKeysToDisplay = maxKeysSpinner.value as Int
        settings.autoConnectOnOpen = autoConnectCheckbox.isSelected
    }

    override fun reset() {
        val settings = FerriteSettings.getInstance()
        hostField.text = settings.defaultHost
        portSpinner.value = settings.defaultPort
        outputFormatCombo.selectedItem = settings.outputFormat
        maxKeysSpinner.value = settings.maxKeysToDisplay
        autoConnectCheckbox.isSelected = settings.autoConnectOnOpen
    }

    override fun disposeUIResources() {
        panel = null
    }
}
