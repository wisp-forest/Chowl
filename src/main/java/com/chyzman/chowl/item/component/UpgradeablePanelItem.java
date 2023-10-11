package com.chyzman.chowl.item.component;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public interface UpgradeablePanelItem extends PanelItem {
    List<ItemStack> upgrades(ItemStack stack);
    void setUpgrades(ItemStack stack, List<ItemStack> upgrades);

    default boolean hasUpgrade(ItemStack stack, Predicate<ItemStack> upgrade) {
        for (var upgradeStack : upgrades(stack)) {
            if (upgrade.test(upgradeStack)) {
                return true;
            }
        }
        return false;
    }
}