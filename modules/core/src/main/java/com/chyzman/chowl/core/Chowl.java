package com.chyzman.chowl.core;

import com.chyzman.chowl.core.debug.DebugCommand;
import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.chyzman.chowl.core.network.ChowlPackets;
import com.chyzman.chowl.core.panel.TemporaryPanelInitThingy;
import com.chyzman.chowl.core.registry.*;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chowl implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Chowl");
    public static final String MODID = "chowl";

//    public static final ChowlCoreConfig CONFIG = ChowlCoreConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ChowlRegistries.init();
        CoreBlockEntities.init();
        CoreBlocks.init();
        CoreParts.init();

        ChowlPackets.registerCommon();

        NetworkRegistry.init();

        FieldRegistrationHandler.register(ChowlComponents.class, MODID, true);

        DebugCommand.register();

        TemporaryPanelInitThingy.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
