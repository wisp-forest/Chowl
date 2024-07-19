package com.chyzman.chowl.industries.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.DataComponentTypes;

@SuppressWarnings("UnstableApiUsage")
public final class VariantUtils {
    private VariantUtils() {

    }

    public static boolean hasNbt(ItemVariant variant) {
        return !variant.getComponents().isEmpty();
    }

    public static boolean isFireproof(ItemVariant variant) {
        ComponentMap map = ComponentMapImpl.create(variant.getItem().getComponents(), variant.getComponents());
        return map.contains(DataComponentTypes.FIRE_RESISTANT);
    }
}
