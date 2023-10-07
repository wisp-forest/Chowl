package com.chyzman.chowl.item.component;

import net.minecraft.item.ItemStack;

public interface LockablePanelItem extends PanelItem {
    boolean locked(ItemStack stack);
    void setLocked(ItemStack stack, boolean locked);
}
