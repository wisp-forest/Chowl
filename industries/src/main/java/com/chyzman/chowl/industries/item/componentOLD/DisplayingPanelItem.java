package com.chyzman.chowl.industries.item.component;

import com.chyzman.chowl.core.registry.ChowlComponents;
import com.chyzman.chowl.industries.util.EasterEggUtil;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

public interface DisplayingPanelItem extends PanelItem {
    default @Nullable Text styleText(ItemStack stack, Text wrapped) {
        Calendar calendar = Calendar.getInstance();
        var style = getConfig(stack).textStyle();
        var chroma = Color.ofArgb(MathHelper.hsvToRgb((float) (System.currentTimeMillis() / 20d % 360d) / 360f, 1f, 1f)).rgb();
        if (calendar.get(Calendar.MONTH) + 1 == 5 && calendar.get(Calendar.DATE) == 16) {
            style = style.withColor(chroma);
        } else if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            var easterEgg = EasterEggUtil.EasterEgg.findEasterEgg(stack.getName().getString());
            if (easterEgg != null && easterEgg.colorSupplier != null) {
                style = style.withColor(easterEgg.colorSupplier.get().rgb());
            }
        }
        return Text.literal("").append(wrapped).setStyle(style);
    }

    default boolean supportsHideItem() {
        return true;
    }

    default boolean supportsHideName() {
        return true;
    }

    static DisplayingPanelConfig getConfig(ItemStack stack) {
        return stack.getOrDefault(ChowlComponents.DISPLAYING_CONFIG, DisplayingPanelConfig.DEFAULT);
    }
}
