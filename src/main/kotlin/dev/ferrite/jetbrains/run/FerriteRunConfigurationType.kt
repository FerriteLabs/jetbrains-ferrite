package dev.ferrite.jetbrains.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import dev.ferrite.jetbrains.language.FerriteIcons
import org.jdom.Element
import javax.swing.Icon

class FerriteRunConfigurationType : ConfigurationType {

    override fun getDisplayName(): String = "FerriteQL"

    override fun getConfigurationTypeDescription(): String = "Execute FerriteQL script file"

    override fun getIcon(): Icon = FerriteIcons.FERRITE

    override fun getId(): String = "FerriteRunConfiguration"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> =
        arrayOf(FerriteRunConfigurationFactory(this))
}

class FerriteRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = "FerriteRunConfigurationFactory"

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        FerriteRunConfiguration(project, this, "FerriteQL")

}

class FerriteRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfigurationOptions>(project, factory, name) {

    var scriptPath: String = ""
    var connectionName: String = ""

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        FerriteRunSettingsEditor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? = null

    override fun readExternal(element: Element) {
        super.readExternal(element)
        scriptPath = JDOMExternalizerUtil.readField(element, "scriptPath") ?: ""
        connectionName = JDOMExternalizerUtil.readField(element, "connectionName") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, "scriptPath", scriptPath)
        JDOMExternalizerUtil.writeField(element, "connectionName", connectionName)
    }
}

class FerriteRunSettingsEditor : SettingsEditor<FerriteRunConfiguration>() {

    private val scriptPathField = com.intellij.ui.components.JBTextField()
    private val connectionField = com.intellij.ui.components.JBTextField()

    override fun createEditor(): javax.swing.JComponent {
        val panel = javax.swing.JPanel(java.awt.GridBagLayout())
        val gbc = java.awt.GridBagConstraints().apply {
            insets = java.awt.Insets(5, 5, 5, 5)
            fill = java.awt.GridBagConstraints.HORIZONTAL
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0
        panel.add(com.intellij.ui.components.JBLabel("Script path:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        panel.add(scriptPathField, gbc)

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0
        panel.add(com.intellij.ui.components.JBLabel("Connection:"), gbc)
        gbc.gridx = 1; gbc.weightx = 1.0
        panel.add(connectionField, gbc)

        return panel
    }

    override fun resetEditorFrom(config: FerriteRunConfiguration) {
        scriptPathField.text = config.scriptPath
        connectionField.text = config.connectionName
    }

    override fun applyEditorTo(config: FerriteRunConfiguration) {
        config.scriptPath = scriptPathField.text
        config.connectionName = connectionField.text
    }
}
