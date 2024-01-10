package com.chyzman.chowl.item.component;

import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

public interface CapacityLimitedPanelItem extends PanelItem {
    NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);

    BigInteger baseCapacity();

    default BigInteger capacity(ItemStack panel) {
        return BigIntUtils.powOf2(baseCapacity(), capacityTier(panel));
    }

    static BigInteger capacityTier(ItemStack stack) {
        return stack.getOr(CAPACITY, BigInteger.ZERO);
    }
}