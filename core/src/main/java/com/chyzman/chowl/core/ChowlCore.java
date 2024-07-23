package com.chyzman.chowl.core;

import com.chyzman.chowl.core.network.ChowlCoreNetworking;
import com.chyzman.chowl.core.registry.ChowlCoreComponents;
import com.chyzman.chowl.core.util.ChowlCoreConfig;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ChowlCore implements ModInitializer {
    public static final String MODID = "chowl-core";

    public static final ChowlCoreConfig CONFIG = ChowlCoreConfig.createAndLoad();

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(ChowlCoreComponents.class, MODID, true);

        ChowlCoreNetworking.init();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
