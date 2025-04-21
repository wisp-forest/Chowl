package com.chyzman.chowl.core.format.number.api;

public interface NumberFormatterType {
    String format(String number);

    default void invalidateCache() {}
}
