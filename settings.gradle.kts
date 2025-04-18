pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/amper/amper")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

/*plugins {
    id("org.jetbrains.amper.settings.plugin").version("0.5.0-dev-1997")
}*/

include("io-core")
include("io")
include("uri")
include("kotlinx-io")
include("okio")
include("charset")
include("charset-ext")
//include("charset-ext-big")