repositories {
    mavenLocal()

    maven {
        name = "Create"
        url = uri("https://maven.createmod.net")
    }

    maven {
        name = "FTB"
        url = uri("https://maven.ftb.dev/releases")
        content {
            includeGroup("dev.ftb.mods")
            includeGroup("dev.architectury")
        }
    }

    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev")
        content {
            includeGroup("dev.architectury")
        }
    }

    maven {
        name = "Iglee"
        url = uri("https://maven.iglee.fr/releases")
    }

    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }

    maven {
        name = "Gegy"
        url = uri("https://maven.gegy.dev/releases")
    }
}
