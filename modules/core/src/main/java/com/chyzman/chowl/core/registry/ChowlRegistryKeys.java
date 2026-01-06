package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.multipart.api.PartType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ChowlRegistryKeys {
    public static final ResourceKey<Registry<AttachableType<?>>> ATTACHABLE_TYPE = of("attachable_type");
    public static final ResourceKey<Registry<PartType<?>>> PART = of("multipart/part");

    private static <T> ResourceKey<Registry<T>> of(String id) {
        //noinspection unchecked
        return ResourceKey.createRegistryKey(Chowl.id(id));
    }
}
