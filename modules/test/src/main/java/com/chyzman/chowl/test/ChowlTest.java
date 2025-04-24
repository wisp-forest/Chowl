package com.chyzman.chowl.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ChowlTest implements ModInitializer {
    public static final String MODID = "chowl-test";

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
