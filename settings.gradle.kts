pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

rootProject.name = "chowl"

module("core")

val focus: File = File("focus.txt");

val modules = if (focus.exists()) focus.readLines() else listOf(
    "industries",
    "logistics",
    "visage",
    "electromechanics",
    // "oddities",
    "test"
)

modules.forEach { module(it) }

fun module(module: String) {
    include(module)
    project(":$module").projectDir = file("modules/$module")
}
