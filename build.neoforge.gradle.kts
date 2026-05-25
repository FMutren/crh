plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev")
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

fun rootProp(key: String): String {
    val text = rootProject.file("gradle.properties").readText().replace("\r", "")
    val pattern = Regex("""(?m)^\s*${Regex.escape(key)}\s*=\s*(.+)\s*$""")
    return pattern.find(text)?.groupValues?.get(1)?.trim()
        ?: error("Missing property '$key' in gradle.properties")
}

val minecraftVersion = project.name.substringBeforeLast("-")
val minecraftVersionRange = libs.versions.minecraftRange.get()
val neoForgeVersion = libs.versions.neoForge.get()
val neoForgeVersionRange = libs.versions.neoForgeRange.get()
val loaderVersionRange = libs.versions.loaderRange.get()
val createVersionRange = libs.versions.createRange.get()

val modId = rootProp("mod_id")
val modName = rootProp("mod_name")
val modLicense = rootProp("mod_license")
val modGroupId = rootProp("mod_group_id")
val modAuthors = rootProp("mod_authors")
val modDescription = rootProp("mod_description")
val modVersion = rootProp("mod_version")

version = modVersion
group = modGroupId

base {
    archivesName.set("$modId-$minecraftVersion-neoforge")
}


val localRuntime = configurations.maybeCreate("localRuntime")
configurations.named(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME) {
    extendsFrom(localRuntime)
}

java {
    toolchain {
        languageVersion.set(
            JavaLanguageVersion.of(
                if (minecraftVersion == "1.20.1") 17 else 21
            )
        )
    }
}

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
        url = uri("https://maven.ithundxr.dev/snapshots")
        content {
            includeGroup("com.tterrag.registrate")
        }
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
    from(rootProject.file("src/main/templates/neoforge"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}


tasks.named<ProcessResources>("processResources") {
    inputs.property("mixin_compatibility", "JAVA_21")
    inputs.property("pack_format", 34)

    filesMatching("crh.mixins.json") {
        expand("mixin_compatibility" to "JAVA_21")
    }
    filesMatching("pack.mcmeta") {
        expand("pack_format" to 34)
    }
}

sourceSets.named("main") {
    // Common code/resources stay in the original root src tree so Git history stays on src/**.
    // Version-only implementations live under versions/<version>/src/main/**.
    java.setSrcDirs(
        listOf(
            rootProject.file("src/main/java"),
            rootProject.file("versions/${project.name}/src/main/java")
        )
    )
    resources.setSrcDirs(
        listOf(
            rootProject.file("src/main/resources"),
            rootProject.file("versions/${project.name}/src/main/resources")
        )
    )
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
                    "--output", rootProject.file("src/generated/resources/").absolutePath,
                    "--existing", rootProject.file("src/main/resources/").absolutePath
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
            sourceSet(sourceSets.main.get())
        }
    }

    ideSyncTask(generateModMetadata)
}

tasks.named("compileJava") {
    dependsOn("stonecutterGenerate")
}

dependencies {
    // Create Mod & Registrate Core
    compileOnly(variantOf(libs.create) { classifier("slim") }) { isTransitive = false }
    add("localRuntime", "com.simibubi.create:create-1.21.1:${rootProp("create_1_21_1")}:slim") { isTransitive = false }

    compileOnly(libs.registrate)
    add("localRuntime", "com.tterrag.registrate:Registrate:${rootProp("registrate_1_21_1")}")

    // Ponder & Flywheel Rendering
    compileOnly("net.createmod.ponder:ponder-neoforge:${rootProp("ponder_1_21_1")}+mc$minecraftVersion")
    add("localRuntime", "net.createmod.ponder:ponder-neoforge:${rootProp("ponder_1_21_1")}+mc$minecraftVersion")

    compileOnly(libs.flywheel) { isTransitive = false }
    add("localRuntime", "dev.engine-room.flywheel:flywheel-neoforge-1.21.1:${rootProp("flywheel_1_21_1")}") { isTransitive = false }

    // Create Casing
    compileOnly(libs.createCasing)
    add("localRuntime", "fr.iglee42:CreateCasing:${rootProp("create_encased_1_21_1")}")

    // FTB Ultimine
    compileOnly(libs.ftbUltimineNeoForge)
    add("localRuntime", "dev.ftb.mods:ftb-ultimine-neoforge:${rootProp("ftb_ultimine_1_21_1")}") { isTransitive = false }
    add("localRuntime", "dev.ftb.mods:ftb-library-neoforge:${rootProp("ftb_library_1_21_1")}") { isTransitive = false }
    add("localRuntime", "dev.architectury:architectury-neoforge:${rootProp("architectury_neoforge_1_21_1")}")
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
