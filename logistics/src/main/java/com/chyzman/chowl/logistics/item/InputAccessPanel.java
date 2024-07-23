package com.chyzman.chowl.logistics.item;

import com.chyzman.chowl.industries.item.BasePanelItem;
import com.chyzman.chowl.industries.registry.ChowlBlocks;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import org.jetbrains.annotations.Nullable;

public class InputAccessPanel extends BasePanelItem {
    public InputAccessPanel(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        return null;
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getNetworkStorage(PanelStorageContext ctx) {
        var targetPos = ctx.drawerFrame().getPos().offset(ctx.frameSide());

        if (ctx.world().getBlockState(targetPos).isOf(ChowlBlocks.DRAWER_FRAME)) {
            return null;
        }

        var storage = ItemStorage.SIDED.find(ctx.world(), targetPos, ctx.frameSide().getOpposite());

        if (storage instanceof SlottedStorage<ItemVariant> slotted) return slotted;
        else return null;
    }
}
