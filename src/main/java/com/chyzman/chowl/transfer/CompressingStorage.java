package com.chyzman.chowl.transfer;

import com.chyzman.chowl.util.CompressionManager;
import com.chyzman.chowl.util.VariantUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

@SuppressWarnings("UnstableApiUsage")
public class CompressingStorage implements SingleSlotStorage<ItemVariant> {
    private final SingleSlotStorage<ItemVariant> base;
    private final int times;

    public CompressingStorage(SingleSlotStorage<ItemVariant> base, int times) {
        this.base = base;
        this.times = times;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (VariantUtils.hasNbt(resource)) return 0;

        var res = CompressionManager.downBy(resource.getItem(), times);

        if (res == null) return 0;

        long insertable;
        try (var nested = transaction.openNested()) {
            insertable = base.insert(ItemVariant.of(res.item()), maxAmount * res.totalMultiplier().longValueExact(), nested);
        }

        insertable = insertable / res.totalMultiplier().longValueExact() * res.totalMultiplier().longValueExact();

        return base.insert(ItemVariant.of(res.item()), insertable, transaction) / res.totalMultiplier().longValueExact();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (VariantUtils.hasNbt(resource)) return 0;

        var res = CompressionManager.downBy(resource.getItem(), times);

        if (res == null) return 0;
        if (res.item() != base.getResource().getItem()) return 0;

        long extractable;
        try (var nested = transaction.openNested()) {
            extractable = base.extract(ItemVariant.of(res.item()), maxAmount * res.totalMultiplier().longValueExact(), nested);
        }

        extractable = extractable / res.totalMultiplier().longValueExact() * res.totalMultiplier().longValueExact();

        return base.extract(ItemVariant.of(res.item()), extractable, transaction) / res.totalMultiplier().longValueExact();
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public ItemVariant getResource() {
        var res = CompressionManager.upBy(base.getResource().getItem(), times);

        if (res == null) return ItemVariant.blank();

        return ItemVariant.of(res.item());
    }

    @Override
    public long getAmount() {
        var res = CompressionManager.upBy(base.getResource().getItem(), times);

        if (res == null) return 0;

        return base.getAmount() / res.totalMultiplier().longValueExact();
    }

    @Override
    public long getCapacity() {
        var res = CompressionManager.upBy(base.getResource().getItem(), times);

        if (res == null) return 0;

        return base.getCapacity() / res.totalMultiplier().longValueExact();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompressingStorage that = (CompressingStorage) o;

        if (times != that.times) return false;
        return base.equals(that.base);
    }

    @Override
    public int hashCode() {
        int result = base.hashCode();
        result = 31 * result + times;
        return result;
    }
}