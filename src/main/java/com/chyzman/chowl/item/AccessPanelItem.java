package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.transfer.PanelStorageContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AccessPanelItem extends BasePanelItem implements UpgradeablePanelItem {
    public AccessPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        List<SingleSlotStorage<ItemVariant>> storages = new ArrayList<>();

        if (!ctx.traverseNetwork(storage -> storages.addAll(storage.getSlots())))
            return null;

        storages.sort(Comparator.comparing(x -> -x.getAmount()));

        return new CombinedSlottedStorage<>(storages);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getNetworkStorage(PanelStorageContext ctx) {
        return null;
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return Collections.singletonList(STORAGE_BUTTON);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public Config defaultConfig() {
        Config cfg = new Config();

        cfg.hideCapacity(true);
        cfg.hideCount(true);
        cfg.hideName(true);

        return cfg;
    }

    @Override
    public boolean supportsHideItem() {
        return false;
    }

    @Override
    public boolean supportsHideName() {
        return false;
    }

    @Override
    public boolean canExtractFromButton() {
        return false;
    }
}