plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.fabricmc.net/")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.fabricmc:fabric-loom:1.10-SNAPSHOT")
}
