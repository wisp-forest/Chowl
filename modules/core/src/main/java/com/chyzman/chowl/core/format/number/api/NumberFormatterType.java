package com.chyzman.chowl.core.format.number.api;

import net.minecraft.text.Text;

public interface NumberFormatterType {
    Text format(String number);

    default void invalidateCache() {}
}
