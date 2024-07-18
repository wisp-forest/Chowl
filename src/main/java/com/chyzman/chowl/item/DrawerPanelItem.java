package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.BigSingleSlotStorage;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

import static com.chyzman.chowl.Chowl.*;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, UpgradeablePanelItem, StoragePanelItem {
    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        return new Storage(ctx);
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return List.of(STORAGE_BUTTON);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        return stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.set(ChowlRegistry.CONTAINED_ITEM_VARIANT, newFilter);
        stack.set(ChowlRegistry.LOCKED, !newFilter.equals(ItemVariant.blank()));
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.LOCKED, false);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.set(ChowlRegistry.LOCKED, locked);

        if (!locked && stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO).equals(BigInteger.ZERO)) {
            stack.set(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());
        }
    }

    @Override
    public boolean hasComparatorOutput() {
        return true;
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity());
    }

    @Override
    public BigInteger count(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
    }

    @Override
    public void setCount(ItemStack stack, BigInteger count) {
        stack.set(ChowlRegistry.COUNT, count);
    }

    @SuppressWarnings("UnstableApiUsage")
    private class Storage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        public Storage(PanelStorageContext ctx) {
            super(ctx);
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            var contained = ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());

            if (contained.isBlank()) contained = resource;
            if (!contained.equals(resource)) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
            var capacity = DrawerPanelItem.this.capacity(ctx.stack());
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            updateSnapshots(transaction);
            ctx.stack().set(ChowlRegistry.CONTAINED_ITEM_VARIANT, contained);
            ctx.stack().set(ChowlRegistry.COUNT, currentCount.add(inserted));

            ItemVariant finalContained = contained;

            if (DrawerPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!VariantUtils.isFireproof(finalContained) && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            var contained = ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());

            if (contained.isBlank()) return BigInteger.ZERO;
            if (!contained.equals(resource)) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().set(ChowlRegistry.COUNT, newCount);

            if (newCount.compareTo(BigInteger.ZERO) <= 0) {
                if (!ctx.stack().getOrDefault(ChowlRegistry.LOCKED, false)) {
                    ctx.stack().set(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());
                }

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
            return ctx.stack().getOrDefault(ChowlRegistry.CONTAINED_ITEM_VARIANT, ItemVariant.blank());
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOrDefault(ChowlRegistry.COUNT, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return DrawerPanelItem.this.capacity(ctx.stack());
        }
    }
}