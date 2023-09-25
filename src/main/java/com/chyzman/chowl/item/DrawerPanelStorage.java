package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DrawerPanelStorage extends SnapshotParticipant<ItemStack> implements Storage<ItemVariant> {
    private ItemStack stack;
    private final DrawerFrameBlockEntity blockEntity;

    public DrawerPanelStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity) {
        this.stack = stack;
        this.blockEntity = blockEntity;
    }

    @Override
    protected ItemStack createSnapshot() {
        return stack.copy();
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        this.stack = snapshot;
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

        long removed = Math.min(maxAmount, component.count.longValue());
        component.count = component.count.subtract(BigInteger.valueOf(removed));

        updateSnapshots(transaction);
        stack.put(DrawerPanelItem.COMPONENT, component);

        return removed;
    }

    @Override
    protected void onFinalCommit() {
        this.blockEntity.markDirty();
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return List.of(new StorageView<ItemVariant>() {
            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return DrawerPanelStorage.this.extract(resource, maxAmount, transaction);
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
                return stack.get(DrawerPanelItem.COMPONENT).count.longValue();
            }

            @Override
            public long getCapacity() {
                return Long.MAX_VALUE;
            }
        }.getUnderlyingView()).iterator();
    }
}
