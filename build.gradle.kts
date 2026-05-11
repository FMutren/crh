plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev") version "2.0.141"
}

val minecraft_version: String by project
val minecraft_version_range: String by project
val neo_version: String by project
val neo_version_range: String by project
val loader_version_range: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_version: String by project
val mod_group_id: String by project
val mod_authors: String by project
val mod_description: String by project
val create_version: String by project
val ponder_version: String by project

version = mod_version
group = mod_group_id

repositories {
    mavenLocal()

    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    maven {
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }

    maven {
        url = uri("https://maven.createmod.net")
    }

    maven {
        // Registrate
        url = uri("https://maven.gegy.dev/releases")
    }
}

base {
    archivesName.set(mod_id)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val mainSourceSet = sourceSets.named("main")

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to minecraft_version_range,
        "neo_version" to neo_version,
        "neo_version_range" to neo_version_range,
        "loader_version_range" to loader_version_range,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )

    inputs.properties(replaceProperties)

    expand(replaceProperties)

    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets.named("main") {
    resources.srcDir("src/generated/resources")
    resources.srcDir(generateModMetadata)
}

neoForge {
    version = neo_version

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("gameTestServer") {
            type.set("gameTestServer")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            data()

            programArguments.addAll(
                listOf(
                    "--mod", mod_id,
                    "--all",
                    "--output", file("src/generated/resources/").absolutePath,
                    "--existing", file("src/main/resources/").absolutePath
                )
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel.set(org.slf4j.event.Level.DEBUG)
        }
    }

    mods {
        create(mod_id) {
            sourceSet(mainSourceSet.get())
        }
    }

    ideSyncTask(generateModMetadata)
}

dependencies {
    compileOnly("com.simibubi.create:create-$minecraft_version:$create_version:slim") {
        isTransitive = false
    }

    compileOnly("net.createmod.ponder:Ponder-NeoForge-$minecraft_version:$ponder_version")
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "localRepo"
            url = layout.projectDirectory.dir("repo").asFile.toURI()
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
