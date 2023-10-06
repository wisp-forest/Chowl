package com.chyzman.chowl.item.component;

import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.item.ItemStack;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public interface DrawerFilterHolder<D extends DrawerFilterHolder<D>> {
    NbtKey.Type<ItemVariant> ITEM_VARIANT_TYPE = NbtKey.Type.COMPOUND.then(ItemVariant::fromNbt, TransferVariant::toNbt);
    NbtKey<ItemVariant> FILTER = new NbtKey<>("Filter", ITEM_VARIANT_TYPE);

    default D filter(ItemStack stack, ItemVariant variant) {
        stack.put(FILTER, variant);
        return (D) this;
    }

    default ItemVariant filter(ItemStack stack) {
        return stack.get(FILTER);
    }
}