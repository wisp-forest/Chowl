package com.chyzman.chowl.core.panel.registry;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PanelComponents implements AutoRegistryContainer<DataComponentType<?>> {

    public static final DataComponentType<Boolean> LOCKED = DataComponentType.<Boolean>builder()
        .endec(Endec.BOOLEAN)
        .build();

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
