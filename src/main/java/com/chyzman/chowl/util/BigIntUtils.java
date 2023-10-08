package com.chyzman.chowl.util;

import java.math.BigInteger;

public final class BigIntUtils {
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

    public static BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }
}