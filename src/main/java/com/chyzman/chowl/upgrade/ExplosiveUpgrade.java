package com.chyzman.chowl.upgrade;

import com.chyzman.chowl.event.PanelEmptiedEvent;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chyzman.chowl.Chowl.EXPLOSIVE_UPGRADE_TAG;
import static com.chyzman.chowl.Chowl.FIERY_UPGRADE_TAG;

public class ExplosiveUpgrade {
    public static void init() {
        PanelEmptiedEvent.EVENT.register(ctx -> {
            if (ctx.drawerFrame() != null
                && ctx.stack().getItem() instanceof UpgradeablePanelItem panelItem
                && panelItem.hasUpgrade(ctx.stack(), upgrade -> upgrade.isIn(EXPLOSIVE_UPGRADE_TAG))) {
                var world = ctx.drawerFrame().getWorld();
                var pos = ctx.drawerFrame().getPos();
                var upgrades = panelItem.upgrades(ctx.stack());
                int power = 0;
                boolean fiery = false;

                for (ItemStack upgrade : upgrades) {
                    if (upgrade.isIn(EXPLOSIVE_UPGRADE_TAG)) {
                        power += 1;
                        upgrade.decrement(1);
                    }
                    if (upgrade.isIn(FIERY_UPGRADE_TAG)) {
                        fiery = true;
                        upgrade.decrement(1);
                    }
                }

                if (power == 0) return;

                panelItem.setUpgrades(ctx.stack(), upgrades);

                world.createExplosion(
                    null,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    power + 1,
                    fiery,
                    World.ExplosionSourceType.BLOCK
                );
            }
        });
    }
}
