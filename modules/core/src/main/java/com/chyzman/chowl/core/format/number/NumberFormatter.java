package com.chyzman.chowl.core.format.number;

import com.chyzman.chowl.core.format.number.api.NumberAbbreviationMode;
import com.chyzman.chowl.core.format.number.api.NumberFormatterTypes;
import io.wispforest.owo.command.EnumArgumentType;
import net.minecraft.util.Language;

import java.util.regex.Pattern;

public class NumberFormatter {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    //TODO: make these into config options
    public static NumberAbbreviationMode abbreviation_mode = NumberAbbreviationMode.LETTER;
    public static int digits_before_abbreviation = 6;
    public static int abbreviation_precision = 2;
    public static boolean use_separators = true;


    public static String format(String number, NumberFormatterTypes type) {
        if (!NUMBER_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Number must be a valid number");
        }
        if (type.type == null) {
            return addSeparators(number);
        }
        var formatter = type.type;
        return formatter.format(number);
    }

    public String formatUnchecked(String number, NumberFormatterTypes type) {
        try {
            return format(number, type);
        } catch (Exception ignored) {
            return number;
        }
    }

    public static String addSeparators(String number) {
        var decimal = number.indexOf('.');
        var plain = decimal == -1 ? number : number.substring(0, decimal);
        var result = new StringBuilder();

        var separator = Language.getInstance().get("chowl.format.number.separator.group", ",");

        for (int i = 0; i < plain.length(); i++) {
            if (i > 0 && (plain.length() - i) % 3 == 0) {
                result.append(separator);
            }
            result.append(plain.charAt(i));
        }

        if (decimal != -1) result.append(number.substring(decimal));

        return result.toString();
    }
}
