plugins {
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kotlinx.atomicfu)
}

group = "com.fleeksoft.io"
version = libs.versions.libraryVersion.get()

mavenPublishing {
    coordinates("com.fleeksoft.io", "uri", libs.versions.libraryVersion.get())
    pom {
        name.set("uri")
        description.set("Kotlin Multiplatform IO")
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