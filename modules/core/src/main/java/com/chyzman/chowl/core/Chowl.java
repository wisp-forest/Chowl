package com.chyzman.chowl.core;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-core";



//    public static final ChowlCoreConfig CONFIG = ChowlCoreConfig.createAndLoad();

    @Override
    public void onInitialize() {
//        FieldRegistrationHandler.register(ChowlCoreComponents.class, MODID, true);
//        FieldRegistrationHandler.register(ChowlCoreBlocks.class, MODID, true);
//
//        NetworkRegistry.init();
//
//        ChowlCoreNetworking.init();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
