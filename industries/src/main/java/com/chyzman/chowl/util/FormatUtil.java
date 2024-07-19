package com.chyzman.chowl.util;

import com.google.common.math.BigIntegerMath;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;
import java.math.RoundingMode;

import static com.chyzman.chowl.Chowl.CHOWL_CONFIG;

public final class FormatUtil {

    public static String formatCount(BigInteger count) {
        var digits = BigInteger.valueOf(count.toString().length());

        if (digits.compareTo(new BigInteger(CHOWL_CONFIG.max_digits_before_exponents())) > 0)
            return "2^" + (BigIntegerMath.log2(count, RoundingMode.HALF_UP));
        else
            return count.toString();
    }
}