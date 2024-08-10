package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.industries.block.DrawerFrameSideState;
import com.chyzman.chowl.industries.item.component.BareItemsComponent;
import com.chyzman.chowl.industries.item.component.DisplayingPanelConfig;
import com.chyzman.chowl.industries.item.component.UpgradeListComponent;
import com.chyzman.chowl.industries.util.ChowlEndecs;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Unit;

import java.math.BigInteger;
import java.util.List;

public class ChowlComponents implements AutoRegistryContainer<ComponentType<?>> {
    public static final ComponentType<DisplayingPanelConfig> DISPLAYING_CONFIG = ComponentType.<DisplayingPanelConfig>builder()
        .endec(DisplayingPanelConfig.ENDEC)
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
        .endec(ChowlEndecs.BIG_INTEGER)
        .build();

    public static final ComponentType<BigInteger> CAPACITY = ComponentType.<BigInteger>builder()
        .endec(ChowlEndecs.BIG_INTEGER)
        .build();

    public static final ComponentType<Unit> LOCKED = ComponentType.<Unit>builder()
        .codec(Unit.CODEC)
        .packetCodec(PacketCodec.unit(Unit.INSTANCE))
        .build();

    public static final ComponentType<UpgradeListComponent> UPGRADE_LIST = ComponentType.<UpgradeListComponent>builder()
        .endec(UpgradeListComponent.ENDEC)
        .build();

    public static final ComponentType<List<DrawerFrameSideState>> DRAWER_FRAME_SIDES = ComponentType.<List<DrawerFrameSideState>>builder()
        .endec(DrawerFrameSideState.LIST_ENDEC)
        .build();

    public static final ComponentType<BareItemsComponent> BARE_ITEMS = ComponentType.<BareItemsComponent>builder()
        .endec(BareItemsComponent.ENDEC)
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
