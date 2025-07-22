plugins {
    id("java-library")
    id("eclipse")
    id("maven-publish")
    id("fabric-loom")
}

allprojects {
    group = rootProject.property("maven_group")!!

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }

    apply {
        plugin("java-library")
        plugin("fabric-loom")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }

    java {
        withSourcesJar()
    }

    sourceSets {
        create("testmod") {
            compileClasspath += main.get().compileClasspath
            runtimeClasspath += main.get().runtimeClasspath
        }

        test {
            compileClasspath += get("testmod").compileClasspath
            runtimeClasspath += get("testmod").runtimeClasspath
        }
    }

    loom {
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

    allprojects.forEach { p ->
        loom.mods.register(p.name) {
            sourceSet( p.sourceSets.main.get())
        }

        loom.mods.register(p.name + "-testmod") {
            sourceSet(p.sourceSets["testmod"])
        }
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
//        include("io.wispforest:owo-sentinel:${rootProject.property("owo_version")}+${rootProject.property("wispforest_mc_version")}")

        modImplementation("io.wispforest:lavender:${rootProject.property("lavender_version")}+${rootProject.property("wispforest_mc_version")}")

        modImplementation(include("com.kneelawk.graphlib:graphlib-core-fabric:${rootProject.property("graphlib_version")}")!!)
//        modLocalRuntime("com.kneelawk.graphlib:graphlib-debugrender-fabric:${rootProject.property("graphlib_version")}")

//        modImplementation("eu.pb4:common-protection-api:${rootProject.property("cpa_version")}")

        "testmodImplementation"(sourceSets.main.get().output)
    }

    tasks.withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

subprojects {
    version = "${rootProject.property(project.name + "_version")}+${rootProject.property("minecraft_base_version")}"

    tasks.jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName}" }
        }
    }

    base {
        archivesName = "chowl-${project.name}"
    }

    tasks.withType<ProcessResources> {
        val properties = mapOf(
            "version" to version,
            "minecraft_version_dep" to rootProject.property("minecraft_version_dep"),
            "loader_version" to rootProject.property("loader_version"),
            "owo_version" to rootProject.property("owo_version")
        )

        inputs.properties(properties)
        filesMatching("fabric.mod.json") { expand(properties) }
    }

    dependencies {
        "testmodImplementation"(sourceSets.main.get().output)

        if (project.name != "core") {
            api(project(":core", "namedElements"))
            include(project(":core", "namedElements"))
        }
    }
}
