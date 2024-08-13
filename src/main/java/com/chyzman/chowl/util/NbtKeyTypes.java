package com.chyzman.chowl.util;

import io.wispforest.owo.serialization.*;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

import java.math.BigInteger;

public final class NbtKeyTypes {
    public static final Endec<ItemVariant> ITEM_VARIANT = NbtEndec.COMPOUND.xmap(ItemVariant::fromNbt, TransferVariant::toNbt);
    public static final Endec<BigInteger> BIG_INTEGER = switchSelfDescribing(
        Endec.STRING.xmap(str -> {
            if (str.isEmpty()) return BigInteger.ZERO;

            return new BigInteger(str);
        }, BigInteger::toString),
        Endec.BYTES.xmap(BigInteger::new, BigInteger::toByteArray)
    );

    private NbtKeyTypes() {

    }

    public static <T> Endec<T> switchSelfDescribing(Endec<T> selfDescribing, Endec<T> generic) {
        return new Endec<>() {
            @Override
            public void encode(Serializer<?> serializer, T value) {
                if (serializer.attributes().contains(SerializationAttribute.SELF_DESCRIBING)) {
                    selfDescribing.encode(serializer, value);
                } else {
                    generic.encode(serializer, value);
                }
            }

            @Override
            public T decode(Deserializer<?> deserializer) {
                if (deserializer instanceof SelfDescribedDeserializer<?>) {
                    return selfDescribing.decode(deserializer);
                } else {
                    return generic.decode(deserializer);
                }
            }
        };
    }
}