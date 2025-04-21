package com.chyzman.chowl.core.format.number.api;

import org.jetbrains.annotations.Nullable;

public enum NumberAbbreviationMode {
    LETTER("letter"),
    WORD("word"),
    EXPONENT(null),
    SCIENTIFIC(null),
    SI("si"),
    NONE(null);

    public final @Nullable String key;
    NumberAbbreviationMode(@Nullable String key) {
        this.key = key;
    }
}
