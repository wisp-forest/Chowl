plugins {
    id("chowl-base")
}

dependencies {
    modCompileOnly("maven.modrinth:sodium:${rootProject.property("sodium_version")}")
}
