package com.chyzman.chowl.core.format.number.api;

import com.chyzman.chowl.core.format.number.impl.DynamicNumberFormatterType;
import com.chyzman.chowl.core.format.number.impl.ScalingNumberFormatterType;
import com.chyzman.chowl.core.format.number.impl.ScientificNumberFormatterType;

public enum NumberFormatterTypes {
    LETTER(new DynamicNumberFormatterType("letter")),
    WORD(new DynamicNumberFormatterType("word")),
    SCIENTIFIC(new ScientificNumberFormatterType()),
    EXPONENT(null),
    SI(new ScalingNumberFormatterType("si")),
    FULL_SI(new ScalingNumberFormatterType("full_si")),
    NONE(null);

    public final NumberFormatterType type;

    NumberFormatterTypes(NumberFormatterType type) {
        this.type = type;
    }
}
