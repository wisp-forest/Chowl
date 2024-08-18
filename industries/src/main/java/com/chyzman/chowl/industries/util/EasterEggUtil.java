package com.chyzman.chowl.industries.util;

import io.wispforest.owo.ui.core.Color;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public class EasterEggUtil {
    public enum EasterEgg {
        CHROMA(List.of("jeb_", "chyzman"), EasterEggUtil::chromafy, null),
        UPSIDE_DOWN(List.of("dinnerbone", "grumm"), null, integer -> (integer + 2) % 4);

        EasterEgg(
                List<String> triggers,
                @Nullable Supplier<Color> colorSupplier,
                @Nullable Function<Integer, Integer> orientationModifier
        ) {
            this.triggers = triggers;
            this.colorSupplier = colorSupplier;
            this.orientationModifier = orientationModifier;
        }

        public final List<String> triggers;
        public final Supplier<Color> colorSupplier;
        public final Function<Integer, Integer> orientationModifier;

        public static @Nullable EasterEgg findEasterEgg(String name) {
            for (EasterEgg easterEgg : values()) {
                if (easterEgg.triggers.contains(name.toLowerCase(Locale.ROOT))) {
                    return easterEgg;
                }
            }
            return null;
        }

    }

    public static Color chromafy() {
        return Color.ofArgb(MathHelper.hsvToRgb((float) (System.currentTimeMillis() / 20d % 360d) / 360f, 1f, 1f));
    }
}
