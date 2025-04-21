package com.chyzman.chowl.core.format.number.api;

import com.chyzman.chowl.core.format.number.impl.DynamicNumberFormatterType;
import com.chyzman.chowl.core.format.number.impl.ScientificNumberFormatterType;

public enum NumberFormatterTypes {
    LETTER(new DynamicNumberFormatterType("letter")),
    WORD(new DynamicNumberFormatterType("word")),
    SCIENTIFIC(new ScientificNumberFormatterType()),
    EXPONENT(null),
    SI(new DynamicNumberFormatterType("si")),
    FULL_SCI(new DynamicNumberFormatterType("full_sci")),
    NONE(null);

    public final NumberFormatterType type;

    NumberFormatterTypes(NumberFormatterType type) {
        this.type = type;
    }
}
