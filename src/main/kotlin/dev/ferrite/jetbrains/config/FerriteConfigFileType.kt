package dev.ferrite.jetbrains.config

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import dev.ferrite.jetbrains.language.FerriteIcons
import javax.swing.Icon

object FerriteConfigFileType : FileType {

    override fun getName(): @NlsSafe String = "Ferrite Config"

    override fun getDescription(): @NlsContexts.Label String = "Ferrite configuration file"

    override fun getDefaultExtension(): @NlsSafe String = "toml"

    override fun getIcon(): Icon = FerriteIcons.FERRITE

    override fun isBinary(): Boolean = false
}
