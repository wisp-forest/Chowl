package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.network.chat.Component;

public class ScientificNumberFormatterType implements NumberFormatterType {
    public static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));

    @Override
    public Component format(String number) {
        var num = new BigDecimal(number);
        return Component.literal(SCIENTIFIC_FORMAT.format(num));
    }
}
