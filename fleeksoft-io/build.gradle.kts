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
        url.set("https://github.com/fleeksoft/ksoup")
        issueManagement {
            system.set("Github")
            url.set("https://github.com/fleeksoft/ksoup/issues")
        }
        scm {
            connection.set("https://github.com/fleeksoft/ksoup.git")
            url.set("https://github.com/fleeksoft/ksoup")
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