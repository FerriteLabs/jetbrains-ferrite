plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.intellij.platform") version "2.15.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "dev.ferrite"
version = "1.3.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("io.lettuce:lettuce-core:7.5.1.RELEASE")
    testImplementation("junit:junit:4.13.2")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")

    intellijPlatform {
        intellijIdeaCommunity("2024.3.1")
        bundledPlugin("com.intellij.java")
        instrumentationTools()
    }
}

detekt {
    config.setFrom("$projectDir/detekt.yml")
    buildUponDefaultConfig = true
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("251.*")

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
            <h2>1.1.0</h2>
            <ul>
                <li>Fix FerriteSettings syntax error</li>
                <li>Fix command dispatch crash on Ferrite-specific commands</li>
                <li>Expand annotator validation to 130+ commands</li>
            </ul>
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

