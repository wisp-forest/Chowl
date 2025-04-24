package com.chyzman.chowl.core.multipart;

import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class Part implements Comparable<Part> {
    public static final Endec<Part> PART_ENDEC = Endec.dispatched(Part::getPart, Part::getId, MinecraftEndecs.IDENTIFIER);
    public static final Endec<Map<Identifier, Part>> ENDEC = Endec.map(MinecraftEndecs.IDENTIFIER, PART_ENDEC);

    public abstract Identifier getId();

    private static Endec<? extends Part> getPart(Identifier identifier) {
        return ChowlRegistries.PART.get(identifier).getEndec();
    }
}
