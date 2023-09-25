package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

@SuppressWarnings("UnstableApiUsage")
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

    public BigInteger getCount(ItemStack stack) {
        var component = stack.get(COMPONENT);
        return component.count;
    }

    public ItemVariant getVariant(ItemStack stack) {
        var component = stack.get(COMPONENT);
        return component.itemVariant;
    }

    @SuppressWarnings("UnstableApiUsage")
    public Storage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity) {
        return new DrawerPanelStorage(stack, blockEntity);
    }
}