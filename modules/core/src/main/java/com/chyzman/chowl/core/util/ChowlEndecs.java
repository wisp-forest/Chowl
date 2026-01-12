package com.chyzman.chowl.core.util;

import io.wispforest.endec.*;
import net.minecraft.core.FrontAndTop;

import java.math.BigInteger;

public class ChowlEndecs {
    public static final Endec<FrontAndTop> FRONT_AND_TOP = Endec.forEnum(FrontAndTop.class);

    public static final Endec<BigInteger> BIG_INTEGER = switchSelfDescribing(
        Endec.STRING.xmap(str -> str.isEmpty() ? BigInteger.ZERO : new BigInteger(str), BigInteger::toString),
        Endec.BYTES.xmap(BigInteger::new, BigInteger::toByteArray)
    );

    public static <T> Endec<T> switchSelfDescribing(Endec<T> selfDescribing, Endec<T> generic) {
        return new Endec<>() {
            @Override
            public void encode(SerializationContext ctx, Serializer<?> serializer, T value) {
                (serializer instanceof SelfDescribedSerializer<?> ? selfDescribing : generic).encode(ctx, serializer, value);
            }

            @Override
            public T decode(SerializationContext ctx, Deserializer<?> deserializer) {
                return (deserializer instanceof SelfDescribedDeserializer<?> ? selfDescribing : generic).decode(ctx, deserializer);
            }
        };
    }
}
