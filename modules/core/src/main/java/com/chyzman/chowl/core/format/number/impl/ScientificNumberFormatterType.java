package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ScientificNumberFormatterType implements NumberFormatterType {
    public static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));

    @Override
    public Text format(String number) {
        var num = new BigDecimal(number);
        return Text.literal(SCIENTIFIC_FORMAT.format(num));
    }
}
