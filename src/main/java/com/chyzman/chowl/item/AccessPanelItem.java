package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.transfer.TransferState;
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
public class AccessPanelItem extends BasePanelItem implements DisplayingPanelItem {
    public AccessPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        List<SingleSlotStorage<ItemVariant>> storages = new ArrayList<>();

        try {
            if (TransferState.DOUBLE_CLICK_INSERT.get())
                TransferState.NO_BLANK_DRAWERS.set(true);
            if (!ctx.traverseNetwork(storage -> storages.addAll(storage.getSlots())))
                return null;
        } finally {
            TransferState.NO_BLANK_DRAWERS.set(false);
        }

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
    public boolean hasDisplay() {
        return false;
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public boolean canExtractFromButton() {
        return false;
    }
}