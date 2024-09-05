package com.chyzman.chowl.industries.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigIntUtils {
    private static final LoadingCache<Pair<BigInteger, BigInteger>, BigInteger> CAPACITY_CACHE = CacheBuilder.newBuilder()
        .concurrencyLevel(1)
        .maximumSize(200)
        .build(CacheLoader.from(input ->  BigIntUtils.pow(BigInteger.TWO, input.getLeft()).multiply(input.getRight())));

    private static final BigDecimal LOG10_OF_2 = BigDecimal.valueOf(Math.log10(2));

    private BigIntUtils() {

    }

    public static long longValueSaturating(BigInteger big) {
        try {
            return big.longValueExact();
        } catch (ArithmeticException e) {
            if (big.signum() > 0) {
                return Long.MAX_VALUE;
            } else {
                return Long.MIN_VALUE;
            }
        }
    }

    public static BigInteger powOf2(BigInteger multiplier, BigInteger exponent) {
        return CAPACITY_CACHE.getUnchecked(new Pair<>(exponent, multiplier));
    }

    public static BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    public static int decimalDigits(BigInteger bigInteger) {
        return bigInteger.abs().toString().length();
    }
}
