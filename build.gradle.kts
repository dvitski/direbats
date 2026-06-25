plugins {
    kotlin("jvm").version(System.getProperty("kotlin_version"))

    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin") version "2.0.1"
}

base { archivesName.set(extra["archives_base_name"] as String) }

val versionMinecraft = extra["minecraft_version"] as String

version = "${extra["mod_version"]}+$versionMinecraft"
group = extra["maven_group"] as String

val modId = extra["mod_id"] as String
val versionJava = extra["java_version"] as String
val versionLoader = extra["loader_version"] as String
val versionFabricApi = extra["fabric_version"] as String
val versionFabricKotlin = extra["fabric_language_kotlin_version"] as String

dependencies {
    minecraft("com.mojang", "minecraft", versionMinecraft)
    implementation("net.fabricmc", "fabric-loader", versionLoader)
    implementation("net.fabricmc.fabric-api", "fabric-api", versionFabricApi)
    include(implementation("net.fabricmc", "fabric-language-kotlin", versionFabricKotlin))
}

tasks {
    val javaVersion = JavaVersion.toVersion(versionJava.toInt())
    val javaVersionString = javaVersion.toString()

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersionString
        targetCompatibility = javaVersionString
        options.release.set(javaVersionString.toInt())
    }

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersionString)) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }

    // jar { from("LICENSE") { rename { "${it}_${base.archivesName.get()}" } } }
    processResources { filesMatching("fabric.mod.json") { expand(mapOf("version" to version)) } }
}

/* Data Generation */

val generatedResourcesDir = "src/main/generated"

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath.set(file("src/main/resources/$modId.accesswidener"))

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runs {
        create("Data Generation") {
            client()

            jvmArguments.addAll(
                "-Dfabric-api.datagen",
                "-Dfabric-api.datagen.output-dir=${file(generatedResourcesDir)}",
                "-Dfabric-api.datagen.modid=$modId",
            )

            runDirectory.set(file("build/datagen"))
            sourceSet.set(sourceSets["client"].name)
        }
    }
}

sourceSets.main {
    resources {
        srcDir(generatedResourcesDir)
    }
}

/* Releasing */

publishMods {
    file.set(tasks.jar.flatMap { it.archiveFile })

    version.set(project.version.toString())
    type.set(STABLE)
    modLoaders.add("fabric")

    val changelogFile = file("CHANGELOG.md")
    changelog.set(if (changelogFile.exists()) changelogFile.readText() else "No changelog provided")

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository.set("dvitski/direbats")
        commitish.set("latest")
    }

    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_API_KEY"))
        projectId.set("epF1CjCx")
        minecraftVersions.add(versionMinecraft)
    }

    curseforge { // god forbid
        projectId.set("407987")
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        minecraftVersions.add(versionMinecraft)
        client.set(true)
        server.set(true)
    }
}
