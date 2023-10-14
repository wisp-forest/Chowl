package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.component.*;
import com.chyzman.chowl.transfer.BigStorageView;
import com.chyzman.chowl.transfer.CompressingStorage;
import com.chyzman.chowl.transfer.PanelStorage;
import com.chyzman.chowl.util.BigIntUtils;
import com.chyzman.chowl.util.CompressionManager;
import com.chyzman.chowl.util.NbtKeyTypes;
import com.chyzman.chowl.util.VariantUtils;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chowl.Chowl.*;

@SuppressWarnings("UnstableApiUsage")
public class CompressingPanelItem extends BasePanelItem implements FilteringPanelItem, LockablePanelItem, DisplayingPanelItem, CapacityLimitedPanelItem {
    public static final NbtKey<Item> ITEM = new NbtKey<>("Variant", NbtKey.Type.ofRegistry(Registries.ITEM));
    public static final NbtKey<BigInteger> COUNT = new NbtKey<>("Count", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);

    public CompressingPanelItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ItemVariant displayedVariant(ItemStack stack) {
        return ItemVariant.of(stack.get(ITEM));
    }

    @Override
    public BigInteger displayedCount(ItemStack stack, @Nullable DrawerFrameBlockEntity drawerFrame) {
        // TODO: use proper step
        return stack.get(COUNT);
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return ItemVariant.of(stack.get(ITEM));
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        if (to.getNbt() != null && !to.getNbt().isEmpty()) return false;

        var baseTo = CompressionManager.followDown(to.getItem());

        if (stack.get(ITEM).equals(baseTo)) return true;

        return stack.get(COUNT).signum() == 0;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        var baseNew = CompressionManager.followDown(newFilter.getItem());

        stack.put(ITEM, baseNew);
        stack.put(LOCKED, true);
    }

    @Override
    public boolean locked(ItemStack stack) {
        return stack.get(LOCKED);
    }

    @Override
    public void setLocked(ItemStack stack, boolean locked) {
        stack.put(LOCKED, true);
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return List.of(STORAGE_BUTTON);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        var storages = new ArrayList<SlottedStorage<ItemVariant>>();
        var base = new BaseStorage(stack, blockEntity, side);

        for (int i = 5; i >= 1; i--) storages.add(new CompressingStorage(base, i));
        storages.add(base);

        return new CombinedSlottedStorage<>(storages);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public BigInteger baseCapacity() {
        return new BigInteger(CHOWL_CONFIG.base_compressing_panel_capacity());
    }

    @SuppressWarnings("UnstableApiUsage")
    private class BaseStorage extends PanelStorage implements SingleSlotStorage<ItemVariant>, BigStorageView<ItemVariant> {
        public BaseStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
            super(stack, blockEntity, side);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (VariantUtils.hasNbt(resource)) return 0;
            if (CompressionManager.getOrCreateNode(resource.getItem()).previous != null) return 0;

            var contained = stack.get(ITEM);

            if (contained == Items.AIR) contained = resource.getItem();
            if (contained != resource.getItem()) return 0;

            var currentCount = stack.get(COUNT);
            var capacity = CompressingPanelItem.this.capacity(stack);
            var spaceLeft = capacity.subtract(currentCount).max(BigInteger.ZERO);
            var inserted = spaceLeft.min(BigInteger.valueOf(maxAmount));

            updateSnapshots(transaction);
            stack.put(ITEM, contained);
            stack.put(COUNT, currentCount.add(inserted));

//            Item finalContained = contained;
//
//            if (CompressingPanelItem.this.hasUpgrade(stack,
//                upgrade -> upgrade.isIn(VOID_UPGRADE_TAG)
//                    || (!finalContained.isFireproof() && upgrade.isIn(LAVA_UPGRADE_TAG))))
//                return maxAmount;

            return BigIntUtils.longValueSaturating(inserted);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext tx) {
            if (VariantUtils.hasNbt(resource)) return 0;

            var contained = stack.get(ITEM);

            if (contained == Items.AIR) return 0;
            if (contained != resource.getItem()) return 0;

            var currentCount = stack.get(COUNT);

            long removed = Math.min(BigIntUtils.longValueSaturating(currentCount), maxAmount);
            var newCount = currentCount.subtract(BigInteger.valueOf(removed));

            updateSnapshots(tx);
            stack.put(COUNT, newCount);

            if (newCount.equals(BigInteger.ZERO) && !stack.get(LOCKED)) {
                stack.put(ITEM, Items.AIR);
            }

            return removed;
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(stack.get(ITEM));
        }

        @Override
        public BigInteger bigAmount() {
            return stack.get(COUNT);
        }

        @Override
        public BigInteger bigCapacity() {
            return CompressingPanelItem.this.capacity(stack);
        }
    }
}