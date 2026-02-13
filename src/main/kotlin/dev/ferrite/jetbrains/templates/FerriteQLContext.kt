package dev.ferrite.jetbrains.templates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import dev.ferrite.jetbrains.language.FerriteQLFileType

@Suppress("DEPRECATION")
class FerriteQLContext : TemplateContextType("FerriteQL", "FerriteQL") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file.fileType == FerriteQLFileType
    }
}
