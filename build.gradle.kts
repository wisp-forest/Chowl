import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.fabricmc.loom.task.GenerateSourcesTask

plugins {
    id("java-library")
    id("idea")
    id("maven-publish")
    id("fabric-loom")
}

idea {
    module {
        inheritOutputDirs = true
    }
}

allprojects {
    group = rootProject.property("maven_group")!!

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }

    apply {
        plugin("java-library")
        plugin("fabric-loom")
        plugin("idea")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }

    java {
        withSourcesJar()
    }

    sourceSets {
        val testmod by creating {
            compileClasspath += main.get().compileClasspath
            runtimeClasspath += main.get().runtimeClasspath
        }

        test {
            compileClasspath += testmod.compileClasspath
            runtimeClasspath += testmod.runtimeClasspath
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

    loom.mods.register(project.name) {
        sourceSet(sourceSets.main.get())
    }

    if (project.name != "test") {
        loom.mods.register(project.name + "-testmod") {
            sourceSet(sourceSets["testmod"])
        }
    }

    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://api.modrinth.com/maven/")
        maven("https://maven.wispforest.io/releases/")
        maven("https://jitpack.io")
        maven("https://maven.nucleoid.xyz/") {
            content {
                includeGroupAndSubgroups("com.kneelawk.graphlib")
            }
        }
        maven("https://maven.cafeteria.dev/releases/")
        maven("https://maven.kneelawk.com/releases/")
        maven("https://maven.alexiil.uk/")
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        minecraft("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
//        mappings("net.fabricmc:yarn:${rootProject.property("yarn_mappings")}:v2")
        mappings(loom.officialMojangMappings())

        modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_version")}")

        annotationProcessor(modImplementation("io.wispforest:owo-lib:${rootProject.property("owo_version")}")!!)
//        include("io.wispforest:owo-sentinel:${rootProject.property("owo_version")}")

//        modImplementation("io.wispforest:lavender:${rootProject.property("lavender_version")}") {
//            exclude(group = "io.wispforest", module = "owo-lib")
//        }

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
    version = "${rootProject.property("${project.name}_version") }+${rootProject.property("minecraft_base_version")}".trim()

    fun configureFmjMerge(sourceSetName: String) {
        val fmj = project.file("src/$sourceSetName/resources/fabric.mod.json")
        if (!fmj.exists()) return

        val generatedResourcesDir = project.layout.buildDirectory.dir("generated/resources/$sourceSetName").get().asFile
        val outputFile = File(generatedResourcesDir, "fabric.mod.json")

        val templateFile = rootProject.file("fabric.mod.json")
        val slurper = JsonSlurper()
        val merged = mutableMapOf<String, Any>()

        if (templateFile.exists()) {
            @Suppress("UNCHECKED_CAST")
            merged.putAll(slurper.parseText(templateFile.readText()) as Map<String, Any>)
        }

        @Suppress("UNCHECKED_CAST")
        val local = slurper.parseText(fmj.readText()) as Map<String, Any>

        fun deepMerge(base: MutableMap<String, Any>, overlay: Map<String, Any>) {
            overlay.forEach { (key, value) ->
                when {
                    base[key] is Map<*, *> && value is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        deepMerge(base[key] as MutableMap<String, Any>, value as Map<String, Any>)
                    }
                    base[key] is List<*> && value is List<*> -> {
                        val existing = (base[key] as List<*>).toMutableList()
                        existing.addAll(value)
                        base[key] = existing.distinct() as Any
                    }
                    else -> base[key] = value
                }
            }
        }

        deepMerge(merged, local)

        merged["id"] = "chowl-${project.name.lowercase()}"
        merged["version"] = version.toString()

        val placeholderValues = mapOf(
            "version" to version.toString(),
            "module" to project.name,
            "minecraft_version" to rootProject.property("minecraft_version").toString(),
            "loader_version" to rootProject.property("loader_version").toString(),
            "owo_version" to rootProject.property("owo_version").toString(),
        )

        fun resolvePlaceholders(node: Any?): Any? = when (node) {
            is String -> placeholderValues.entries.fold(node) { acc, (k, v) -> acc.replace("\${$k}", v) }
            is Map<*, *> -> node.mapValues { (_, v) -> resolvePlaceholders(v)!! }
            is List<*> -> node.map { resolvePlaceholders(it) }
            else -> node
        }

        val resolved = resolvePlaceholders(merged) as Map<*, *>

        outputFile.parentFile.mkdirs()
        outputFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(resolved)))

        sourceSets.named(sourceSetName).configure {
            resources.srcDir(generatedResourcesDir)
            resources.exclude { it.file == fmj }
        }

        tasks.withType<ProcessResources> {
            if (name != "process${sourceSetName.replaceFirstChar { it.uppercaseChar() }}Resources") return@withType
            inputs.properties(placeholderValues)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            filesMatching("fabric.mod.json") { expand(placeholderValues) }
        }
    }

    configureFmjMerge("main")
    configureFmjMerge("test")

    tasks.jar {
        from("LICENSE") { rename { "${it}_${project.base.archivesName}" } }
    }

    base { archivesName = "chowl-${project.name}" }

    if (!project.file("src/main/resources/fabric.mod.json").exists()) {
        tasks.withType<ProcessResources> {
            val properties = mapOf(
                "version" to version,
                "module" to project.name,
                "minecraft_version" to rootProject.property("minecraft_version"),
                "loader_version" to rootProject.property("loader_version"),
                "owo_version" to rootProject.property("owo_version"),
            )

            inputs.properties(properties)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            filesMatching("fabric.mod.json") { expand(properties) }
        }
    }

    if (project.name == "test") {
        tasks.named<Jar>("jar") { enabled = false }
        tasks.matching { it.name == "remapJar" }.configureEach { enabled = false }
    }

    dependencies {
        "testmodImplementation"(sourceSets.main.get().output)

        if (project.name != "core") {
            api(project(":core", "namedElements"))
            include(project(":core", "namedElements"))
        }
    }
}

subprojects {
    if (project.name != "test") {
        tasks.register<Copy>("copyJarsToRoot") {
            from(layout.buildDirectory.dir("libs"))
            into(rootProject.layout.buildDirectory.dir("libs"))
            dependsOn("assemble")
        }

        tasks.named("build") {
            dependsOn("copyJarsToRoot")
        }
    }

    idea {
        module {
            inheritOutputDirs = true
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }
}

val subprojectList = subprojects.toList()
subprojectList.forEachIndexed { index, project ->
    project.tasks.withType<GenerateSourcesTask> {
        if (index > 0) {
            mustRunAfter(subprojectList[index - 1].tasks.withType<GenerateSourcesTask>())
        } else {
            mustRunAfter(rootProject.tasks.withType<GenerateSourcesTask>())
        }
    }
}

tasks.jar { enabled = false }
tasks.remapJar { enabled = false }
