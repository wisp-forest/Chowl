package com.chyzman.chowl.util;

import com.chyzman.chowl.Chowl;
import net.minecraft.util.Identifier;

public class ChowlRegistryHelper {
    public static Identifier id(String path) {
        return Identifier.of(Chowl.MODID, path);
    }
}