package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.format.number.api.NumberFormatterType;

import java.util.HashMap;
import java.util.Map;

public class ScalingNumberFormatterType implements NumberFormatterType {
    private final Map<Integer, String> units = new HashMap<>();

    @Override
    public String format(String number) {
        return "";
    }
}
