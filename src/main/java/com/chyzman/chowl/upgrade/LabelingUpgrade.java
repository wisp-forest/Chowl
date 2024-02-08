package com.chyzman.chowl.upgrade;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.event.UpgradeInsertedEvent;
import com.chyzman.chowl.registry.ChowlRegistry;

public class LabelingUpgrade {
    public static void init() {
        UpgradeInsertedEvent.EVENT.register((player, frame, side, panel, upgrade) -> {
            if (!upgrade.isIn(Chowl.LABELING_UPGRADE_TAG)) return;
            if (!upgrade.hasCustomName()) return;

            ChowlRegistry.LABELED_PANEL_CRITERIA.trigger(player, upgrade.getName().getString());
        });
    }
}
