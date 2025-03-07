package com.chyzman.chowl.industries;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Industries implements ModInitializer {
    public static final String MODID = "chowl-industries";

    @Override
    public void onInitialize() {
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
