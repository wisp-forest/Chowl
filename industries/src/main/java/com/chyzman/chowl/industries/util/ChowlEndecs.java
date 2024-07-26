package com.chyzman.chowl.industries.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.RegistriesAttribute;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;

import java.math.BigInteger;

public final class ChowlEndecs {
    // TODO: add support for byte encoding of big integers.
    public static final Endec<BigInteger> BIG_INTEGER = Endec.STRING.xmap(str -> {
        if (str.isEmpty()) return BigInteger.ZERO;

        return new BigInteger(str);
    }, BigInteger::toString);

    public static final Endec<ItemStack> ITEM_STACK = toEndecViaNbt(ItemStack.OPTIONAL_CODEC);

    public static <T> Endec<T> toEndecViaNbt(Codec<T> codec) {
        return Endec.of(
            (ctx, serializer, value) -> {
                DynamicOps<NbtElement> ops = NbtOps.INSTANCE;
                if (ctx.hasAttribute(RegistriesAttribute.REGISTRIES)) {
                    ops = RegistryOps.of(ops, ctx.getAttributeValue(RegistriesAttribute.REGISTRIES).infoGetter());
                }

                NbtEndec.ELEMENT.encode(ctx, serializer, codec.encodeStart(ops, value).getOrThrow(IllegalStateException::new));
            },
            (ctx, deserializer) -> {
                DynamicOps<NbtElement> ops = NbtOps.INSTANCE;
                if (ctx.hasAttribute(RegistriesAttribute.REGISTRIES)) {
                    ops = RegistryOps.of(ops, ctx.getAttributeValue(RegistriesAttribute.REGISTRIES).infoGetter());
                }

                return codec.parse(ops, NbtEndec.ELEMENT.decode(ctx, deserializer)).getOrThrow(IllegalStateException::new);
            }
        );
    }

    private ChowlEndecs() {

    }
}