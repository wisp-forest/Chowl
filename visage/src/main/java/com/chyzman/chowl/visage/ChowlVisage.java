package com.chyzman.chowl.visage;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ChowlVisage implements ModInitializer {
    public static final String MODID = "chowl-visage";

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
