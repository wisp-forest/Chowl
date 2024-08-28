package com.chyzman.chowl.industries.transfer;

import com.chyzman.chowl.industries.util.CompressionManager;
import com.chyzman.chowl.industries.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class InitialCompressingStorage implements SingleSlotStorage<ItemVariant> {
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
            insertable = base.insert(ItemVariant.of(res.item()), maxAmount * res.totalMultiplier().longValueExact(), nested);
        }

        insertable = insertable / res.totalMultiplier().longValueExact() * res.totalMultiplier().longValueExact();

        return base.insert(ItemVariant.of(res.item()), insertable, transaction) / res.totalMultiplier().longValueExact();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return base.isResourceBlank();
    }

    @Override
    public ItemVariant getResource() {
        return base.getResource();
    }

    @Override
    public long getAmount() {
        return base.getAmount();
    }

    @Override
    public long getCapacity() {
        return base.isResourceBlank() ? base.getCapacity() : 0;
    }
}