package com.chyzman.chowl.item.component;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface UpgradeablePanelItem extends PanelItem {
    List<ItemStack> upgrades(ItemStack stack);
    void setUpgrades(ItemStack stack, List<ItemStack> upgrades);
}