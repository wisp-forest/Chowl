plugins {
    id("chowl-testable")
}

tasks.jar { enabled = false }
tasks.remapJar { enabled = false }

dependencies {
    rootProject.subprojects.forEach {
        if (it.name != project.name) useModule(it.name)
    }
}
