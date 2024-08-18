package com.chyzman.chowl.industries.upgrade;

import com.chyzman.chowl.industries.Chowl;
import com.chyzman.chowl.industries.event.UpgradeInteractionEvents;
import com.chyzman.chowl.industries.registry.ChowlCriteria;
import com.chyzman.chowl.industries.util.EasterEggUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import java.util.List;

public class LabelingUpgrade {
    public static void init() {
        //TODO make this be on renamed item or on inventory tick or something
//        UpgradeInteractionEvents.UPGRADE_INSERTED.register((player, frame, side, panel, upgrade) -> {
//            if (!upgrade.isIn(Chowl.LABELING_UPGRADE_TAG)) return;
//            if (!upgrade.contains(DataComponentTypes.CUSTOM_NAME)) return;
//
//            ChowlCriteria.LABELED_PANEL.trigger(player, upgrade.getName().getString());
//        });
    }

    public static int rotateOrientationForEasterEggs(int original, ItemStack panel) {
            if (panel.contains(DataComponentTypes.CUSTOM_NAME)) {
                var easteregg = EasterEggUtil.EasterEgg.findEasterEgg(panel.getName().getString());
                if (easteregg != null && easteregg.orientationModifier != null) {
                    original = easteregg.orientationModifier.apply(original);
                }
            }
        return original;
    }
}
