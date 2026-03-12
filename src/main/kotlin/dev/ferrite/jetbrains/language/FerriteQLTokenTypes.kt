package dev.ferrite.jetbrains.language

import com.intellij.psi.tree.IElementType

object FerriteQLTokenTypes {
    val COMMAND = FerriteQLTokenType("COMMAND")
    val STRING = FerriteQLTokenType("STRING")
    val NUMBER = FerriteQLTokenType("NUMBER")
    val COMMENT = FerriteQLTokenType("COMMENT")
    val KEY = FerriteQLTokenType("KEY")
    val OPTION = FerriteQLTokenType("OPTION")
    val WHITESPACE = FerriteQLTokenType("WHITESPACE")
    val NEWLINE = FerriteQLTokenType("NEWLINE")
    val IDENTIFIER = FerriteQLTokenType("IDENTIFIER")
    val ACL_KEYWORD = FerriteQLTokenType("ACL_KEYWORD")
    val ACL_RULE = FerriteQLTokenType("ACL_RULE")
    val BAD_CHARACTER = FerriteQLTokenType("BAD_CHARACTER")
}

class FerriteQLTokenType(debugName: String) : IElementType(debugName, FerriteQLLanguage.INSTANCE) {
    override fun toString(): String = "FerriteQL:" + super.toString()
}

class FerriteQLElementType(debugName: String) : IElementType(debugName, FerriteQLLanguage.INSTANCE)
