package com.chyzman.chowl.core.format.number.impl;

import com.chyzman.chowl.core.format.number.NumberFormatter;
import com.chyzman.chowl.core.format.number.api.NumberFormatterType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.text.Text;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class ScalingNumberFormatterType implements NumberFormatterType {
    private static final int MAX_SEARCH = 10;

    private static final UnitReference DUMMY_UNIT = new UnitReference(Text.empty(), 0);

    private final String baseKey;

    private final Cache<Integer, UnitReference> unit_cache = CacheBuilder.newBuilder()
        .build();

    public ScalingNumberFormatterType(String typeName) {
        this.baseKey = NumberFormatter.BASE_KEY + "scaling." + typeName + ".";
    }

    @Override
    public Text format(String number) {
        var bd = new BigDecimal(number);
        var negative = bd.signum() < 0;
        var abs = bd.abs();

        var norm = abs.stripTrailingZeros();
        var scale = norm.precision() - norm.scale() - 1;
        var integerDigits = scale >= 0 ? scale + 1 : 0;

        if (integerDigits > 0 && integerDigits <= NumberFormatter.digits_before_abbreviation) {
            return NumberFormatter.addSeparators(number);
        }

        var unit = getUnit(scale);

        var scaled = abs.movePointLeft(unit.offset());
        var rounded = scaled.setScale(NumberFormatter.abbreviation_precision, RoundingMode.DOWN);
        var mantissa = (negative ? "-" : "") + rounded.stripTrailingZeros().toPlainString();

        if (unit.text() == null || unit.text().getString().isEmpty()) return Text.literal(mantissa);
        return Text.literal(mantissa).append(unit.text());
    }

    private UnitReference getUnit(int scale) {
        if (scale == 0) return DUMMY_UNIT;

        var cached = unit_cache.getIfPresent(scale);
        if (cached != null) return cached;

        Set<Integer> traversed = new HashSet<>();
        Text foundLabel = null;
        Integer foundScale = null;

        for (int i = scale; i >= scale - MAX_SEARCH; i--) {
            traversed.add(i);
            String target = baseKey + i;
            Text label = Text.translatableWithFallback(target, "");
            if (!label.getString().isEmpty()) {
                foundLabel = label;
                foundScale = i;
                break;
            }
        }

        if (foundLabel == null) {
            for (Integer k : traversed) unit_cache.put(k, DUMMY_UNIT);
            return DUMMY_UNIT;
        }

        for (Integer k : traversed) unit_cache.put(k, new UnitReference(foundLabel, foundScale));
        return new UnitReference(foundLabel, foundScale);
    }

    @Override
    public void invalidateCache() {
        unit_cache.invalidateAll();
    }

    private record UnitReference(Text text, int offset) {}
}
