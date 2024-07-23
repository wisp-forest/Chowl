package com.chyzman.chowl.core.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ChannelUtil {
    public static Identifier getChannelId(String modId) {
        String v = FabricLoader.getInstance()
            .getModContainer(modId)
            .orElseThrow()
            .getMetadata()
            .getVersion()
            .getFriendlyString();

        int plusIndex = v.indexOf('+');

        return Identifier.of(modId, v.substring(0, plusIndex));
    }
}
