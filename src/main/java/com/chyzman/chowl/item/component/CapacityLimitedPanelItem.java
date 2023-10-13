package com.chyzman.chowl.item.component;

import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

import static com.chyzman.chowl.Chowl.POWER_CACHE;

public interface CapacityLimitedPanelItem extends PanelItem {
    NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);

    BigInteger baseCapacity();

    default BigInteger capacity(ItemStack stack) {
        return baseCapacity().multiply(POWER_CACHE.getUnchecked(capacityTier(stack)));
    }

    static BigInteger capacityTier(ItemStack stack) {
        return stack.get(CAPACITY).min(BigInteger.valueOf(100000000));
    }
}
