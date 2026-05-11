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
            url = uri("https://maven.neoforged.net/releases")
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
}
