plugins {
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kotlinx.atomicfu)
}

group = "com.fleeksoft.charset"
version = libs.versions.libraryVersion.get()

mavenPublishing {
    coordinates("com.fleeksoft.charset", "charset-ext", libs.versions.libraryVersion.get())
    pom {
        name.set("charset-ext")
        description.set("Kotlni Multiplatform Charsets")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        url.set("https://github.com/fleeksoft/charset")
        issueManagement {
            system.set("Github")
            url.set("https://github.com/fleeksoft/charset/issues")
        }
        scm {
            connection.set("https://github.com/fleeksoft/charset.git")
            url.set("https://github.com/fleeksoft/charset")
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