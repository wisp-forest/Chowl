package com.chyzman.chowl.core.format.number.api;

import net.minecraft.network.chat.Component;

public interface NumberFormatterType {
    Component format(String number);

    default void invalidateCache() {}
}
