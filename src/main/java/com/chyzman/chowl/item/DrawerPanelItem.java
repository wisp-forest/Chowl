package com.chyzman.chowl.item;

import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;

import java.math.BigInteger;

public class DrawerPanelItem extends Item {
    public static final NbtKey<DrawerComponent> COMPONENT = new NbtKey<>("DrawerComponent", DrawerComponent.KEY_TYPE);

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public void insert(ItemStack stack, ItemStack inserted) {
        var component = stack.get(COMPONENT);
        inserted.setCount(component.insert(inserted));
        stack.put(COMPONENT, component);
    }

    public ItemStack extract(ItemStack stack, boolean sneaking) {
        var component = stack.get(COMPONENT);
        var amount = sneaking ? component.itemVariant.getItem().getMaxCount() : 1;
        var returned = component.extract(amount);

        if (returned.isEmpty()) return returned;

        stack.put(COMPONENT, component);
        return returned;
    }
}