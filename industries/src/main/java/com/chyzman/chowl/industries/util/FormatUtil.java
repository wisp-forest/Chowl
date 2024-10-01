package com.chyzman.chowl.industries.util;

import com.chyzman.chowl.core.util.BigIntUtils;
import com.google.common.math.BigIntegerMath;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.chyzman.chowl.industries.Chowl.CHOWL_CONFIG;

public final class FormatUtil {

    public static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));

    //TODO make count also formatted with it's own config (decide if it should have it's own format)
    //TODO make it so holding shift or something disables formatting in tooltips idk

    public static String formatCount(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);

        String formatted;
        if (digits > CHOWL_CONFIG.digits_before_abbreviation() && count.compareTo(BigInteger.ZERO) != 0) {
            formatted = switch (CHOWL_CONFIG.abbreviation_mode()) {
                case LETTERS -> letterAbbreviation(count);
                case EXPONENTS -> "2^" + (BigIntegerMath.log2(count, RoundingMode.HALF_UP));
                case SCIENTIFIC -> SCIENTIFIC_FORMAT.format(count);
                case SI -> siAbbreviation(count);
                default -> null;
            };
        } else formatted = null;
        if (formatted == null) {
            return CHOWL_CONFIG.use_commas() ? NumberFormat.getNumberInstance(Locale.US).format(count) : count.toString();
        } else if (formatted.isBlank()) {
            return "A lot";
        } else {
            return formatted;
        }
    }

    public static @Nullable String letterAbbreviation(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);
        var abv = List.of("K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No");
        var abvOnes = List.of("", "U", "D", "T", "Qq", "Qd", "Sx", "St", "Oc", "Nm");
        var abvTens = List.of("Dc", "Vg", "Tg");

        if (digits < 4) return null;
        var shownDigits = getShownDigits(count);
        if (digits < 34) {
            return shownDigits + abv.get((digits - 1) / 3 - 1);
        } else if (digits < 100) {
            var tens = (digits - 34) / 30;
            var ones = (digits - 34) / 3 % 10;
            return shownDigits + abvOnes.get(ones) + (ones > 0 ? abvTens.get(tens).toLowerCase(Locale.ROOT) : abvTens.get(tens));
        } else if (digits < 103) {
            return (shownDigits.equals("1") ? "" : shownDigits) + "Googol";
        } else return "";
    }

    public static @Nullable String siAbbreviation(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);
        var abv = List.of("K","M", "G", "T", "P", "E", "Z", "Y", "R", "Q");
        if (digits < 4) return null;
        if (digits < 34) return getShownDigits(count) + abv.get((digits - 1) / 3 - 1);
        return "";
    }


    public static String getShownDigits(BigInteger count) {
        var digits = BigIntUtils.decimalDigits(count);
        if (digits < 4) return count.toString();
        var strung = count.toString();
        var decimals = CHOWL_CONFIG.abbreviation_precision();
        var cutoff = (digits - 1) % 3 + 1 - (count.signum() < 0 ? 1 : 0);
        var returned = strung.substring(0, cutoff);
        if (decimals > 0) returned += "." + strung.substring(cutoff, Math.min(cutoff + decimals, strung.length()));
        returned = returned.contains(".") ? returned.replaceAll("0*$","").replaceAll("\\.$","") : returned;
        return returned;
    }
}
