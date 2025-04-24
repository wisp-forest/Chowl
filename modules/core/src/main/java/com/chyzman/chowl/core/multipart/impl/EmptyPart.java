package com.chyzman.chowl.core.multipart.impl;

import com.chyzman.chowl.core.multipart.Part;
import io.wispforest.endec.Endec;
import net.minecraft.util.Identifier;

public class EmptyPart extends Part {
    public static final EmptyPart INSTANCE = new EmptyPart();
    public static final Endec<EmptyPart> ENDEC = Endec.unit(INSTANCE);

    @Override
    public Identifier getId() {
        return Identifier.of("empty");
    }
}
