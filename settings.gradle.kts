pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

val modules = listOf(
    "core",
    "industries",
    "logistics",
    "visage",
    "electromechanics",
    "oddities",
    "test"
)

modules.forEach { module ->
    include(module)
    project(":$module").projectDir = file("modules/$module")
}
