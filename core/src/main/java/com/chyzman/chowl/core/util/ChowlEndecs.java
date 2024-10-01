package com.chyzman.chowl.core.util;

import io.wispforest.endec.*;

import java.math.BigInteger;

public final class ChowlEndecs {
    public static final Endec<BigInteger> BIG_INTEGER = switchSelfDescribing(
        Endec.STRING.xmap(str -> {
            if (str.isEmpty()) return BigInteger.ZERO;

            return new BigInteger(str);
        }, BigInteger::toString),
        Endec.BYTES.xmap(BigInteger::new, BigInteger::toByteArray)
    );

    public static <T> Endec<T> switchSelfDescribing(Endec<T> selfDescribing, Endec<T> generic) {
        return new Endec<>() {
            @Override
            public void encode(SerializationContext ctx, Serializer<?> serializer, T value) {
                if (serializer instanceof SelfDescribedSerializer<?>) {
                    selfDescribing.encode(ctx, serializer, value);
                } else {
                    generic.encode(ctx, serializer, value);
                }
            }

            @Override
            public T decode(SerializationContext ctx, Deserializer<?> deserializer) {
                if (deserializer instanceof SelfDescribedDeserializer<?>) {
                    return selfDescribing.decode(ctx, deserializer);
                } else {
                    return generic.decode(ctx, deserializer);
                }
            }
        };
    }

    private ChowlEndecs() {

    }
}
