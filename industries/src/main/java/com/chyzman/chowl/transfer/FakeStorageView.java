package com.chyzman.chowl.transfer;

public interface FakeStorageView {
    default boolean countInTotalStorage() {
        return false;
    }

    default boolean countInDisplay() {
        return false;
    }
}
