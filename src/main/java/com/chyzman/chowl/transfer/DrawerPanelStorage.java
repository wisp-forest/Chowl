package com.chyzman.chowl.transfer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.DrawerPanelItem;
import com.chyzman.chowl.item.component.DrawerCountHolder;
import com.chyzman.chowl.item.component.DrawerFilterHolder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.math.BigInteger;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelStorage extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
    private ItemStack stack;
    private final DrawerFrameBlockEntity blockEntity;
    private final Direction side;

    public DrawerPanelStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        this.stack = stack;
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    protected ItemStack createSnapshot() {
        return stack.copy();
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        this.stack = snapshot;
        blockEntity.stacks.set(side.getId(), new Pair<>(stack, blockEntity.stacks.get(side.getId()).getRight()));
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (stack.getItem() instanceof DrawerFilterHolder<?> filterHolder) {
            if (filterHolder.filter(stack).isBlank()) {
                filterHolder.filter(stack, resource);
            }
            if (filterHolder.filter(stack).equals(resource)) {
                if (stack.getItem() instanceof DrawerCountHolder<?> countHolder) {
                    countHolder.count(stack, countHolder.count(stack).add(BigInteger.valueOf(maxAmount)));
                }
                updateSnapshots(transaction);
                return maxAmount;
            }
        }
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (stack.getItem() instanceof DrawerFilterHolder<?> filterHolder) {
            if (filterHolder.filter(stack).isBlank()) {
                return 0;
            }
            if (stack.getItem() instanceof DrawerCountHolder<?> countHolder) {
                long removed;
                try {
                    removed = Math.min(countHolder.count(stack).longValueExact(), maxAmount);
                } catch (ArithmeticException e) {
                    removed = maxAmount;
                }
                countHolder.count(stack, countHolder.count(stack).subtract(BigInteger.valueOf(removed)));
                return removed;
            }
        }
        return 0;
    }

    @Override
    protected void onFinalCommit() {
        this.blockEntity.markDirty();
    }

    @Override
    public boolean isResourceBlank() {
        return (stack.getItem() instanceof DrawerFilterHolder<?> filterHolder &&
                filterHolder.filter(stack).isBlank()) || !(stack.getItem() instanceof DrawerFilterHolder<?>);
    }

    @Override
    public ItemVariant getResource() {
        return (stack.getItem() instanceof DrawerFilterHolder<?> filterHolder) ? filterHolder.filter(stack) : ItemVariant.blank();
    }

    @Override
    public long getAmount() {
        if ((stack.getItem() instanceof DrawerCountHolder<?> countHolder)) {
            try {
                return countHolder.count(stack).longValueExact();
            } catch (ArithmeticException e) {
                return Long.MAX_VALUE;
            }
        }
        return 0;
    }

    //todo make getamount return lower value so that getcapacity will allow you to insert (for when theres more then an entire long inside panel)
    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawerPanelStorage that = (DrawerPanelStorage) o;

        if (!blockEntity.equals(that.blockEntity)) return false;
        return side == that.side;
    }

    @Override
    public int hashCode() {
        int result = blockEntity.hashCode();
        result = 31 * result + side.hashCode();
        return result;
    }
}