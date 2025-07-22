plugins {
    id("fabric-loom")
    id("maven-publish")
    id("base")
    id("java")
    id("java-library")
    //id("chowl-base")
}

loom {
    runs {
        create("datagen") {
            client()
            name("[${project.name}] DataGen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${project.file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${project.name}")

            runDir("build/datagen")
        }
    }
}
