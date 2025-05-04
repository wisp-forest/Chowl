package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.multipart.api.PartType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ChowlRegistryKeys {
    public static final RegistryKey<Registry<AttachableType<?>>> ATTACHABLE_TYPE = of("attachable_type");
    public static final RegistryKey<Registry<PartType<?>>> PART = of("multipart/part");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        //noinspection unchecked
        return RegistryKey.ofRegistry(Chowl.id(id));
    }
}
