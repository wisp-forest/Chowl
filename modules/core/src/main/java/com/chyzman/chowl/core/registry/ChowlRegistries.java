package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ChowlRegistries {
    public static final Registry<AttachableType<?>> ATTACHABLE_TYPE = createIntrusive(ChowlRegistryKeys.ATTACHABLE_TYPE);
    public static final DefaultedRegistry<PartType<?>> PART = createIntrusive(ChowlRegistryKeys.PART, "empty");

    private static <T> Registry<T> create(ResourceKey<? extends Registry<T>> key) {
        return new MappedRegistry<>(key, Lifecycle.stable(), false);
    }

    private static <T> Registry<T> createIntrusive(ResourceKey<? extends Registry<T>> key) {
        return new MappedRegistry<>(key, Lifecycle.stable(), true);
    }

    private static <T> DefaultedRegistry<T> create(ResourceKey<? extends Registry<T>> key, String defaultId) {
        return new DefaultedMappedRegistry<>(defaultId, key, Lifecycle.stable(), false);
    }

    private static <T> DefaultedRegistry<T> createIntrusive(ResourceKey<? extends Registry<T>> key, String defaultId) {
        return new DefaultedMappedRegistry<>(defaultId, key, Lifecycle.stable(), true);
    }

    public static void init() {}
}
