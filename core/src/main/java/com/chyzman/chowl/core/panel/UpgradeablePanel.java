package com.chyzman.chowl.core.panel;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface UpgradeablePanel {
    ComponentType<UpgradeListComponent> UPGRADE_LIST = ComponentType.<UpgradeListComponent>builder()
            .endec(UpgradeListComponent.ENDEC)
            .build();

    default UpgradeListComponent upgrades(ItemStack stack) {
        return stack.getOrDefault(UPGRADE_LIST, UpgradeListComponent.DEFAULT);
    }

    default void modifyUpgrades(ItemStack panel, UnaryOperator<List<ItemStack>> modifier) {
        var stacks = upgrades(panel).copyStacks();
        stacks = modifier.apply(stacks);
        panel.set(UPGRADE_LIST, new UpgradeListComponent(Collections.unmodifiableList(stacks)));
    }

    default boolean hasUpgrade(ItemStack panel, Predicate<ItemStack> upgrade) {
        for (var upgradeStack : upgrades(panel).upgradeStacks()) {
            if (upgrade.test(upgradeStack)) {
                return true;
            }
        }
        return false;
    }
}
