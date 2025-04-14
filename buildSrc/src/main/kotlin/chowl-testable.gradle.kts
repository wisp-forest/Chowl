plugins {
    id("fabric-loom")
    id("maven-publish")
    id("base")
    id("java")
    id("java-library")
    id("chowl-base")
}

loom {
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
            ideConfigGenerated(true)
//            runDir("../../run")
//            source(sourceSets["main"])

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
                    @Suppress("DEPRECATION")
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
