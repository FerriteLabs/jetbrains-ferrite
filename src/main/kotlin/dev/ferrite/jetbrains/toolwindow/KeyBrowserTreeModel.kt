package dev.ferrite.jetbrains.toolwindow

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * Tree model for the key browser that supports lazy loading of child nodes.
 *
 * Keys are split by their delimiter (default `:`) into a hierarchical tree.
 * Child nodes are only fetched from the server when the user expands a branch,
 * keeping memory usage low for databases with millions of keys.
 */
class KeyBrowserTreeModel : DefaultTreeModel(DefaultMutableTreeNode("Keys")) {

    companion object {
        const val DEFAULT_DELIMITER = ":"
        const val LAZY_LOAD_BATCH_SIZE = 200
        const val PLACEHOLDER_TEXT = "Loading..."
    }

    private var delimiter: String = DEFAULT_DELIMITER

    fun setDelimiter(delimiter: String) {
        this.delimiter = delimiter
    }

    /**
     * Build top-level nodes from a list of key prefixes.
     * Each prefix becomes a branch that can be lazily expanded.
     */
    fun loadPrefixes(prefixes: List<String>) {
        val root = this.root as DefaultMutableTreeNode
        root.removeAllChildren()

        for (prefix in prefixes.sorted()) {
            val node = DefaultMutableTreeNode(prefix)
            // Add a placeholder child so the node appears expandable
            node.add(DefaultMutableTreeNode(PLACEHOLDER_TEXT))
            root.add(node)
        }

        reload()
    }

    /**
     * Replace the placeholder children of [parentNode] with actual key entries.
     */
    fun loadChildren(parentNode: DefaultMutableTreeNode, keys: List<String>) {
        parentNode.removeAllChildren()

        for (key in keys.sorted()) {
            val segments = key.split(delimiter)
            val label = segments.lastOrNull() ?: key
            parentNode.add(DefaultMutableTreeNode(label))
        }

        reload(parentNode)
    }
}
