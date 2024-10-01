package com.chyzman.chowl.core.panel;

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface PanelItem<T> {

    default @Nullable SlottedStorage<T> getStorage(ItemStack panel) {
        return null;
    }

    default @Nullable SlottedStorage<T> getNetworkStorage(ItemStack panel) {
        return getStorage(panel);
    }
}
