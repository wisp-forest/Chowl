package com.chyzman.chowl.industries.upgrade;

import com.chyzman.chowl.industries.event.PanelEmptiedEvent;
import com.chyzman.chowl.industries.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.industries.registry.ChowlCriteria;
import com.chyzman.chowl.industries.util.ServerTickHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import static com.chyzman.chowl.industries.Chowl.EXPLOSIVE_UPGRADE_TAG;
import static com.chyzman.chowl.industries.Chowl.FIERY_UPGRADE_TAG;

public class ExplosiveUpgrade {
    public static void init() {
        PanelEmptiedEvent.EVENT.register(ctx -> {
            if (ctx.drawerFrame() != null
                && ctx.stack().getItem() instanceof UpgradeablePanelItem panelItem
                && panelItem.hasUpgrade(ctx.stack(), upgrade -> upgrade.isIn(EXPLOSIVE_UPGRADE_TAG))) {
                var world = ctx.drawerFrame().getWorld();
                var pos = ctx.drawerFrame().getPos();
                MutableInt power = new MutableInt();
                MutableBoolean fiery = new MutableBoolean(false);

                panelItem.modifyUpgrades(ctx.stack(), upgrades -> {
                    for (ItemStack upgrade : upgrades) {
                        if (upgrade.isIn(EXPLOSIVE_UPGRADE_TAG)) {
                            power.add(1);
                            upgrade.decrement(1);
                        }
                        if (upgrade.isIn(FIERY_UPGRADE_TAG)) {
                            fiery.setTrue();
                            upgrade.decrement(1);
                        }
                    }

                    return upgrades;
                });

                if (power.getValue() == 0) return;

                boolean finalFiery = fiery.getValue();
                int finalPower = power.getValue();
                ServerTickHelper.schedule(() -> {
                    Box affected = Box.of(pos.toCenterPos(), 10, 10, 10);

                    for (PlayerEntity player : world.getPlayers()) {
                        if (!player.isPartOfGame()) continue;
                        if (!affected.contains(player.getPos())) continue;

                        ChowlCriteria.WITNESSED_BLASTING.trigger((ServerPlayerEntity) player, false);
                    }

                    world.createExplosion(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        finalPower + 1,
                        finalFiery,
                        World.ExplosionSourceType.BLOCK
                    );
                });
            }
        });
    }
}
