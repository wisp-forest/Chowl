package com.chyzman.chowl.item.component;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;


@SuppressWarnings("UnstableApiUsage")
public interface FilteringPanelItem extends PanelItem {
    ItemVariant currentFilter(ItemStack stack);
    boolean canSetFilter(ItemStack stack, ItemVariant to);
    void setFilter(ItemStack stack, ItemVariant newFilter);
}
