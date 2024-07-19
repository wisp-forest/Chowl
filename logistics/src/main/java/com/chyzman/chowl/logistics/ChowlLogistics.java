package com.chyzman.chowl.logistics;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ChowlLogistics implements ModInitializer {
    public static final String MODID = "chowl-logistics";

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
