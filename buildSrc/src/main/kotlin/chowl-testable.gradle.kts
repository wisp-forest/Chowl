import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("fabric-loom")
    id("java")
    id("java-library")
}

extensions.configure<LoomGradleExtensionAPI> {
    runs {
        create("testmodClient") {
            client()
            ideConfigGenerated(true)
            name("Testmod Client")
            runDir("../../run")
            source("test")
        }

        create("testmodServer") {
            server()
            ideConfigGenerated(true)
            name("Testmod Server")
            runDir("../../run")
            source("test")
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
            source("test")
        }

        configureEach {
            if (!name.startsWith("testmod")) return@configureEach

            vmArg("-XX:+AllowEnhancedClassRedefinition")
            source("test")

            if (devUserInfo != null) programArgs(devUserInfo.split(" "))
        }

        afterEvaluate {
            var mixin: String? = null
            try {
                val compileClasspath = project.configurations.getByName("compileClasspath")
                val resolved = compileClasspath.resolvedConfiguration.resolvedArtifacts
                val artifact = resolved.firstOrNull { it.name == "sponge-mixin" }
                if (artifact != null) {
                    mixin = artifact.file.absolutePath
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
                    vmArg("-javaagent=\"$mixin\"")
                }
            }
        }
    }
}
