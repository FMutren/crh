plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev.legacyforge")
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

fun prop(key: String): Provider<String> = providers.gradleProperty(key)

fun propString(key: String): String = prop(key).get()

val minecraftVersion = project.name.substringBeforeLast("-")
val forgeVersion = propString("forge_1_20_1")

val modId = propString("mod_id")
val modName = propString("mod_name")
val modLicense = propString("mod_license")
val modGroupId = propString("mod_group_id")
val modAuthors = propString("mod_authors")
val modDescription = propString("mod_description")
val modVersion = propString("mod_version")
val createVersionRange = propString("create_range_1_20_1")

version = modVersion
group = modGroupId

base {
    archivesName.set("$modId-$minecraftVersion-forge")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

apply(from = rootProject.file("gradle/crh-repositories.gradle.kts"))

repositories {
    maven {
        name = "tterrag"
        url = uri("https://maven.tterrag.com/")
    }
}

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to "[1.20.1,1.21)",
        "loader_version_range" to "[47,)",
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
    from(rootProject.file("src/main/templates/forge"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}


val commonResourceProperties = mapOf(
    "mixin_compatibility" to "JAVA_17",
    "mod_id" to modId,
    "pack_format" to 15
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
    // The expanded generated resources are added before raw resources below.
    // EXCLUDE makes generated crh.mixins.json/pack.mcmeta win without failing on the raw templates.
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

legacyForge {
    version = "1.20.1-$forgeVersion"
    validateAccessTransformers = true

    runs {
        create("client") {
            client()
            gameDirectory = file("run/")
        }
        create("server") {
            server()
            gameDirectory = file("run/")
            programArgument("--nogui")
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
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest.attributes("MixinConfigs" to "$modId.mixins.json")
    from(layout.buildDirectory.dir("mixin")) {
        include("$modId.refmap.json")
    }
}

tasks.named("reobfJar") {
    enabled = false
}

dependencies {
    // Core Create Mod Framework & Core Dependencies
    modImplementation("com.simibubi.create:create-1.20.1:${propString("create_1_20_1")}:slim") {
        isTransitive = false
    }
    modImplementation("net.createmod.ponder:Ponder-Forge-1.20.1:${propString("ponder_1_20_1")}")
    modImplementation("com.tterrag.registrate:Registrate:${propString("registrate_1_20_1")}")

    // Flywheel Rendering Engine
    modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-1.20.1:${propString("flywheel_1_20_1")}")
    modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-1.20.1:${propString("flywheel_1_20_1")}") {
        isTransitive = false
    }

    // Mixin & MixinExtras
    compileOnly(annotationProcessor("org.spongepowered:mixin:0.8.5:processor")!!)
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
    implementation("io.github.llamalad7:mixinextras-forge:0.4.1")

    // Optional Create Casing compatibility. Keep it compile-only for production, but load it in dev runs.
    modCompileOnly("fr.iglee42:CreateCasing:${propString("create_encased_1_20_1")}")
    modRuntimeOnly("fr.iglee42:CreateCasing:${propString("create_encased_1_20_1")}")

    // FTB Ultimine
    modImplementation("dev.ftb.mods:ftb-ultimine-forge:${propString("ftb_ultimine_1_20_1")}") {
        isTransitive = false
    }
    modRuntimeOnly("dev.ftb.mods:ftb-library-forge:${propString("ftb_library_1_20_1")}") {
        isTransitive = false
    }
    compileOnly("dev.architectury:architectury-forge:${propString("architectury_forge_1_20_1")}")
    modRuntimeOnly("dev.architectury:architectury-forge:${propString("architectury_forge_1_20_1")}")
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
