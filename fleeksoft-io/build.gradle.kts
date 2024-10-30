plugins {
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kotlinx.atomicfu)
}

group = "com.fleeksoft.io"
version = libs.versions.libraryVersion.get()

mavenPublishing {
    coordinates("com.fleeksoft.io", "io", libs.versions.libraryVersion.get())
    pom {
        name.set("io")
        description.set("Kotlni Multiplatform Charsets")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        url.set("https://github.com/fleeksoft/fleeksoft-io")
        issueManagement {
            system.set("Github")
            url.set("https://github.com/fleeksoft/fleeksoft-io/issues")
        }
        scm {
            connection.set("https://github.com/fleeksoft/fleeksoft-io.git")
            url.set("https://github.com/fleeksoft/fleeksoft-io")
        }
        developers {
            developer {
                name.set("Sabeeh Ul Hussnain Anjum")
                email.set("fleeksoft@gmail.com")
                organization.set("Fleek Soft")
            }
        }
    }
}


val rootPath = "generated/kotlin"
kotlin {
    sourceSets {
        commonTest {
            this.kotlin.srcDir(layout.buildDirectory.file(rootPath))
        }
    }
}

val generateBuildConfigFile: Task by tasks.creating {
    group = "build setup"
    val file = layout.buildDirectory.file("$rootPath/BuildConfig.kt")
    outputs.file(file)

    doLast {
        val content =
            """
            package com.fleeksoft.io

            object BuildConfig {
                const val PROJECT_ROOT: String = "${rootProject.rootDir.absolutePath.replace("\\", "\\\\")}"
            }
            """.trimIndent()
        file.get().asFile.writeText(content)
    }
}

// Configure the generateBuildConfigFile task to run only for test tasks
tasks.withType<Test>().configureEach {
    dependsOn(generateBuildConfigFile)
}

// Ensure generateBuildConfigFile runs before compileKotlinJvm
tasks.named("compileTestKotlinJvm").configure {
    dependsOn(generateBuildConfigFile)
}