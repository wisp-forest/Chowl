package com.chyzman.chowl.industries.item;

import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import org.jetbrains.annotations.Nullable;

public class BlankPanelItem extends BasePanelItem {
    public BlankPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        return null;
    }
}