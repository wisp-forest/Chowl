package com.chyzman.chowl.transfer;

import com.chyzman.chowl.event.PanelEmptiedEvent;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class PanelStorage extends SnapshotParticipant<PanelStorage.State> {
    protected final PanelStorageContext ctx;
    protected boolean needsEmptiedEvent = false;

    public PanelStorage(PanelStorageContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected PanelStorage.State createSnapshot() {
        var old = ctx.stack();
        ctx.setStack(old.copy());
        return new State(old, needsEmptiedEvent);
    }

    @Override
    protected void readSnapshot(State snapshot) {
        ctx.setStack(snapshot.stack());
        needsEmptiedEvent = snapshot.needsEmptiedEvent();
    }

    @Override
    protected void onFinalCommit() {
        ctx.markDirty();

        if (needsEmptiedEvent)
            PanelEmptiedEvent.EVENT.invoker().onPanelEmptied(ctx);
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

    public record State(ItemStack stack, boolean needsEmptiedEvent) {

    }
}