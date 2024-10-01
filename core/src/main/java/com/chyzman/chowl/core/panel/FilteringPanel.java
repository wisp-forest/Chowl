package com.chyzman.chowl.core.panel;

import net.minecraft.item.ItemStack;

public interface FilteringPanel<T> {
    boolean isPartOfFilter(ItemStack panel, T target);

    boolean canAddToFilter(ItemStack panel, T target);

    void setFilter(ItemStack panel, T newFilter);
}
