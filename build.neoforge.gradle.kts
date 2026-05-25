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

fun prop(key: String): Provider<String> = providers.gradleProperty(key)

fun propString(key: String): String = prop(key).get()

val minecraftVersion = project.name.substringBeforeLast("-")
val minecraftVersionRange = libs.versions.minecraftRange.get()
val neoForgeVersion = libs.versions.neoForge.get()
val neoForgeVersionRange = libs.versions.neoForgeRange.get()
val loaderVersionRange = libs.versions.loaderRange.get()
val createVersionRange = propString("create_range_1_21_1")

val modId = propString("mod_id")
val modName = propString("mod_name")
val modLicense = propString("mod_license")
val modGroupId = propString("mod_group_id")
val modAuthors = propString("mod_authors")
val modDescription = propString("mod_description")
val modVersion = propString("mod_version")

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

apply(from = rootProject.file("gradle/crh-repositories.gradle.kts"))

repositories {
    maven {
        name = "tterragRegistrate"
        url = uri("https://maven.ithundxr.dev/snapshots")
        content {
            includeGroup("com.tterrag.registrate")
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


val commonResourceProperties = mapOf(
    "mixin_compatibility" to "JAVA_21",
    "mod_id" to modId,
    "pack_format" to 34
)

val generateExpandedCommonResources by tasks.registering(ProcessResources::class) {
    inputs.properties(commonResourceProperties)
    filteringCharset = "UTF-8"
    expand(commonResourceProperties)
    from(
        rootProject.file("src/main/resources/crh.mixins.json"),
        rootProject.file("src/main/resources/pack.mcmeta")
    )
    into(layout.buildDirectory.dir("generated/sources/commonResources"))
}


tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
            generateExpandedCommonResources,
            rootProject.file("versions/${project.name}/src/main/resources"),
            rootProject.file("src/main/resources"),
            generateModMetadata
        )
    )
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
    compileOnly("com.simibubi.create:create-1.21.1:${propString("create_1_21_1")}:slim") { isTransitive = false }
    add("localRuntime", "com.simibubi.create:create-1.21.1:${propString("create_1_21_1")}:slim") {
        isTransitive = false
    }

    compileOnly("com.tterrag.registrate:Registrate:${propString("registrate_1_21_1")}")
    add("localRuntime", "com.tterrag.registrate:Registrate:${propString("registrate_1_21_1")}")

    // Ponder & Flywheel Rendering
    compileOnly("net.createmod.ponder:ponder-neoforge:${propString("ponder_1_21_1")}+mc$minecraftVersion")
    add("localRuntime", "net.createmod.ponder:ponder-neoforge:${propString("ponder_1_21_1")}+mc$minecraftVersion")

    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-1.21.1:${propString("flywheel_1_21_1")}") {
        isTransitive = false
    }
    add(
        "localRuntime",
        "dev.engine-room.flywheel:flywheel-neoforge-1.21.1:${propString("flywheel_1_21_1")}"
    ) { isTransitive = false }

    // Create Casing
    compileOnly("fr.iglee42:CreateCasing:${propString("create_encased_1_21_1")}")
    add("localRuntime", "fr.iglee42:CreateCasing:${propString("create_encased_1_21_1")}")

    // FTB Ultimine
    compileOnly("dev.ftb.mods:ftb-ultimine-neoforge:${propString("ftb_ultimine_1_21_1")}")
    add("localRuntime", "dev.ftb.mods:ftb-ultimine-neoforge:${propString("ftb_ultimine_1_21_1")}") {
        isTransitive = false
    }
    add("localRuntime", "dev.ftb.mods:ftb-library-neoforge:${propString("ftb_library_1_21_1")}") {
        isTransitive = false
    }
    add("localRuntime", "dev.architectury:architectury-neoforge:${propString("architectury_neoforge_1_21_1")}")
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
