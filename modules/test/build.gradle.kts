plugins {
    id("chowl-testable")
}

dependencies {
    rootProject.subprojects.forEach {
        if (it.name != "test") implementation(project(it.path, configuration = "namedElements"))
    }
}
