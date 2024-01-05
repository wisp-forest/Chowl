package com.chyzman.chowl.upgrade;

import com.chyzman.chowl.event.PanelEmptiedEvent;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.misc.EpicExplosion;

public class NukeCoreUpgrade {
    public static void init() {
        PanelEmptiedEvent.EVENT.register(ctx -> {
            if (ctx.drawerFrame() != null
                && ctx.stack().getItem() instanceof UpgradeablePanelItem panelItem) {
                var world = ctx.drawerFrame().getWorld();
                var pos = ctx.drawerFrame().getPos();
                var upgrades = panelItem.upgrades(ctx.stack());

                if (world.isClient) return; // how

                int power = 0;
                int total = 0;

                for (ItemStack upgrade : upgrades) {
                    if (isNukeCore(upgrade)) {
                        power += 8 - total;
                        total += 1;
                        upgrade.decrement(1);
                    }

                }

                if (power == 0) return;

                panelItem.setUpgrades(ctx.stack(), upgrades);

                EpicExplosion.explode(
                    (ServerWorld) world,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    power,
                    state -> true,
                    null,
                    null
                );
            }
        });
    }

    private static boolean isNukeCore(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem)
            return blockItem.getBlock().getRegistryEntry().isIn(MythicTags.NUKE_CORES);

        return false;
    }
}
