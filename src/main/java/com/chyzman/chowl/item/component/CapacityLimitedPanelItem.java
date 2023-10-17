package com.chyzman.chowl.item.component;

import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.NbtKeyTypes;
import com.google.common.math.BigIntegerMath;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;
import java.math.RoundingMode;

import static com.chyzman.chowl.Chowl.*;

public interface CapacityLimitedPanelItem extends PanelItem {
    NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);

    BigInteger baseCapacity();

    default String formattedCapacity(ItemStack stack) {
        var digits = BigIntUtils.decimalDigitsLog2(capacityTier(stack));

        if (digits.compareTo(new BigInteger(CHOWL_CONFIG.max_digits_before_exponents())) > 0)
            return "2^" + (capacityTier(stack).add(BigInteger.valueOf(BigIntegerMath.log2(baseCapacity(), RoundingMode.HALF_UP))));
        else
            return capacity(stack).toString();
    }

    default BigInteger capacity(ItemStack stack) {
        return BigIntUtils.powOf2(baseCapacity(), capacityTier(stack));
    }

    static BigInteger capacityTier(ItemStack stack) {
            return stack.get(CAPACITY);
    }
}