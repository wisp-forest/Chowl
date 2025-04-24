package com.chyzman.chowl.core;

import com.chyzman.chowl.core.debug.DebugCommand;
import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.chyzman.chowl.core.network.ChowlPackets;
import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.chyzman.chowl.core.registry.CoreBlocks;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-core";

//    public static final ChowlCoreConfig CONFIG = ChowlCoreConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ChowlRegistries.init();
        CoreBlocks.init();
        CoreBlockEntities.init();
        ChowlPackets.registerCommon();

        NetworkRegistry.init();

        FieldRegistrationHandler.register(ChowlComponents.class, MODID, true);

        DebugCommand.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
