package com.chyzman.chowl.core.multipart;

import io.wispforest.endec.Endec;

public class PartType<T extends Part> {
    private final Endec<T> endec;

    public PartType(Endec<T> endec) {
        this.endec = endec;
    }

    public Endec<T> getEndec() {
        return endec;
    }
}
