package com.chyzman.chowl.industries.item;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.block.DrawerFrameSideState;
import com.chyzman.chowl.industries.block.button.BlockButton;
import com.chyzman.chowl.industries.item.component.*;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.registry.ChowlStats;
import com.chyzman.chowl.industries.transfer.BigSingleSlotStorage;
import com.chyzman.chowl.industries.transfer.PanelStorage;
import com.chyzman.chowl.industries.transfer.PanelStorageContext;
import com.chyzman.chowl.industries.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.industries.Chowl.*;

public class PackingPanelItem extends BasePanelItem implements PanelItem, DisplayingPanelItem, UpgradeablePanelItem, CapacityLimitedPanelItem {
    public PackingPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);
        if (items.entries().isEmpty()) {
            return new Storage(ctx, Items.AIR);
        } else {
            List<Storage> storages = new ArrayList<>(items.entries().size());

            for (Item item : items.entries().keySet()) storages.add(new Storage(ctx, item));

            return new CombinedSlottedStorage<>(storages);
        }
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return listStorageButtons(drawerFrame, side, stack);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public boolean hasComparatorOutput() {
        return true;
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity());
    }

    private class Storage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        private final Item item;

        public Storage(PanelStorageContext ctx, Item item) {
            super(ctx);
            this.item = item;
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);

            var currentCount = items.totalCount();
            var capacity = PackingPanelItem.this.capacity(ctx.stack());
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            updateSnapshots(transaction);
            ctx.stack().set(ChowlComponents.BARE_ITEMS, items.copyAndInsert(resource.getItem(), inserted));

            if (PackingPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!VariantUtils.isFireproof(resource) && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var items = ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT);

            var currentCount = items.entries().getOrDefault(resource.getItem(), BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().set(ChowlComponents.BARE_ITEMS, items.copyAndSet(resource.getItem(), newCount));

            if (items.entries().isEmpty()) {
                needsEmptiedEvent = true;
            }
            return removed;
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(item);
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOrDefault(ChowlComponents.BARE_ITEMS, BareItemsComponent.DEFAULT).entries().getOrDefault(item, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return PackingPanelItem.this.capacity(ctx.stack());
        }
    }
}
