package com.chyzman.chowl.item.component;

import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unchecked")
public interface DrawerLockHolder<D extends DrawerLockHolder<D>> {
    NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);

    default D locked(ItemStack stack, boolean locked) {
        stack.put(LOCKED, locked);
        return (D) this;
    }

    default boolean locked(ItemStack stack) {
        return stack.get(LOCKED);
    }
}