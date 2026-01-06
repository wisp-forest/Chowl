package com.chyzman.chowl.core.format.number;

import com.chyzman.chowl.core.format.number.api.NumberAbbreviationMode;
import com.chyzman.chowl.core.util.OvercomplicatedMathHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class SOMEWHATNEWERNumberFormatter {

    private static final String KEY_BASE = "chowl.format.number.";

    private static String groupSeparator = getSeparator("group");
    private static String decimalSeparator = getSeparator("decimal");

    //TODO: make these into configs
    public static NumberAbbreviationMode abbreviation_mode = NumberAbbreviationMode.LETTER;
    public static int digits_before_abbreviation = 6;
    public static int abbreviation_precision = 2;
    public static boolean use_separators = true;

    public static void test() {
        for (int i = 0; i < 303; i++) {
            var number = new BigInteger("1" + "0".repeat(i));
            System.out.println(format(number, NumberAbbreviationMode.WORD).getString());
        }
    }

    public static Component format(Number number, NumberAbbreviationMode abbreviationMode) {
        var digits = OvercomplicatedMathHelper.intDigits(number);
        if (digits != null && digits > digits_before_abbreviation && number.doubleValue() != 0) {
            return ABBREVIATION_CACHE.getUnchecked(new Tuple<>(digits, "word")).apply(getShownDigits(number));
        } else {
            return Component.literal(addCommas(number.toString()));
        }
    }


    private static final LoadingCache<Tuple<Integer, String>, Function<String, Component>> ABBREVIATION_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .expireAfterAccess(Duration.ofSeconds(10))
            .build(CacheLoader.from(abbreviationKey -> {
                var key = KEY_BASE + "abbreviation." + abbreviationKey.getB();

                //check for static translations
                for (int i = 0; i > -3; i--) {
                    var digits = abbreviationKey.getA() - 1 + i;
                    var target = key + ".unique." + digits;
                    if (Language.getInstance().has(target)) {
                        return integer -> Component.translatable(target, integer);
                    }
                }

                //check for dynamic translations
                var digitsString = String.valueOf((abbreviationKey.getA() - 4) / 3);
                var toApply = new ArrayList<String>();
                for (int i = digitsString.length() - 1; i > 0; i--) {
                    var digit = digitsString.charAt(i);
                    var target = key + ".dynamic." + (i) + "." + digit;
//                    if (!Language.getInstance().hasTranslation(target)) break;
                    toApply.add(target);
                }
                var affix = key + ".dynamic.affix";
                if (Language.getInstance().has(affix)) toApply.add(affix);
//                if (toApply.size() == digitsString.length()) {
                return count -> {
                    var text = Component.literal(count);
                    for (var t : toApply) text = Component.translatable(t, text);
                    return text;
                };
//                }

                //give up
//                return Text::literal;
            }));

    private static String getShownDigits(Number count) {
        var digits = OvercomplicatedMathHelper.intDigits(count);
        var strung = count.toString();
        if (digits < 4) return strung;
        var decimals = abbreviation_precision;
        var cutoff = (digits - 1) % 3 + 1 - (Math.signum(count.doubleValue()) < 0 ? 1 : 0);
        var returned = strung.substring(0, cutoff);
        if (decimals > 0) returned += "." + strung.substring(cutoff, Math.min(cutoff + decimals, strung.length()));
        returned = returned.contains(".") ? returned.replaceAll("0*$", "").replaceAll("\\.$", "") : returned;
        return addCommas(returned);
    }

    private static String addCommas(String number) {
        if (!use_separators) return number;
        var parts = number.split("\\.");
        var whole = parts[0];
        var decimal = parts.length > 1 ? decimalSeparator + parts[1] : "";
        StringBuilder result = new StringBuilder();
        for (int i = whole.length(); i > 0; i--) {
            if (i > 3 && i % 3 == 0) result.insert(0, groupSeparator);
            result.insert(0, whole.charAt(i - 1));
        }
        return result + decimal;
    }

    public static void onLangReload() {
        ABBREVIATION_CACHE.invalidateAll();
        groupSeparator = getSeparator("group");
        decimalSeparator = getSeparator("decimal");
    }

    private static String getSeparator(String type) {
        return Language.getInstance().getOrDefault(KEY_BASE + "separator." + type, ",");
    }
}
