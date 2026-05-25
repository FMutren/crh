plugins {
    id("dev.kikugie.stonecutter")

    id("net.neoforged.moddev") version "2.0.141" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.141" apply false
}

stonecutter active file(".sc_active_version")

tasks.register("runActiveClient") {
    group = "stonecutter"
    dependsOn(stonecutter.current!!.project + ":runClient")
}

tasks.register("runActiveServer") {
    group = "stonecutter"
    dependsOn(stonecutter.current!!.project + ":runServer")
}

stonecutter parameters {
    constants.match(current.project.substringAfterLast("-"), "forge", "neoforge")
}
