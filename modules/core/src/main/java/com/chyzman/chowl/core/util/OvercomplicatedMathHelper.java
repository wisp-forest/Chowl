package com.chyzman.chowl.core.util;

import com.google.common.util.concurrent.AtomicDouble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OvercomplicatedMathHelper {

    public static @Nullable Integer intDigits(@NotNull Number number) {
        if (
                number instanceof Long ||
                number instanceof Integer ||
                number instanceof Short ||
                number instanceof Byte ||
                number instanceof AtomicInteger ||
                number instanceof AtomicLong ||
                (number instanceof BigInteger && ((BigInteger) number).bitLength() < 64)
        ) {
            return String.valueOf(Math.abs(number.longValue())).length();
        } else if (
                number instanceof Double ||
                number instanceof Float ||
                number instanceof AtomicDouble
        ) {
            var thisDouble = Double.valueOf(number.doubleValue());
            return thisDouble.isInfinite() || thisDouble.isNaN() ? null : intDigits(thisDouble.longValue());
        }else if (
                number instanceof BigDecimal bigDecimal
        ) {
            return intDigits(bigDecimal.toBigInteger());
        } else if (
                number instanceof BigInteger bigInteger
        ) {
            return intDigits(bigInteger);
        }  else {
            return intDigits(number.longValue());
        }
    }

    public static @NotNull Integer intDigits(@NotNull BigInteger bigInteger) {
        return bigInteger.abs().toString().length();
    }

}
