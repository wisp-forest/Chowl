package com.chyzman.chowl.oddities.registry;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.oddities.Oddities;
import net.minecraft.registry.Registry;

public class OdditiesParts {

    private static <T extends Part> PartType<T> register(String id, PartType<T> partType) {
        return Registry.register(ChowlRegistries.PART, Oddities.id(id), partType);
    }

    public static void init() {}
}
