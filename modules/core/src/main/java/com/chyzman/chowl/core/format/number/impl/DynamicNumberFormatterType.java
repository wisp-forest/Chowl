package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.format.number.NumberFormatter;
import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.chyzman.chowl.core.format.number.NumberFormatter.ZERO;

public class DynamicNumberFormatterType implements NumberFormatterType {
    private final Text thousand;

    private final List<Text> specials;
    private final List<Text> units;
    private final List<Text> tens;
    private final List<Text> hundreds;
    private final Text millia;

    private final @Nullable Text prefix;
    private final @Nullable Text suffix;
    private final @Nullable Text illion;
    private final @Nullable Text tillion;

    public DynamicNumberFormatterType(String typeName) {
        var baseKey = NumberFormatter.BASE_KEY + "dynamic." + typeName + ".";

        this.thousand = Text.translatable(baseKey + "thousand");
        this.units = new ArrayList<>();
        this.tens = new ArrayList<>();
        this.hundreds = new ArrayList<>();
        this.specials = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            this.specials.add(Text.translatable(baseKey + "special." + i));
            this.units.add(Text.translatable(baseKey + "unit." + i));
            this.tens.add(Text.translatable(baseKey + "ten." + i));
            this.hundreds.add(Text.translatable(baseKey + "hundred." + i));
        }
        this.millia = Text.translatable(baseKey + "millia");

        this.prefix = Text.translatableWithFallback(baseKey + "prefix", "");
        this.suffix = Text.translatableWithFallback(baseKey + "suffix", "");
        this.illion = Text.translatableWithFallback(baseKey + "illion", "");
        this.tillion = Text.translatableWithFallback(baseKey + "tillion", "");
    }

    @Override
    public Text format(String number) {
        var plain = new BigDecimal(number).toPlainString();

        var decimal = plain.indexOf('.');
        plain = plain.substring(0, decimal == -1 ? plain.length() : decimal);

        if (plain.isEmpty()) return NumberFormatter.ZERO;

        var mod = plain.length() % 3;
        if (mod == 0) mod = 3;
        var power = (plain.length() - mod) / 3;

        if (power <= 0) {
            return NumberFormatter.addSeparators(plain);
        }

        var scaled = new BigDecimal(plain).movePointLeft(power * 3);
        var mantissa = scaled
            .setScale(NumberFormatter.abbreviation_precision, RoundingMode.DOWN)
            .stripTrailingZeros()
            .toPlainString();

        var kiloName = this.getKiloName(power);
        return kiloName == null ? Text.literal(mantissa) : Text.literal(mantissa).append(kiloName);
    }

    private List<String> splitKilos(String s) {
        var reversed = new StringBuilder(s).reverse().toString();
        var kiloKilos = new ArrayList<String>();
        for (int i = 0; i < reversed.length(); i += 3) {
            kiloKilos.add(new StringBuilder(reversed.substring(i, Math.min(i + 3, reversed.length()))).reverse().toString());
        }
        return kiloKilos.reversed();
    }

    private Text getKiloKilo(int latinPowerKilo, int milliaCount, List<Integer> kilos) {
        var kiloOnes = latinPowerKilo % 10;
        var kiloTens = MathHelper.floor(latinPowerKilo / 10d) % 10;
        var kiloHundreds = MathHelper.floor(latinPowerKilo / 100d) % 10;
        var lastKilo = kilos.size() - 1;
        var prefixFragments = new ArrayList<Text>();

        if (kiloOnes > 0 && (
            lastKilo == 0 ||
            milliaCount < lastKilo ||
            milliaCount == lastKilo && latinPowerKilo > 1
        )) {
            prefixFragments.addFirst(
                latinPowerKilo < 10 && milliaCount < 1 && lastKilo < 1 ?
                    specials.get(kiloOnes - 1) :
                    units.get(kiloOnes - 1)
            );
        }

        if (kiloTens > 0) prefixFragments.add(tens.get(kiloTens - 1));

        if (kiloHundreds > 0) prefixFragments.addFirst(hundreds.get(kiloHundreds - 1));

        if (latinPowerKilo > 0 && milliaCount > 0) {
            var millia = Text.empty();
            for (int i = 0; i < kilos.size() - milliaCount; i++) millia = millia.append(this.millia);
            prefixFragments.addFirst(millia);
        }

        return prefixFragments.stream()
            .filter(s -> s != null && !s.getString().isBlank())
            .collect(Text::empty, MutableText::append, MutableText::append);
    }

    private Text getKiloPrefix(int latinPower) {
        var kilos = splitKilos(String.valueOf(latinPower))
            .stream()
            .map(Integer::parseInt)
            .toList();

        List<Text> kiloPrefixParts = new ArrayList<>();
        for (int i = 0; i < kilos.size(); i++) {
            kiloPrefixParts.add(getKiloKilo(kilos.get(i), i, kilos));
        }

        return Texts.join(kiloPrefixParts, Text.empty());
    }

    @Nullable
    private Text getIllion(int latinPower) {
        var powerKilo = latinPower % 1000;

        if (powerKilo < 5 && powerKilo > 0 && latinPower < 1000) return null;

        if (powerKilo >= 7 && powerKilo <= 10 || MathHelper.floor(powerKilo / 10d) % 10 == 1) return illion;

        return tillion;
    }

    @Nullable
    private Text getKiloName(int power) {
        List<Text> fragments;

        if (power < 2) return power == 1 ? Text.empty().append(prefix).append(thousand) : null;

        fragments = new ArrayList<>();
        fragments.add(prefix);
        fragments.add(getKiloPrefix(power - 1));
        fragments.add(getIllion(power - 1));
        fragments.add(suffix);

        return fragments.stream()
            .filter(s -> s != null && !s.getString().isBlank())
            .collect(Text::empty, MutableText::append, MutableText::append);
    }
}
