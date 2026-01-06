package com.chyzman.chowl.core.format.number;

import com.chyzman.chowl.core.format.number.api.NumberAbbreviationMode;
import com.chyzman.chowl.core.format.number.api.NumberFormatterTypes;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;

import static com.chyzman.chowl.core.Chowl.MODID;

public class NumberFormatter {
    public static final String BASE_KEY = MODID + ".format.number.";

    public static final  Component ZERO = Component.literal("0");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    //TODO: make these into config options
    public static NumberAbbreviationMode abbreviation_mode = NumberAbbreviationMode.LETTER;
    public static int digits_before_abbreviation = 6;
    public static int abbreviation_precision = 2;
    public static boolean use_separators = true;


    public static Component format(String number, NumberFormatterTypes type) {
        if (!NUMBER_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Number must be a valid number");
        }
        if (type.type == null) {
            return addSeparators(number);
        }
        var formatter = type.type;
        return formatter.format(number);
    }

    public Component formatUnchecked(String number, NumberFormatterTypes type) {
        try {
            return format(number, type);
        } catch (Exception ignored) {
            return Component.literal(number);
        }
    }

    public static Component addSeparators(String number) {
        var decimal = number.indexOf('.');
        var plain = decimal == -1 ? number : number.substring(0, decimal);
        var result = Component.empty();

        var separator = Component.translatableWithFallback(BASE_KEY + "separator.group", ",");

        for (int i = 0; i < plain.length(); i++) {
            if (i > 0 && (plain.length() - i) % 3 == 0) {
                result.append(separator);
            }
            result.append(Component.literal(Character.toString(plain.charAt(i))));
        }

        if (decimal != -1) result.append(number.substring(decimal));

        return result;
    }
}
