package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.*;

public class ChowlRegistries {
    public static final Registry<AttachableType<?>> ATTACHABLE_TYPE = createIntrusive(ChowlRegistryKeys.ATTACHABLE_TYPE);
    public static final DefaultedRegistry<PartType<?>> PART = createIntrusive(ChowlRegistryKeys.PART, "empty");

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key) {
        return new SimpleRegistry<>(key, Lifecycle.stable(), false);
    }

    private static <T> Registry<T> createIntrusive(RegistryKey<? extends Registry<T>> key) {
        return new SimpleRegistry<>(key, Lifecycle.stable(), true);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId) {
        return new SimpleDefaultedRegistry<>(defaultId, key, Lifecycle.stable(), false);
    }

    private static <T> DefaultedRegistry<T> createIntrusive(RegistryKey<? extends Registry<T>> key, String defaultId) {
        return new SimpleDefaultedRegistry<>(defaultId, key, Lifecycle.stable(), true);
    }

    public static void init() {}
}
