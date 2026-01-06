package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.format.number.NumberFormatter;
import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import static com.chyzman.chowl.core.format.number.NumberFormatter.ZERO;

public class DynamicNumberFormatterType implements NumberFormatterType {
    private final Component thousand;

    private final List<Component> specials;
    private final List<Component> units;
    private final List<Component> tens;
    private final List<Component> hundreds;
    private final Component millia;

    private final @Nullable Component prefix;
    private final @Nullable Component suffix;
    private final @Nullable Component illion;
    private final @Nullable Component tillion;

    public DynamicNumberFormatterType(String typeName) {
        var baseKey = NumberFormatter.BASE_KEY + "dynamic." + typeName + ".";

        this.thousand = Component.translatable(baseKey + "thousand");
        this.units = new ArrayList<>();
        this.tens = new ArrayList<>();
        this.hundreds = new ArrayList<>();
        this.specials = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            this.specials.add(Component.translatable(baseKey + "special." + i));
            this.units.add(Component.translatable(baseKey + "unit." + i));
            this.tens.add(Component.translatable(baseKey + "ten." + i));
            this.hundreds.add(Component.translatable(baseKey + "hundred." + i));
        }
        this.millia = Component.translatable(baseKey + "millia");

        this.prefix = Component.translatableWithFallback(baseKey + "prefix", "");
        this.suffix = Component.translatableWithFallback(baseKey + "suffix", "");
        this.illion = Component.translatableWithFallback(baseKey + "illion", "");
        this.tillion = Component.translatableWithFallback(baseKey + "tillion", "");
    }

    @Override
    public Component format(String number) {
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
        return kiloName == null ? Component.literal(mantissa) : Component.literal(mantissa).append(kiloName);
    }

    private List<String> splitKilos(String s) {
        var reversed = new StringBuilder(s).reverse().toString();
        var kiloKilos = new ArrayList<String>();
        for (int i = 0; i < reversed.length(); i += 3) {
            kiloKilos.add(new StringBuilder(reversed.substring(i, Math.min(i + 3, reversed.length()))).reverse().toString());
        }
        return kiloKilos.reversed();
    }

    private Component getKiloKilo(int latinPowerKilo, int milliaCount, List<Integer> kilos) {
        var kiloOnes = latinPowerKilo % 10;
        var kiloTens = Mth.floor(latinPowerKilo / 10d) % 10;
        var kiloHundreds = Mth.floor(latinPowerKilo / 100d) % 10;
        var lastKilo = kilos.size() - 1;
        var prefixFragments = new ArrayList<Component>();

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
            var millia = Component.empty();
            for (int i = 0; i < kilos.size() - milliaCount; i++) millia = millia.append(this.millia);
            prefixFragments.addFirst(millia);
        }

        return prefixFragments.stream()
            .filter(s -> s != null && !s.getString().isBlank())
            .collect(Component::empty, MutableComponent::append, MutableComponent::append);
    }

    private Component getKiloPrefix(int latinPower) {
        var kilos = splitKilos(String.valueOf(latinPower))
            .stream()
            .map(Integer::parseInt)
            .toList();

        List<Component> kiloPrefixParts = new ArrayList<>();
        for (int i = 0; i < kilos.size(); i++) {
            kiloPrefixParts.add(getKiloKilo(kilos.get(i), i, kilos));
        }

        return ComponentUtils.formatList(kiloPrefixParts, Component.empty());
    }

    @Nullable
    private Component getIllion(int latinPower) {
        var powerKilo = latinPower % 1000;

        if (powerKilo < 5 && powerKilo > 0 && latinPower < 1000) return null;

        if (powerKilo >= 7 && powerKilo <= 10 || Mth.floor(powerKilo / 10d) % 10 == 1) return illion;

        return tillion;
    }

    @Nullable
    private Component getKiloName(int power) {
        List<Component> fragments;

        if (power < 2) return power == 1 ? Component.empty().append(prefix).append(thousand) : null;

        fragments = new ArrayList<>();
        fragments.add(prefix);
        fragments.add(getKiloPrefix(power - 1));
        fragments.add(getIllion(power - 1));
        fragments.add(suffix);

        return fragments.stream()
            .filter(s -> s != null && !s.getString().isBlank())
            .collect(Component::empty, MutableComponent::append, MutableComponent::append);
    }
}
