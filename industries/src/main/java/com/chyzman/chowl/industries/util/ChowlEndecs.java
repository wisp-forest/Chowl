package com.chyzman.chowl.industries.util;

import io.wispforest.endec.Endec;

import java.math.BigInteger;

public final class ChowlEndecs {
    // TODO: add support for byte encoding of big integers.
    public static final Endec<BigInteger> BIG_INTEGER = Endec.STRING.xmap(str -> {
        if (str.isEmpty()) return BigInteger.ZERO;

        return new BigInteger(str);
    }, BigInteger::toString);

    private ChowlEndecs() {

    }
}