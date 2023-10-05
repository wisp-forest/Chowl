package com.chyzman.chowl.transfer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.item.DrawerPanelItem;
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
        var component = stack.get(DrawerPanelItem.COMPONENT);

        if (component.itemVariant.isBlank()) component.itemVariant = resource;

        if (component.itemVariant.equals(resource)) {
            component.count = component.count.add(BigInteger.valueOf(maxAmount));

            updateSnapshots(transaction);
            stack.put(DrawerPanelItem.COMPONENT, component);

            return maxAmount;
        } else {
            return 0;
        }
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        var component = stack.get(DrawerPanelItem.COMPONENT);

        if (!component.itemVariant.equals(resource)) return 0;

        long removed;
        try {
            removed = Math.min(component.count.longValueExact(), maxAmount);
        } catch (ArithmeticException e) {
            removed = maxAmount;
        }
        component.count = component.count.subtract(BigInteger.valueOf(removed));
        component.updateVariant();

        updateSnapshots(transaction);
        stack.put(DrawerPanelItem.COMPONENT, component);

        return removed;
    }

    @Override
    protected void onFinalCommit() {
        this.blockEntity.markDirty();
    }

    @Override
    public boolean isResourceBlank() {
        return stack.get(DrawerPanelItem.COMPONENT).itemVariant.isBlank();
    }

    @Override
    public ItemVariant getResource() {
        return stack.get(DrawerPanelItem.COMPONENT).itemVariant;
    }

    @Override
    public long getAmount() {
        try {
            return stack.get(DrawerPanelItem.COMPONENT).count.longValueExact();
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

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