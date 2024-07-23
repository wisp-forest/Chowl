package com.chyzman.chowl.industries.upgrade;

import com.chyzman.chowl.industries.event.PanelEmptiedEvent;
import com.chyzman.chowl.industries.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.industries.registry.ChowlCriteria;
import com.chyzman.chowl.industries.util.ServerTickHelper;
import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.explosion.Explosion;
import nourl.mythicmetals.data.MythicTags;
import nourl.mythicmetals.entity.BanglumTntEntity;
import nourl.mythicmetals.misc.BanglumNukeSource;
import nourl.mythicmetals.misc.EpicExplosion;
import nourl.mythicmetals.misc.MythicDamageTypes;
import nourl.mythicmetals.registry.RegisterSounds;
import org.apache.commons.lang3.mutable.MutableInt;

public class NukeCoreUpgrade {
    public static void init() {
        PanelEmptiedEvent.EVENT.register(ctx -> {
            if (ctx.drawerFrame() != null
                && ctx.stack().getItem() instanceof UpgradeablePanelItem panelItem) {
                var world = ctx.world();
                var pos = ctx.drawerFrame().getPos();
                var upgrades = panelItem.upgrades(ctx.stack()).copyStacks();

                if (world.isClient) return; // how

                MutableInt power = new MutableInt(0);
                MutableInt total = new MutableInt(0);

                panelItem.modifyUpgrades(ctx.stack(), stacks -> {
                    for (ItemStack upgrade : upgrades) {
                        if (isNukeCore(upgrade)) {
                            power.add(8 - total.getValue());
                            total.add(1);
                            upgrade.decrement(1);
                        }
                    }

                    return stacks;
                });

                if (power.getValue() == 0) return;

                int finalPower = power.getValue();
                ServerTickHelper.schedule(() -> {
                    var pos3d = pos.toCenterPos();
                    Box affected = Box.of(pos3d, 10, 10, 10);

                    for (PlayerEntity player : world.getPlayers()) {
                        if (!player.isPartOfGame()) continue;
                        if (!affected.contains(player.getPos())) continue;

                        ChowlCriteria.WITNESSED_BLASTING.trigger((ServerPlayerEntity) player, true);
                    }

                    EpicExplosion.explode(
                        (ServerWorld) world,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        finalPower,
                        state -> true,
                        null,
                        null
                    );

                    // Literally copied from BanglumNukeEntity :D
                    // https://github.com/Noaaan/MythicMetals/blob/1.20/LICENSE

                    int soundRadius = finalPower * 3;

                    for (PlayerEntity player : world.getPlayers()) {
                        if (player.squaredDistanceTo(pos3d) > soundRadius * soundRadius) continue;

                        world.playSound(null, pos, RegisterSounds.BANGLUM_NUKE_EXPLOSION, SoundCategory.BLOCKS, 5.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
                    }

                    Explosion explosion = new Explosion(world, null, pos.getX(), pos.getY(), pos.getZ(), finalPower, false, Explosion.DestructionType.DESTROY_WITH_DECAY);

                    for (var entity : world.getOtherEntities(null, Box.of(pos3d, finalPower * 2, finalPower * 2, finalPower * 2))) {
                        if (entity.isImmuneToExplosion(explosion)) continue;
                        if (!CommonProtection.canDamageEntity(world, entity, CommonProtection.UNKNOWN, null)) continue;

                        double distanceModifier = 1 - Math.sqrt(entity.squaredDistanceTo(pos3d)) / (double) finalPower;
                        if (distanceModifier >= 0) {
                            double x = entity.getX() - pos.getX();
                            double y = (entity instanceof BanglumTntEntity ? entity.getY() : entity.getEyeY()) - pos.getY();
                            double z = entity.getZ() - pos.getZ();
                            double dist = Math.sqrt(x * x + y * y + z * z);
                            if (dist != 0.0) {
                                x /= dist;
                                y /= dist;
                                z /= dist;

                                var banglumNukeSource = new BanglumNukeSource(
                                    world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(MythicDamageTypes.BANGLUM_NUKE).orElseThrow(),
                                    null,
                                    null);
                                entity.damage(banglumNukeSource, MathHelper.floor((distanceModifier * distanceModifier + distanceModifier) * 7.0 * finalPower + 1.0));

                                double knockback = distanceModifier * 5;
                                if (entity instanceof LivingEntity living) {
                                    knockback = distanceModifier * (5.0 - living.getAttributeValue(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE));
                                }

                                entity.addVelocity(x * knockback, y * knockback, z * knockback);
                            }
                        }
                    }
                });
            }
        });
    }

    private static boolean isNukeCore(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem)
            return blockItem.getBlock().getRegistryEntry().isIn(MythicTags.NUKE_CORES);

        return false;
    }
}
