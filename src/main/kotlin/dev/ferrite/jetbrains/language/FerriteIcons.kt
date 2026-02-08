package dev.ferrite.jetbrains.language

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object FerriteIcons {
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/ferrite-file.svg", FerriteIcons::class.java)

    @JvmField
    val FERRITE: Icon = IconLoader.getIcon("/icons/ferrite.svg", FerriteIcons::class.java)

    @JvmField
    val TOOL_WINDOW: Icon = IconLoader.getIcon("/icons/ferrite-tool.svg", FerriteIcons::class.java)

    @JvmField
    val KEY: Icon = IconLoader.getIcon("/icons/key.svg", FerriteIcons::class.java)

    @JvmField
    val STRING: Icon = IconLoader.getIcon("/icons/string.svg", FerriteIcons::class.java)

    @JvmField
    val HASH: Icon = IconLoader.getIcon("/icons/hash.svg", FerriteIcons::class.java)

    @JvmField
    val LIST: Icon = IconLoader.getIcon("/icons/list.svg", FerriteIcons::class.java)

    @JvmField
    val SET: Icon = IconLoader.getIcon("/icons/set.svg", FerriteIcons::class.java)

    @JvmField
    val ZSET: Icon = IconLoader.getIcon("/icons/zset.svg", FerriteIcons::class.java)

    @JvmField
    val STREAM: Icon = IconLoader.getIcon("/icons/stream.svg", FerriteIcons::class.java)

    @JvmField
    val VECTOR: Icon = IconLoader.getIcon("/icons/vector.svg", FerriteIcons::class.java)

    @JvmField
    val CONNECTED: Icon = IconLoader.getIcon("/icons/connected.svg", FerriteIcons::class.java)

    @JvmField
    val DISCONNECTED: Icon = IconLoader.getIcon("/icons/disconnected.svg", FerriteIcons::class.java)
}
