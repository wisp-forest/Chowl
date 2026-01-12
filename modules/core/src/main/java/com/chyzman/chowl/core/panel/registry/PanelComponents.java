package com.chyzman.chowl.core.panel.registry;

import com.chyzman.chowl.core.util.ChowlEndecs;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;

public class PanelComponents implements AutoRegistryContainer<DataComponentType<?>> {

    public static final DataComponentType<BigInteger> COUNT = DataComponentType.<BigInteger>builder()
        .endec(ChowlEndecs.BIG_INTEGER)
        .build();

    //NOTE: the value stored here is x where the actual capacity is (base * 2^x)
    //this might not need to be a big integer but yknow
    public static final DataComponentType<BigInteger> CAPACITY = DataComponentType.<BigInteger>builder()
        .endec(ChowlEndecs.BIG_INTEGER)
        .build();

    public static final DataComponentType<Boolean> LOCKED = DataComponentType.<Boolean>builder()
        .endec(Endec.BOOLEAN)
        .build();

    public static final DataComponentType<ItemVariant> ITEM_FILTER = DataComponentType.<ItemVariant>builder()
        .persistent(ItemVariant.CODEC)
        .networkSynchronized(ItemVariant.PACKET_CODEC)
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
