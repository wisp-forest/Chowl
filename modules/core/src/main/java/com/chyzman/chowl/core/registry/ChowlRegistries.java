package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.attachables.api.AttachableType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public class ChowlRegistries {
    public static final Registry<AttachableType<?>> ATTACHABLE_TYPE = register("attachable_type", true);
    public static final RegistryKey<Registry<AttachableType<?>>> ATTACHABLE_TYPE_KEY = of("attachable_type");

    private static<T> Registry<T> register(String id, boolean intrusive) {
        return FabricRegistryBuilder.from(new SimpleRegistry<T>(RegistryKey.ofRegistry(Chowl.id(id)), Lifecycle.stable(), intrusive)).buildAndRegister();
    }

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(Chowl.id(id));
    }

    public static void init() {}
}
