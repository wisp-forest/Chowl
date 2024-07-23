package com.chyzman.chowl.industries.item.component;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;


public interface FilteringPanelItem extends PanelItem {
    ItemVariant currentFilter(ItemStack stack);
    boolean canSetFilter(ItemStack stack, ItemVariant to);
    void setFilter(ItemStack stack, ItemVariant newFilter);
}
