package com.chyzman.chowl.item.component;

import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

import static com.chyzman.chowl.Chowl.CHOWL_CONFIG;
import static com.chyzman.chowl.Chowl.POWER_CACHE;

public interface CapacityLimitedPanelItem extends PanelItem {
    NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);

    BigInteger baseCapacity();

    default String formattedCapacity(ItemStack stack) {
        BigInteger capacity = capacityTier(stack);
        if (capacity.compareTo(BigInteger.valueOf(CHOWL_CONFIG.max_capacity_level_before_exponents())) > 0)
            return "2^" + stack.get(CAPACITY).add(BigInteger.valueOf(11));
        else
            return capacity(stack).toString();
    }

    default BigInteger capacity(ItemStack stack) {
        return baseCapacity().multiply(POWER_CACHE.getUnchecked(capacityTier(stack)));
    }

    static BigInteger capacityTier(ItemStack stack) {
        return stack.get(CAPACITY).min(BigInteger.valueOf(100000000));
    }
}
