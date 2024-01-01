package com.chyzman.chowl.transfer;

import com.chyzman.chowl.util.CompressionManager;
import com.chyzman.chowl.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

@SuppressWarnings("UnstableApiUsage")
public class InitialCompressingStorage implements SingleSlotStorage<ItemVariant>, FakeStorageView {
    private final SingleSlotStorage<ItemVariant> base;

    public InitialCompressingStorage(SingleSlotStorage<ItemVariant> base) {
        this.base = base;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (base.getAmount() > 0) return 0;
        if (VariantUtils.hasNbt(resource)) return 0;

        var res = CompressionManager.followDown(resource.getItem());

        long insertable;
        try (var nested = transaction.openNested()) {
            insertable = base.insert(ItemVariant.of(res.item()), maxAmount * res.total(), nested);
        }

        insertable = insertable / res.total() * res.total();

        return base.insert(ItemVariant.of(res.item()), insertable, transaction) / res.total();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return true;
    }

    @Override
    public ItemVariant getResource() {
        return ItemVariant.blank();
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return base.isResourceBlank() ? base.getCapacity() : 0;
    }
}
