package com.chyzman.chowl.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

@SuppressWarnings("UnstableApiUsage")
public final class VariantUtils {
    private VariantUtils() {

    }

    public static boolean hasNbt(ItemVariant variant) {
        return variant.getNbt() != null && !variant.getNbt().isEmpty();
    }
}
