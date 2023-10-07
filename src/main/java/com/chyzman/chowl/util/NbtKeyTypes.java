package com.chyzman.chowl.util;

import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

import java.math.BigInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public final class NbtKeyTypes {
    public static final NbtKey.Type<ItemVariant> ITEM_VARIANT = NbtKey.Type.COMPOUND.then(ItemVariant::fromNbt, TransferVariant::toNbt);
    public static final NbtKey.Type<BigInteger> BIG_INTEGER = NbtKey.Type.STRING.then(val -> val.isEmpty() ? BigInteger.ZERO : new BigInteger(val), BigInteger::toString);

    private NbtKeyTypes() {

    }

    public static <T> NbtKey.Type<T> fromFactory(Supplier<T> factory, BiConsumer<T, NbtCompound> deserializer,
                                                 BiConsumer<T, NbtCompound> serializer) {
        return NbtKey.Type.COMPOUND.then(tag -> {
            T instance = factory.get();
            deserializer.accept(instance, tag);
            return instance;
        }, instance -> {
            NbtCompound tag = new NbtCompound();
            serializer.accept(instance, tag);
            return tag;
        });
    }
}
