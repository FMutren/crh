plugins {
    `java-library`
    `maven-publish`
    idea
    alias(libs.plugins.moddev)
}

val minecraftVersion = libs.versions.minecraft.get()
val minecraftVersionRange = libs.versions.minecraftRange.get()
val neoForgeVersion = libs.versions.neoForge.get()
val neoForgeVersionRange = libs.versions.neoForgeRange.get()
val loaderVersionRange = libs.versions.loaderRange.get()
val modVersion = libs.versions.mod.get()
val createVersionRange = libs.versions.createRange.get()

val modId = providers.gradleProperty("mod_id").get()
val modName = providers.gradleProperty("mod_name").get()
val modLicense = providers.gradleProperty("mod_license").get()
val modGroupId = providers.gradleProperty("mod_group_id").get()
val modAuthors = providers.gradleProperty("mod_authors").get()
val modDescription = providers.gradleProperty("mod_description").get()

version = modVersion
group = modGroupId

repositories {
    mavenLocal()

    maven {
        name = "igleeRepoReleases"
        url = uri("https://maven.iglee.fr/releases")
    }

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
        url = uri("https://maven.gegy.dev/releases")
    }

    maven {
        url = uri("https://maven.ftb.dev/releases")
        content {
            includeGroup("dev.ftb.mods")
            includeGroup("dev.architectury")
        }
    }

    maven {
        url = uri("https://maven.architectury.dev")
        content {
            includeGroup("dev.architectury")
        }
    }
}

base {
    archivesName.set(modId)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val mainSourceSet = sourceSets.named("main")

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "neo_version" to neoForgeVersion,
        "neo_version_range" to neoForgeVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "create_version_range" to createVersionRange
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
    version = neoForgeVersion

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("gameTestServer") {
            type.set("gameTestServer")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            data()
            programArguments.addAll(
                listOf(
                    "--mod", modId,
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
        create(modId) {
            sourceSet(mainSourceSet.get())
        }
    }

    ideSyncTask(generateModMetadata)
}

dependencies {
    compileOnly(variantOf(libs.create) { classifier("slim") }) {
        isTransitive = false
    }
    compileOnly(libs.registrate)
    compileOnly(libs.ponder)
    compileOnly(libs.flywheel) { isTransitive = false }
    compileOnly(libs.ftbUltimineNeoForge)
    compileOnly(libs.createCasing)
    compileOnly(libs.copycats)
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