pluginManagement {
    fun versionFromCatalog(alias: String): String {
        val catalog = file("gradle/libs.versions.toml").readText()
        val pattern = Regex("""(?m)^\s*${Regex.escape(alias)}\s*=\s*"([^"]+)"\s*$""")
        return pattern.find(catalog)?.groupValues?.get(1)
            ?: error("Missing version '$alias' in gradle/libs.versions.toml")
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.kikugie.dev/releases")
        }
        maven {
            url = uri("https://maven.kikugie.dev/snapshots")
        }
        maven {
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            url = uri("https://maven.minecraftforge.net")
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.gradle.toolchains.foojay-resolver-convention") {
                useVersion(versionFromCatalog("foojayResolverConvention"))
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
    id("dev.kikugie.stonecutter") version "0.9.4"
}

stonecutter {
    create(rootProject) {
        version("1.20.1-forge", "1.20.1").buildscript = "build.forge.gradle.kts"
        version("1.21.1-neoforge", "1.21.1").buildscript = "build.neoforge.gradle.kts"

        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = "crh"
