package com.chyzman.chowl.core.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ChowlComponents implements AutoRegistryContainer<DataComponentType<?>> {

    public static final DataComponentType<BlockState> TEMPLATE_STATE = DataComponentType.<BlockState>builder()
        .persistent(BlockState.CODEC)
        .networkSynchronized(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY))
        .build();

    public static final DataComponentType<BlockState> TEMPLATE_MODEL_STATE = DataComponentType.<BlockState>builder()
            .persistent(BlockState.CODEC)
            .networkSynchronized(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY))
            .build();

    @Override
    public Registry<DataComponentType<?>> getRegistry() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<DataComponentType<?>> getTargetFieldType() {
        return (Class<DataComponentType<?>>)(Object) DataComponentType.class;
    }
}
