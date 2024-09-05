package com.chyzman.chowl.industries.item;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.block.DrawerFrameSideState;
import com.chyzman.chowl.industries.block.button.BlockButton;
import com.chyzman.chowl.industries.item.component.*;
import com.chyzman.chowl.industries.transfer.*;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import com.chyzman.chowl.industries.registry.ChowlStats;
import com.chyzman.chowl.industries.util.CompressionManager;
import com.chyzman.chowl.industries.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.component.DataComponentTypes;
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

public class CompressingPanelItem extends BasePanelItem implements FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, StoragePanelItem, UpgradeablePanelItem {
    public CompressingPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return ItemVariant.of(stack.getOrDefault(ChowlComponents.CONTAINED_ITEM, Items.AIR));
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (!to.getComponents().isEmpty()) return false;

        return stack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        var baseNew = CompressionManager.followDown(newFilter.getItem()).item();

        stack.set(ChowlComponents.CONTAINED_ITEM, baseNew);

        if (baseNew != Items.AIR)
            stack.set(ChowlComponents.LOCKED, Unit.INSTANCE);
        else
            stack.remove(ChowlComponents.LOCKED);
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.contains(ChowlComponents.LOCKED);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        if (locked)
            stack.set(ChowlComponents.LOCKED, Unit.INSTANCE);
        else
            stack.remove(ChowlComponents.LOCKED);

        if (!locked && stack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO).equals(BigInteger.ZERO)) {
            stack.set(ChowlComponents.CONTAINED_ITEM, Items.AIR);
        }
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return listStorageButtons(drawerFrame, side, stack);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        var storages = new ArrayList<SlottedStorage<ItemVariant>>();
        var base = new BaseStorage(ctx);

        int steps = CompressionManager.followUp(base.getResource().getItem()).totalSteps();
        if (steps == 0) {
            storages.add(new InitialCompressingStorage(base));
        } else {
            storages.add(base);

            for (int i = 0; i < steps; i++) {
                storages.add(new CompressingStorage(base, i + 1));
            }
        }

        return new CombinedSlottedStorage<>(storages);
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
        return new BigInteger(CHOWL_CONFIG.base_capacity.compressing());
    }

    @Override
    public BigInteger capacity(ItemStack panel) {
        return StoragePanelItem.super.capacity(panel);
    }

    @Override
    public BigInteger fullCapacity(ItemStack stack) {
        return capacity(stack).multiply(CompressionManager.followUp(stack.getOrDefault(ChowlComponents.CONTAINED_ITEM, Items.AIR)).totalMultiplier());
    }

    @Override
    public BigInteger count(ItemStack stack) {
        return stack.getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO);
    }

    @Override
    public void setCount(ItemStack stack, BigInteger count) {
        stack.set(ChowlComponents.COUNT, count);
    }

    private class BaseStorage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        public BaseStorage(PanelStorageContext ctx) {
            super(ctx);
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;
            if (CompressionManager.getOrCreateNode(resource.getItem()).previous != null) return BigInteger.ZERO;

            var contained = ctx.stack().getOrDefault(ChowlComponents.CONTAINED_ITEM, Items.AIR);

            if (contained == Items.AIR) contained = resource.getItem();
            if (contained != resource.getItem()) return BigInteger.ZERO;

            updateSnapshots(transaction);
            ctx.stack().set(ChowlComponents.CONTAINED_ITEM, contained);

            var currentCount = ctx.stack().getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO);
            var capacity = bigCapacity();
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            ctx.stack().set(ChowlComponents.COUNT, currentCount.add(inserted));

            Item finalContained = contained;

            if (CompressingPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!finalContained.getComponents().contains(DataComponentTypes.FIRE_RESISTANT) && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            if (VariantUtils.hasNbt(resource)) return BigInteger.ZERO;

            var contained = ctx.stack().getOrDefault(ChowlComponents.CONTAINED_ITEM, Items.AIR);

            if (contained == Items.AIR) return BigInteger.ZERO;
            if (contained != resource.getItem()) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().set(ChowlComponents.COUNT, newCount);

            if (newCount.equals(BigInteger.ZERO)) {
                if (!ctx.stack().contains(ChowlComponents.LOCKED)) {
                    ctx.stack().set(ChowlComponents.CONTAINED_ITEM, Items.AIR);
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
            return ItemVariant.of(ctx.stack().getOrDefault(ChowlComponents.CONTAINED_ITEM, Items.AIR));
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOrDefault(ChowlComponents.COUNT, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return CompressingPanelItem.this.fullCapacity(ctx.stack());
        }
    }
}
