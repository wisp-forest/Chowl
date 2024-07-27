package com.chyzman.chowl.core.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ChowlCoreComponents implements AutoRegistryContainer<ComponentType<?>> {

    public static final ComponentType<BlockState> TEMPLATE_STATE = ComponentType.<BlockState>builder()
        .codec(BlockState.CODEC)
        .packetCodec(PacketCodecs.entryOf(Block.STATE_IDS))
        .build();

    public static final ComponentType<BlockState> TEMPLATE_MODEL_STATE = ComponentType.<BlockState>builder()
            .codec(BlockState.CODEC)
            .packetCodec(PacketCodecs.entryOf(Block.STATE_IDS))
            .build();

    @Override
    public Registry<ComponentType<?>> getRegistry() {
        return Registries.DATA_COMPONENT_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ComponentType<?>> getTargetFieldType() {
        return (Class<ComponentType<?>>)(Object) ComponentType.class;
    }
}
