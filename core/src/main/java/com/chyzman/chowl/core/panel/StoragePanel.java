package com.chyzman.chowl.core.panel;

import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.core.util.ChowlEndecs;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

public interface StoragePanel<T> extends StorageExposingPanel<T> {
    ComponentType<BigInteger> CAPACITY = ComponentType.<BigInteger>builder()
            .endec(ChowlEndecs.BIG_INTEGER)
            .build();

    BigInteger baseCapacity();

    static BigInteger capacityTier(ItemStack panel) {
        return panel.getOrDefault(CAPACITY, BigInteger.ZERO);
    }
}
