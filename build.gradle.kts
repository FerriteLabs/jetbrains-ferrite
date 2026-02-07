plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "dev.ferrite"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.lettuce:lettuce-core:6.3.0.RELEASE")
    testImplementation("junit:junit:4.13.2")
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2023.3")
    type.set("IC") // IntelliJ IDEA Community Edition

    plugins.set(listOf(
        "com.intellij.java"
    ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("241.*")

        pluginDescription.set("""
            <h1>Ferrite for JetBrains IDEs</h1>
            <p>Official JetBrains IDE plugin for <a href="https://ferrite.dev">Ferrite</a> -
            a high-performance, tiered-storage key-value store.</p>

            <h2>Features</h2>
            <ul>
                <li><strong>FerriteQL Support</strong> - Syntax highlighting, completion, and execution</li>
                <li><strong>Database Tool Integration</strong> - Browse and manage Ferrite data</li>
                <li><strong>Configuration Support</strong> - Syntax highlighting for ferrite.toml</li>
                <li><strong>Live Templates</strong> - Code snippets for common operations</li>
            </ul>
        """.trimIndent())

        changeNotes.set("""
            <h2>1.0.0</h2>
            <ul>
                <li>Initial release</li>
                <li>FerriteQL syntax highlighting</li>
                <li>Configuration file support</li>
                <li>Database tool integration</li>
                <li>Live templates for all languages</li>
            </ul>
        """.trimIndent())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
