package com.chyzman.chowl.upgrade;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.event.UpgradeInsertedEvent;
import com.chyzman.chowl.registry.ChowlRegistry;
import net.minecraft.item.ItemStack;

import java.util.List;

public class LabelingUpgrade {
    public static void init() {
        UpgradeInsertedEvent.EVENT.register((player, frame, side, panel, upgrade) -> {
            if (!upgrade.isIn(Chowl.LABELING_UPGRADE_TAG)) return;
            if (!upgrade.hasCustomName()) return;

            ChowlRegistry.LABELED_PANEL_CRITERIA.trigger(player, upgrade.getName().getString());
        });
    }

    public static int rotateOrientationForEasterEggs(int original, List<ItemStack> upgrades) {
        for (ItemStack upgrade : upgrades) {
            if (upgrade.isIn(Chowl.LABELING_UPGRADE_TAG) && upgrade.hasCustomName()) {
                switch (upgrade.getName().getString()) {
                    case "Dinnerbone","Grumm" -> original = (original + 2) % 4;
                }
            }
        }
        return original;
    }
}