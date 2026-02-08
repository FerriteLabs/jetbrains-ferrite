package dev.ferrite.jetbrains.language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiBuilder
import com.intellij.psi.impl.source.tree.LeafPsiElement

class FerriteQLParserDefinition : ParserDefinition {

    companion object {
        val FILE = IFileElementType(FerriteQLLanguage.INSTANCE)

        val COMMENTS = TokenSet.create(FerriteQLTokenTypes.COMMENT)
        val STRINGS = TokenSet.create(FerriteQLTokenTypes.STRING)
        val WHITE_SPACES = TokenSet.create(FerriteQLTokenTypes.WHITESPACE, FerriteQLTokenTypes.NEWLINE)
    }

    override fun createLexer(project: Project?): Lexer = FerriteQLLexer()

    override fun createParser(project: Project?): PsiParser = FerriteQLParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = STRINGS

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun createElement(node: ASTNode): PsiElement = LeafPsiElement(node.elementType, node.text)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = FerriteQLFile(viewProvider)
}

class FerriteQLParser : PsiParser {
    override fun parse(root: com.intellij.psi.tree.IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }
}

class FerriteQLFile(viewProvider: FileViewProvider) :
    com.intellij.extapi.psi.PsiFileBase(viewProvider, FerriteQLLanguage.INSTANCE) {

    override fun getFileType() = FerriteQLFileType
    override fun toString() = "FerriteQL File"
}
