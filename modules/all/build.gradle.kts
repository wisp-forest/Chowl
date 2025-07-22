plugins {
    id("chowl-testable")
}

dependencies {
    rootProject.subprojects.forEach {
        if (it.name != "all") useModule(it.name)
    }
}
