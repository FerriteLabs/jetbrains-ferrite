package dev.ferrite.jetbrains.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object FerriteQLFileType : LanguageFileType(FerriteQLLanguage.INSTANCE) {
    override fun getName(): String = "FerriteQL"
    override fun getDescription(): String = "Ferrite Query Language file"
    override fun getDefaultExtension(): String = "fql"
    override fun getIcon(): Icon = FerriteIcons.FILE
}
