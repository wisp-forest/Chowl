package com.chyzman.chowl.industries.item.component;

import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.util.BigIntUtils;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

public interface CapacityLimitedPanelItem extends PanelItem {
    BigInteger baseCapacity();

    default BigInteger capacity(ItemStack panel) {
        return BigIntUtils.powOf2(baseCapacity(), capacityTier(panel));
    }

    static BigInteger capacityTier(ItemStack stack) {
        return stack.getOrDefault(ChowlComponents.CAPACITY, BigInteger.ZERO);
    }
}