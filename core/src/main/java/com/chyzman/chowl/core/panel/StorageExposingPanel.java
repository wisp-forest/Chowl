package com.chyzman.chowl.core.panel;

import com.chyzman.chowl.core.registry.ChowlComponents;
import net.minecraft.item.ItemStack;

import java.math.BigInteger;

public interface StorageExposingPanel<T> {
    BigInteger count(ItemStack panel, T type);

    BigInteger capacity(ItemStack panel, T type);
}
