pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("net.fabricmc.fabric-loom").version(settings.extra["loom_version"] as String)
        kotlin("jvm").version(System.getProperty("kotlin_version"))
    }
}

rootProject.name = "direbats"
