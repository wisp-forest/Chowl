package com.chyzman.chowl.item.component;

import com.chyzman.chowl.registry.ChowlRegistry;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

import static com.chyzman.chowl.Chowl.LABELING_UPGRADE_TAG;

public interface DisplayingPanelItem extends PanelItem {
    default @Nullable Text styleText(ItemStack stack, Text wrapped) {
        Calendar calendar = Calendar.getInstance();
        var style = getConfig(stack).textStyle();
        var chroma = Color.ofArgb(MathHelper.hsvToRgb((float) (System.currentTimeMillis() / 20d % 360d) / 360f, 1f, 1f)).rgb();
        if (calendar.get(Calendar.MONTH) + 1 == 5 && calendar.get(Calendar.DATE) == 16) {
            style = style.withColor(chroma);
        } else if (stack.getItem() instanceof UpgradeablePanelItem upgradeable) {
            if (upgradeable.hasUpgrade(stack, upgrade -> upgrade.isIn(LABELING_UPGRADE_TAG))) {
                var labelStack = upgradeable.upgrades(stack).findUpgrade(upgradeStack -> upgradeStack.isIn(LABELING_UPGRADE_TAG) && upgradeStack.contains(DataComponentTypes.CUSTOM_NAME));

                if (labelStack != null) {
                    switch (labelStack.getName().getString()) {
                        case "jeb_" -> style = style.withColor(chroma);
                        case "chyzman" -> style = style.withColor(Color.ofArgb(0xFFFF00).rgb());
                    }
                }
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
        return stack.getOrDefault(ChowlRegistry.DISPLAYING_CONFIG, DisplayingPanelConfig.DEFAULT);
    }
}