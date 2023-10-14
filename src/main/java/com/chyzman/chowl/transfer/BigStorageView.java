package com.chyzman.chowl.transfer;

import com.chyzman.chowl.util.BigIntUtils;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.math.BigInteger;

@SuppressWarnings("UnstableApiUsage")
public interface BigStorageView<T> extends StorageView<T> {
    BigInteger bigAmount();

    BigInteger bigCapacity();

    @Override
    default long getAmount() {
        return BigIntUtils.longValueSaturating(bigAmount());
    }

    @Override
    default long getCapacity() {
        return BigIntUtils.longValueSaturating(bigCapacity());
    }
}
