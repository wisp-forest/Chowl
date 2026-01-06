package com.chyzman.chowl.electromechanics.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ElectromechanicsComponents implements AutoRegistryContainer<DataComponentType<?>> {

    @Override
    public Registry<DataComponentType<?>> getRegistry() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<DataComponentType<?>> getTargetFieldType() {
        return (Class<DataComponentType<?>>) (Object) DataComponentType.class;
    }
}
