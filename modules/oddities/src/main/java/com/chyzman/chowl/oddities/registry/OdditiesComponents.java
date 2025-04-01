package com.chyzman.chowl.oddities.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class OdditiesComponents implements AutoRegistryContainer<ComponentType<?>> {

    public static final ComponentType<Text> CLIPBOARD_CONTENT = ComponentType.<Text>builder()
            .endec(MinecraftEndecs.TEXT)
            .build();

    @Override
    public Registry<ComponentType<?>> getRegistry() {
        return Registries.DATA_COMPONENT_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ComponentType<?>> getTargetFieldType() {
        return (Class<ComponentType<?>>) (Object) ComponentType.class;
    }
}
