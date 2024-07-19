package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.item.component.DisplayingPanelConfig;
import com.chyzman.chowl.industries.item.component.UpgradeListComponent;
import com.chyzman.chowl.industries.util.ChowlEndecs;
import com.mojang.serialization.Codec;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

import java.math.BigInteger;

public class ChowlComponents implements AutoRegistryContainer<ComponentType<?>> {
    public static final ComponentType<DisplayingPanelConfig> DISPLAYING_CONFIG = ComponentType.<DisplayingPanelConfig>builder()
        .codec(CodecUtils.toCodec(DisplayingPanelConfig.ENDEC))
        .packetCodec(CodecUtils.toPacketCodec(DisplayingPanelConfig.ENDEC))
        .build();

    public static final ComponentType<Item> CONTAINED_ITEM = ComponentType.<Item>builder()
        .codec(Registries.ITEM.getCodec())
        .packetCodec(PacketCodecs.registryValue(RegistryKeys.ITEM))
        .build();

    public static final ComponentType<ItemVariant> ITEM_FILTER = ComponentType.<ItemVariant>builder()
        .codec(ItemVariant.CODEC)
        .packetCodec(ItemVariant.PACKET_CODEC)
        .build();

    public static final ComponentType<ItemVariant> CONTAINED_ITEM_VARIANT = ComponentType.<ItemVariant>builder()
        .codec(ItemVariant.CODEC)
        .packetCodec(ItemVariant.PACKET_CODEC)
        .build();

    public static final ComponentType<BigInteger> COUNT = ComponentType.<BigInteger>builder()
        .codec(CodecUtils.toCodec(ChowlEndecs.BIG_INTEGER))
        .packetCodec(CodecUtils.toPacketCodec(ChowlEndecs.BIG_INTEGER))
        .build();

    public static final ComponentType<BigInteger> CAPACITY = ComponentType.<BigInteger>builder()
        .codec(CodecUtils.toCodec(ChowlEndecs.BIG_INTEGER))
        .packetCodec(CodecUtils.toPacketCodec(ChowlEndecs.BIG_INTEGER))
        .build();

    public static final ComponentType<Boolean> LOCKED = ComponentType.<Boolean>builder()
        .codec(Codec.BOOL)
        .packetCodec(PacketCodecs.BOOL)
        .build();

    public static final ComponentType<UpgradeListComponent> UPGRADE_LIST = ComponentType.<UpgradeListComponent>builder()
        .codec(CodecUtils.toCodec(UpgradeListComponent.ENDEC))
        .packetCodec(CodecUtils.toPacketCodec(UpgradeListComponent.ENDEC))
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
