package com.chyzman.chowl.core.client;

import com.chyzman.chowl.core.ChowlCore;
import com.chyzman.chowl.core.ext.DoubleClickableBlock;
import com.chyzman.chowl.core.network.ChowlCoreNetworking;
import com.chyzman.chowl.core.network.DoubleClickC2SPacket;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DoubleClickTracker {
    public static final Identifier EVENT_ID = ChowlCore.id("double_click");

    private static long lastClickTime = 0;
    private static BlockPos lastBlockPos = null;

    public static void init() {
        UseBlockCallback.EVENT.addPhaseOrdering(EVENT_ID, Event.DEFAULT_PHASE);
        UseBlockCallback.EVENT.register(EVENT_ID, (player, world, hand, hitResult) -> {
            if (!world.isClient) return ActionResult.PASS;
            if (player.isSpectator()) return ActionResult.PASS;

            long duration = world.getTime() - lastClickTime;

            if (hitResult.getBlockPos().equals(lastBlockPos) && duration > 0 && duration <= ChowlCore.CONFIG.max_ticks_for_double_click()) {
                var pos = hitResult.getBlockPos();
                var state = world.getBlockState(pos);

                if (DoubleClickableBlock.doDoubleClick(world, state, hitResult, player).isAccepted()) {
                    player.swingHand(hand);
                    ChowlCoreNetworking.CHANNEL.clientHandle().send(new DoubleClickC2SPacket(hitResult));

                    return ActionResult.FAIL;
                }
            }

            lastClickTime = world.getTime();
            lastBlockPos = hitResult.getBlockPos().toImmutable();

            return ActionResult.PASS;
        });
    }

    public static void reset() {
        lastClickTime = 0;
        lastBlockPos = null;
    }
}
