package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.impl.EmptyPart;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CoreParts {
    public static final PartType<EmptyPart> EMPTY = registerVanilla("empty", new PartType<>(EmptyPart.ENDEC, EmptyPart::new));

    private static <T extends Part> PartType<T> register(String id, PartType<T> partType) {
        return Registry.register(ChowlRegistries.PART, Chowl.id(id), partType);
    }

    private static <T extends Part> PartType<T> registerVanilla(String id, PartType<T> partType) {
        return Registry.register(ChowlRegistries.PART, Identifier.of(id), partType);
    }

    public static void init() {}
}
