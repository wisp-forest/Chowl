package com.chyzman.chowl.industries.transfer;

public interface FakeStorageView {
    default boolean countInTotalStorage() {
        return false;
    }

    default boolean countInDisplay() {
        return false;
    }
}
