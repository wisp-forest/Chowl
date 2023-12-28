package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.transfer.BigStorageView;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.transfer.TransferState;
import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (TransferState.NO_BLANK_DRAWERS.get() && ctx.stack().get(VARIANT).isBlank()) return null;

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
        return stack.get(VARIANT);
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (stack.get(VARIANT).equals(to)) return true;

        return stack.get(COUNT).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.put(VARIANT, newFilter);
        stack.put(LOCKED, true);
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.get(LOCKED);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.put(LOCKED, locked);
    }

    @Override
    public ItemVariant displayedVariant(ItemStack stack) {
        return stack.get(VARIANT);
    }

    @Override
    public BigInteger displayedCount(ItemStack stack, @Nullable DrawerFrameBlockEntity drawerFrame, @Nullable Direction side) {
        return stack.get(COUNT);
    }

    @Override
    public List<ItemStack> upgrades(ItemStack stack) {
        var returned = new ArrayList<ItemStack>();
        stack.get(UPGRADES_LIST).forEach(nbtElement -> returned.add(ItemStack.fromNbt((NbtCompound) nbtElement)));
        while (returned.size() < 8) returned.add(ItemStack.EMPTY);
        return returned;
    }

    @Override
    public void setUpgrades(ItemStack stack, List<ItemStack> upgrades) {
        var nbtList = new NbtList();
        upgrades.forEach(itemStack -> nbtList.add(itemStack.writeNbt(new NbtCompound())));
        stack.put(UPGRADES_LIST, nbtList);
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_panel_capacity());
    }

    @SuppressWarnings("UnstableApiUsage")
    private class Storage extends PanelStorage implements SingleSlotStorage<ItemVariant>, BigStorageView<ItemVariant> {
        public Storage(PanelStorageContext ctx) {
            super(ctx);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            var contained = ctx.stack().get(VARIANT);

            if (contained.isBlank()) contained = resource;
            if (!contained.equals(resource)) return 0;

            var currentCount = ctx.stack().get(COUNT);
            var capacity = DrawerPanelItem.this.capacity(ctx.stack());
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(BigInteger.valueOf(maxAmount));

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

            return BigIntUtils.longValueSaturating(inserted);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext tx) {
            var contained = ctx.stack().get(VARIANT);

            if (contained.isBlank()) return 0;
            if (!contained.equals(resource)) return 0;

            var currentCount = ctx.stack().get(COUNT);

            long removed = Math.min(BigIntUtils.longValueSaturating(currentCount), maxAmount);
            var newCount = currentCount.subtract(BigInteger.valueOf(removed));

            updateSnapshots(tx);
            ctx.stack().put(COUNT, newCount);

            if (newCount.compareTo(BigInteger.ZERO) <= 0) {
                if (!ctx.stack().get(LOCKED)) {
                    ctx.stack().put(VARIANT, ItemVariant.blank());
                }
                triggerExplosionUpgrade(ctx);
            }
            return removed;
        }

        @Override
        public boolean isResourceBlank() {
            return ctx.stack().get(VARIANT).isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return ctx.stack().get(VARIANT);
        }

        @Override
        public BigInteger bigAmount() {
            return ctx.stack().get(COUNT);
        }

        @Override
        public BigInteger bigCapacity() {
            return DrawerPanelItem.this.capacity(ctx.stack());
        }
    }
}