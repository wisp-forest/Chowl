plugins {
    id("chowl-base")
}

dependencies {
    rootProject.subprojects.forEach {
        if (it.name != "test") implementation(project(it.path, configuration = "namedElements"))
    }
}
