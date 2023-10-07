package com.chyzman.chowl.transfer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class PanelStorage extends SnapshotParticipant<ItemStack> {
    protected ItemStack stack;
    private final DrawerFrameBlockEntity blockEntity;
    private final Direction side;

    public PanelStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        this.stack = stack;
        this.blockEntity = blockEntity;
        this.side = side;
    }

    @Override
    protected ItemStack createSnapshot() {
        var old = stack;
        this.stack = stack.copy();
        blockEntity.stacks.set(side.getId(), new Pair<>(stack, blockEntity.stacks.get(side.getId()).getRight()));
        return old;
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        this.stack = snapshot;
        blockEntity.stacks.set(side.getId(), new Pair<>(stack, blockEntity.stacks.get(side.getId()).getRight()));
    }

    @Override
    protected void onFinalCommit() {
        this.blockEntity.markDirty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PanelStorage that = (PanelStorage) o;

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
