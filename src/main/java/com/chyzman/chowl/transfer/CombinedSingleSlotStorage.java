package com.chyzman.chowl.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CombinedSingleSlotStorage<T> implements SingleSlotStorage<T> {
    private final List<SingleSlotStorage<T>> components;

    public CombinedSingleSlotStorage(List<SingleSlotStorage<T>> components) {
        this.components = components;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        long amount = 0;

        for (SingleSlotStorage<T> component : components) {
            amount += component.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        long amount = 0;

        for (SingleSlotStorage<T> component : components) {
            amount += component.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean isResourceBlank() {
        return components.get(0).isResourceBlank();
    }

    @Override
    public T getResource() {
        return components.get(0).getResource();
    }

    @Override
    public long getAmount() {
        long amount = 0;

        for (var component : components) {
            amount += component.getAmount();
        }

        return amount;
    }

    @Override
    public long getCapacity() {
        long amount = 0;

        for (var component : components) {
            amount += component.getAmount();
        }

        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CombinedSingleSlotStorage<?> that = (CombinedSingleSlotStorage<?>) o;

        return components.equals(that.components);
    }

    @Override
    public int hashCode() {
        return components.hashCode();
    }
}
