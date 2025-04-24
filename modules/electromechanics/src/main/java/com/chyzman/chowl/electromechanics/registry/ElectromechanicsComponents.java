package com.chyzman.chowl.electromechanics.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ElectromechanicsComponents implements AutoRegistryContainer<ComponentType<?>> {

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
