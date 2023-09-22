package com.chyzman.chowl.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.NbtCompound;

import java.math.BigInteger;

public class DrawerComponent {
    public ItemVariant itemVariant = ItemVariant.blank();
    public BigInteger count = BigInteger.ZERO;

    public DrawerComponent() {
    }

    public DrawerComponent(ItemVariant itemVariant, BigInteger count) {
        this.itemVariant = itemVariant;
        this.count = count;
    }

    public DrawerComponent fromNbt(NbtCompound nbt) {
        return new DrawerComponent(
                ItemVariant.fromNbt(nbt.getCompound("Variant")),
                new BigInteger(nbt.getString("Count"))
        );
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Variant", itemVariant.toNbt());
        nbt.putString("Count", count.toString());
        return nbt;
    }
}