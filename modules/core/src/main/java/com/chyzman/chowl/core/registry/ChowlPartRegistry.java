package com.chyzman.chowl.core.registry;

import com.chyzman.chowl.core.multipart.PartType;
import com.chyzman.chowl.core.multipart.impl.EmptyPart;

public class ChowlPartRegistry {
    public static final PartType<EmptyPart> EMPTY = new PartType<>(EmptyPart.ENDEC);

    public static void init() {}
}
