package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.FilteringPanelItem;
import com.chyzman.chowl.item.component.LockablePanelItem;
import com.chyzman.chowl.item.component.PanelItem;
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
import java.util.OptionalInt;

@SuppressWarnings("UnstableApiUsage")
public class CompressingPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, LockablePanelItem, DisplayingPanelItem {
    public static final NbtKey<Item> ITEM = new NbtKey<>("Variant", NbtKey.Type.ofRegistry(Registries.ITEM));
    public static final NbtKey<BigInteger> COUNT = new NbtKey<>("Count", NbtKeyTypes.BIG_INTEGER);
    //    public static final NbtKey<BigInteger> CAPACITY = new NbtKey<>("Capacity", NbtKeyTypes.BIG_INTEGER);
    public static final NbtKey<Boolean> LOCKED = new NbtKey<>("Locked", NbtKey.Type.BOOLEAN);

    public CompressingPanelItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ItemVariant displayedVariant(ItemStack stack) {
        return ItemVariant.of(stack.get(ITEM));
    }

    @Override
    public BigInteger displayedCount(ItemStack stack) {
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

    @SuppressWarnings("UnstableApiUsage")
    private static class BaseStorage extends PanelStorage implements SingleSlotStorage<ItemVariant> {
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

            updateSnapshots(transaction);
            stack.put(ITEM, contained);
            stack.put(COUNT, stack.get(COUNT).add(BigInteger.valueOf(maxAmount))); // TODO: add capacity.
            return maxAmount;
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
        public long getAmount() {
            return BigIntUtils.longValueSaturating(stack.get(COUNT));
        }

        //todo make getamount return lower value so that getcapacity will allow you to insert (for when theres more then an entire long inside panel)
        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }
    }
}