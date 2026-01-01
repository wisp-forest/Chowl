plugins {
    id("fabric-loom")
}

loom {
    runs {
        create("testmodClient") {
            client()
            ideConfigGenerated(true)
            name = "Testmod Client"
            runDir("../../run")
            source(sourceSets["testmod"])
        }

        create("testmodServer") {
            server()
            ideConfigGenerated(true)
            name = "Testmod Server"
            runDir("../../run")
            source(sourceSets["testmod"])
        }

        /*maybeCreate("clientRenderDoc").apply {
            ideConfigGenerated(true)
            name("Client - (RenderDoc)")
        }*/

        var devUserInfo = System.getenv("minecraftDevUserInfo")
        if (devUserInfo == null) {
            println("[Warning]: minecraftDevUserInfo is not set in environment variables, skipping dev user info.")
        }

        create("testmodClientMixinDump") {
            client()
            ideConfigGenerated(true)
            name("Testmod Client - (Mixin Dump)")
            vmArg("-Dfabric.log.disableAnsi=false")
            vmArg("-Dmixin.debug.export=true")
            runDir("../../run")
            source(sourceSets["testmod"])
        }

        configureEach {
            if (!name.startsWith("testmod")) return@configureEach

            vmArg("-XX:+AllowEnhancedClassRedefinition")
            source(sourceSets.test.get())

            if (devUserInfo != null) programArgs(devUserInfo.split(" "))
        }

        afterEvaluate {
            var mixin: String? = null
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
                println("[Error]: Mixin HotSwap had an issue!")
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
