package com.chyzman.chowl.industries.item.component;

import net.minecraft.item.ItemStack;

import java.math.BigInteger;

public interface StoragePanelItem extends CapacityLimitedPanelItem, FilteringPanelItem, UpgradeablePanelItem {
    BigInteger count(ItemStack stack);
    default BigInteger fullCapacity(ItemStack stack) {
        return capacity(stack);
    }

    void setCount(ItemStack stack, BigInteger count);
}
