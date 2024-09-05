package com.chyzman.chowl.industries.util;

import com.chyzman.chowl.industries.classes.ChowlIndustriesConfigModel;
import com.google.common.math.BigIntegerMath;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.chyzman.chowl.industries.Chowl.CHOWL_CONFIG;

public final class FormatUtil {

    //TODO add config to make it put commas where they should be
    //TODO make count also formatted with it's own config (decide if it should have it's own format)
    //TODO make it so holding shift or something disables formatting in tooltips idk

    public static String formatCount(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);

        if (CHOWL_CONFIG.abbreviation_mode().equals(ChowlIndustriesConfigModel.AbbreviationMode.NONE) || digits <= CHOWL_CONFIG.digits_before_abbreviation()) return count.toString();
        return switch (CHOWL_CONFIG.abbreviation_mode()) {
            case LETTERS -> letterAbbreviation(count);
            case EXPONENTS -> "2^" + (BigIntegerMath.log2(count, RoundingMode.HALF_UP));
            case SCIENTIFIC -> scientificNotationAbbreviation(count);
            default -> count.toString();
        };
    }

    public static String letterAbbreviation(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);
        var strung = count.toString();
        var abv = List.of("K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No");
        var abvOnes = List.of("", "U", "D", "T", "Qq", "Qd", "Sx", "St", "Oc", "Nm");
        var abvTens = List.of("Dc", "Vg", "Tg");

        if (digits < 4) return strung;
        var shownDigits = String.valueOf(Double.parseDouble(strung.substring(0, ((digits - 1) % 3 + 1 - (count.signum() < 0 ? 1 : 0) + 1))) / 10).replace(".0", "");
        if (digits < 34) {
            return shownDigits + abv.get((digits - 1) / 3 - 1);
        } else if (digits < 100) {
            var tens = (digits - 34) / 30;
            var ones = (digits - 34) / 3 % 10;
            return shownDigits + abvOnes.get(ones) + (ones > 0 ? abvTens.get(tens).toLowerCase(Locale.ROOT) : abvTens.get(tens));
        } else if (digits < 103) {
            return shownDigits + "Googol";
        } else return "A lot";
    }

    public static String scientificNotationAbbreviation(BigInteger count) {
        NumberFormat formatter = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
        return formatter.format(count);
    }
}
