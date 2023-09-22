package com.chyzman.chowl.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;

import java.math.BigInteger;

public abstract class DrawerPanel extends Item {
    public DrawerComponent component = new DrawerComponent();

    public DrawerPanel(Settings settings) {
        super(settings);
    }

    public void insert(ItemVariant itemVariant, BigInteger count) {
        if (component.itemVariant.isBlank()) {
            component.itemVariant = itemVariant;
        }
        if (component.itemVariant.equals(itemVariant)) {
            component.count = component.count.add(count);
        }
    }

    public void extract(BigInteger count) {
        component.count = component.count.subtract(count);
        if (component.count.equals(BigInteger.ZERO)) {
            component.itemVariant = ItemVariant.blank();
        }
    }
}