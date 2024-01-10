package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.transfer.BigSingleSlotStorage;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.transfer.TransferState;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.Chowl.*;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, UpgradeablePanelItem, CapacityLimitedPanelItem {
    public static final NbtKey<ItemVariant> VARIANT = new NbtKey<>("Variant", NbtKeyTypes.ITEM_VARIANT);
    public static final NbtKey<BigInteger> COUNT = new NbtKey<>("Count", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);

    public DrawerPanelItem(Settings settings) {
        super(settings);
    }

    public @Nullable SlottedStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        if (TransferState.NO_BLANK_DRAWERS.get() && ctx.stack().getOr(VARIANT, ItemVariant.blank()).isBlank()) return null;

        return new Storage(ctx);
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        var returned = new ArrayList<BlockButton>();
        returned.add(STORAGE_BUTTON);
        return addUpgradeButtons(stack, returned);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return stack.getOr(VARIANT, ItemVariant.blank());
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (stack.getOr(VARIANT, ItemVariant.blank()).equals(to)) return true;

        return stack.getOr(COUNT, BigInteger.ZERO).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.put(VARIANT, newFilter);
        stack.put(LOCKED, !newFilter.equals(ItemVariant.blank()));
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.getOr(LOCKED, false);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.put(LOCKED, locked);

        if (!locked && stack.getOr(COUNT, BigInteger.ZERO).equals(BigInteger.ZERO)) {
            stack.put(VARIANT, ItemVariant.blank());
        }
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity());
    }

    @SuppressWarnings("UnstableApiUsage")
    private class Storage extends PanelStorage implements BigSingleSlotStorage<ItemVariant> {
        public Storage(PanelStorageContext ctx) {
            super(ctx);
        }

        @Override
        public BigInteger bigInsert(ItemVariant resource, BigInteger maxAmount, TransactionContext transaction) {
            var contained = ctx.stack().getOr(VARIANT, ItemVariant.blank());

            if (contained.isBlank()) contained = resource;
            if (!contained.equals(resource)) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOr(COUNT, BigInteger.ZERO);
            var capacity = DrawerPanelItem.this.capacity(ctx.stack());
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(maxAmount);

            updateSnapshots(transaction);
            ctx.stack().put(VARIANT, contained);
            ctx.stack().put(COUNT, currentCount.add(inserted));

            ItemVariant finalContained = contained;

            if (DrawerPanelItem.this.hasUpgrade(
                    ctx.stack(),
                    upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
                            || (!finalContained.getItem().isFireproof() && upgrade.isIn(LAVA_UPGRADE_TAG))
            ))
                return maxAmount;

            return inserted;
        }

        @Override
        public BigInteger bigExtract(ItemVariant resource, BigInteger maxAmount, TransactionContext tx) {
            var contained = ctx.stack().getOr(VARIANT, ItemVariant.blank());

            if (contained.isBlank()) return BigInteger.ZERO;
            if (!contained.equals(resource)) return BigInteger.ZERO;

            var currentCount = ctx.stack().getOr(COUNT, BigInteger.ZERO);

            BigInteger removed = currentCount.min(maxAmount);
            var newCount = currentCount.subtract(removed);

            updateSnapshots(tx);
            ctx.stack().put(COUNT, newCount);

            if (newCount.compareTo(BigInteger.ZERO) <= 0) {
                if (!ctx.stack().getOr(LOCKED, false)) {
                    ctx.stack().put(VARIANT, ItemVariant.blank());
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
            return ctx.stack().getOr(VARIANT, ItemVariant.blank());
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().getOr(COUNT, BigInteger.ZERO);
        }

        @Override
        public BigInteger bigCapacity() {
            return DrawerPanelItem.this.capacity(ctx.stack());
        }
    }
}