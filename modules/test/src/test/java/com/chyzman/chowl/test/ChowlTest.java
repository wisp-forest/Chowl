package com.chyzman.chowl.test;

import com.chyzman.chowl.test.registry.TestBlockEntities;
import com.chyzman.chowl.test.registry.TestBlocks;
import com.chyzman.chowl.test.registry.TestItems;
import com.chyzman.chowl.test.registry.TestParts;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ChowlTest implements ModInitializer {
    public static final String MODID = "chowl-test";

    @Override
    public void onInitialize() {
        TestBlocks.init();
        TestItems.init();
        TestBlockEntities.init();
        TestParts.init();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
