package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.format.number.NumberFormatter;
import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DynamicNumberFormatterType implements NumberFormatterType {
    private final String typeName;

    private String thousand;

    private List<String> specials;
    private List<String> units;
    private List<String> tens;
    private List<String> hundreds;
    private String millia;

    private @Nullable String prefix;
    private @Nullable String suffix;
    private @Nullable String illion;
    private @Nullable String tillion;

    public DynamicNumberFormatterType(String typeName) {
        this.typeName = typeName;
        this.invalidateCache();
    }

    @Override
    public String format(String number) {
        var plain = new BigDecimal(number).toPlainString();

        var decimal = plain.indexOf('.');
        plain = plain.substring(0, decimal == -1 ? plain.length() : decimal);

        if (plain.isEmpty()) return "0";

        var mod = plain.length() % 3;
        if (mod == 0) mod = 3;
        var shown = plain.substring(0, mod);

        return NumberFormatter.addSeparators(shown) + Objects.requireNonNullElse(this.getKiloName((plain.length() - mod) / 3), "");
    }

    private List<String> splitKilos(String s) {
        var reversed = new StringBuilder(s).reverse().toString();
        var kiloKilos = new ArrayList<String>();
        for (int i = 0; i < reversed.length(); i += 3) {
            kiloKilos.add(new StringBuilder(reversed.substring(i, Math.min(i + 3, reversed.length()))).reverse().toString());
        }
        return kiloKilos.reversed();
    }

    private String getKiloKilo(int latinPowerKilo, int milliaCount, List<Integer> kilos) {
        var kiloOnes = latinPowerKilo % 10;
        var kiloTens = MathHelper.floor(latinPowerKilo / 10d) % 10;
        var kiloHundreds = MathHelper.floor(latinPowerKilo / 100d) % 10;
        var lastKilo = kilos.size() - 1;
        var prefixFragments = new ArrayList<String>();

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

        if (latinPowerKilo > 0 && milliaCount > 0) prefixFragments.addFirst(millia.repeat(kilos.size() - milliaCount));

        return prefixFragments.stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(""));
    }

    private String getKiloPrefix(int latinPower) {
        var kilos = splitKilos(String.valueOf(latinPower))
                .stream()
                .map(Integer::parseInt)
                .toList();

        List<String> kiloPrefixParts = new ArrayList<>();
        for (int i = 0; i < kilos.size(); i++) {
            kiloPrefixParts.add(getKiloKilo(kilos.get(i), i, kilos));
        }

        return String.join("", kiloPrefixParts).trim();
    }

    @Nullable
    private String getIllion(int latinPower) {
        var powerKilo = latinPower % 1000;

        if (powerKilo < 5 && powerKilo > 0 && latinPower < 1000) return null;

        if (powerKilo >= 7 && powerKilo <= 10 || MathHelper.floor(powerKilo / 10d) % 10 == 1) return illion;

        return tillion;
    }

    @Nullable
    private String getKiloName(int power) {
        List<String> fragments;

        if (power < 2) return power == 1 ? Objects.requireNonNullElse(prefix, "") + thousand : null;

        fragments = new ArrayList<>();
        fragments.add(prefix);
        fragments.add(getKiloPrefix(power - 1));
        fragments.add(getIllion(power - 1));
        fragments.add(suffix);

        return fragments.stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(""));
    }

    @Override
    public void invalidateCache() {
        var language = Language.getInstance();
        var keyBase = "chowl.format.number.dynamic." + this.typeName + ".";

        this.thousand = language.get(keyBase + "thousand");
        this.units = new ArrayList<>();
        this.tens = new ArrayList<>();
        this.hundreds = new ArrayList<>();
        this.specials = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            this.specials.add(language.get(keyBase + "special." + i));
            this.units.add(language.get(keyBase + "unit." + i));
            this.tens.add(language.get(keyBase + "ten." + i));
            this.hundreds.add(language.get(keyBase + "hundred." + i));
        }
        this.millia = language.get(keyBase + "millia");

        this.prefix = language.get(keyBase + "prefix", null);
        this.suffix = language.get(keyBase + "suffix", null);
        this.illion = language.get(keyBase + "illion", null);
        this.tillion = language.get(keyBase + "tillion", null);
    }
}
