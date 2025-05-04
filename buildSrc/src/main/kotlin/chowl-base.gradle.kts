plugins {
    id("fabric-loom")
    id("maven-publish")
    id("base")
    id("java")
    id("java-library")
}

group = rootProject.property("maven_group")!!
version = "${rootProject.property(project.name + "_version")}+${rootProject.property("minecraft_base_version")}"

base {
    archivesName = if (project.hasProperty("module_name")) "${project.property("module_name")}" else "chowl-${project.name}"
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven/")
    maven("https://maven.wispforest.io/releases/")
    maven("https://jitpack.io")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.cafeteria.dev/releases/")
    maven("https://maven.kneelawk.com/releases/")
    maven("https://maven.alexiil.uk/")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${rootProject.property("yarn_mappings")}:v2")

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_version")}")

    annotationProcessor(modImplementation("io.wispforest:owo-lib:${rootProject.property("owo_version")}+${rootProject.property("wispforest_mc_version")}")!!)
//    include("io.wispforest:owo-sentinel:${rootProject.property("owo_version")}+${rootProject.property("wispforest_mc_version")}")

    modImplementation("io.wispforest:lavender:${rootProject.property("lavender_version")}+${rootProject.property("wispforest_mc_version")}")

    modImplementation(include("com.kneelawk.graphlib:graphlib-core-fabric:${rootProject.property("graphlib_version")}")!!)
//    modLocalRuntime("com.kneelawk.graphlib:graphlib-debugrender-fabric:${rootProject.property("graphlib_version")}")

//    modImplementation("eu.pb4:common-protection-api:${rootProject.property("cpa_version")}")

    if (project.name != "core") {
        api(project(":core", "namedElements"))
        include(project(":core", "namedElements"))
    }
}

loom {
    val accessWidener = file("src/main/resources/chowl-${project.name}.accesswidener")
    if (accessWidener.exists()) accessWidenerPath = accessWidener

    runtimeOnlyLog4j = true

    sourceSets {
        main {
            resources {
                srcDir(project.file("src/generated/resources"))
                exclude(project.file("src/generated/resources/.cache").toString())
            }
        }
    }

    runs {
        configureEach {
            ideConfigGenerated(false)
            runDir("../../run")
            source(project.sourceSets["main"])
        }
    }
}

//rootProject.subprojects.forEach {
//    loom.mods.register(it.name) {x
//        sourceSet(it.sourceSets["main"])
//    }
//}

tasks.processResources {
    filteringCharset = "UTF-8"

    val properties = mapOf(
        "version" to project.version,
        "minecraft_version_dep" to rootProject.property("minecraft_version_dep"),
        "loader_version" to rootProject.property("loader_version"),
        "owo_version" to rootProject.property("owo_version")
    )

    inputs.properties(properties)
    filesMatching("fabric.mod.json") { expand(properties) }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
}

tasks.withType<AbstractArchiveTask> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

java {
    withSourcesJar()
}
