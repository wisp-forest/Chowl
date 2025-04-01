package com.chyzman.chowl.core;

import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Attackable;
import net.minecraft.util.Identifier;

import java.util.List;

public class Chowl implements ModInitializer {
    public static final String MODID = "chowl-core";

//    public static final ChowlCoreConfig CONFIG = ChowlCoreConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ChowlRegistries.init();

        FieldRegistrationHandler.register(ChowlComponents.class, MODID, true);
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
