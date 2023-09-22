package com.chyzman.chowl.util;

import com.chyzman.chowl.Chowl;
import net.minecraft.util.Identifier;

public class ChowlRegistryHelper {
    public static Identifier id(String path) {
        return new Identifier(Chowl.MODID, path);
    }
}