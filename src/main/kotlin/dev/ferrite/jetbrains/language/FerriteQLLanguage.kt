package dev.ferrite.jetbrains.language

import com.intellij.lang.Language

class FerriteQLLanguage private constructor() : Language("FerriteQL") {
    companion object {
        @JvmField
        val INSTANCE = FerriteQLLanguage()
    }

    override fun getDisplayName(): String = "FerriteQL"
    override fun isCaseSensitive(): Boolean = false
}
