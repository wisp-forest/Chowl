plugins {
    id("fabric-loom")
    id("maven-publish")
    id("base")
    id("java")
    id("java-library")
}

group = rootProject.property("maven_group")!!
version = "${rootProject.property(project.name + "_version")}+${rootProject.property("minecraft_base_version")})}"

base {
    if (rootProject.hasProperty("module_name")) {
        archivesName = "chowl-${rootProject.property("module_name")}"
    } else {
        archivesName = "chowl-${project.name}"
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
    include("io.wispforest:owo-sentinel:${rootProject.property("owo_version")}+${rootProject.property("wispforest_mc_version")}")

    modImplementation("io.wispforest:lavender:${rootProject.property("lavender_version")}+${rootProject.property("wispforest_mc_version")}")

    modImplementation(include("com.kneelawk.graphlib:graphlib-core-fabric:${rootProject.property("graphlib_version")}")!!)
//    modLocalRuntime("com.kneelawk.graphlib:graphlib-debugrender-fabric:${rootProject.property("graphlib_version")}")

//    modImplementation("eu.pb4:common-protection-api:${rootProject.property("cpa_version")}")

    if (project.name != "core") {
        api(project(":core", "namedElements"))
        include(project(":core", "namedElements"))
    }
}

rootProject.subprojects.forEach {
    loom.mods.register(it.name) {
        sourceSet(it.sourceSets["main"])
    }
}

loom {
    val accessWidener = file("src/main/resources/${project.name}.accesswidener")
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

        getByName("client") {
            client()
            name("[${project.name}] Client")
        }
        getByName("server") {
            server()
            name("[${project.name}] Server")
        }
        create("datagen") {
            client()
            name("[${project.name}] DataGen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${project.file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${project.name}")

            runDir("build/datagen")
        }
        var renderDocPath = System.getenv("renderDocPath")
        if (renderDocPath != null) {
            create("clientRenderDoc") {
                client()
                name("[${project.name}] Client - (RenderDoc)")
                vmArg("-Dowo.renderdocPath=${renderDocPath}")
            }
        } else {
            println("[Warning]: renderDocPath is not set in environment variables, skipping RenderDoc client run.")
        }
        var devUserInfo = System.getenv("minecraftDevUserInfo")
        if (devUserInfo == null) {
            println("[Warning]: minecraftDevUserInfo is not set in environment variables, skipping dev user info.")
        }
        create("clientMixinDump") {
            client()
            name("[${project.name}] Client - (Mixin Dump)")
            vmArg("-Dfabric.log.disableAnsi=false")
            vmArg("-Dmixin.debug.export=true")
        }

        configureEach {
            ideConfigGenerated(project.name == "test")
            runDir("../../run")
            source(sourceSets["main"])

            if (devUserInfo != null) programArg(devUserInfo)
        }

        afterEvaluate {
            var mixin: String? = null;
            try {
                var sponge = this.configurations.compileClasspath.get()
                    .allDependencies
                    .asIterable()
                    .firstOrNull { it.name == "sponge-mixin" }
                if (sponge != null) {
                    mixin = this.configurations.compileClasspath.get().files(sponge).first().path
                    println("[Info]: Mixin HotSwapping should work")
                } else {
                    println("[Warning]: Unable to locate file path for Mixin Jar, HotSwapping will NOT work!")
                }
            } catch (e: Exception) {
                println("[Error]: Mixin HotSwap had a issue!")
                e.printStackTrace()
            }
            if (mixin != null) {
                configureEach {
                    vmArg("-javaagent:\"$mixin\"")
                }
            }
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version_dep", rootProject.property("minecraft_version_dep"))
    inputs.property("loader_version", rootProject.property("loader_version"))
    inputs.property("owo_version", rootProject.property("owo_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version_dep" to rootProject.property("minecraft_version_dep"),
            "loader_version" to rootProject.property("loader_version"),
            "owo_version" to rootProject.property("owo_version")
        )
    }
}

//jar {
//    from("LICENSE") {
//        rename { "${it}_${project.base.archivesName}" }
//    }
//}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
}

java {
    withSourcesJar()
}
