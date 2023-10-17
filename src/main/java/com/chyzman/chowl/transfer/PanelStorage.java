package com.chyzman.chowl.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class PanelStorage extends SnapshotParticipant<ItemStack> {
    protected final PanelStorageContext ctx;

    public PanelStorage(PanelStorageContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected ItemStack createSnapshot() {
        var old = ctx.stack();
        ctx.setStack(old.copy());
        return old;
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        ctx.setStack(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        ctx.markDirty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PanelStorage that = (PanelStorage) o;

        return ctx.equals(that.ctx);
    }

    @Override
    public int hashCode() {
        return ctx.hashCode();
    }
}