package com.chyzman.chowl.client;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.block.DoubleClickableBlock;
import com.chyzman.chowl.classes.AttackInteractionReceiver;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.LoggerFactory;

public class DoubleClickTracker {
    private static long lastClickTime = 0;
    private static BlockPos lastBlockPos = null;

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) return ActionResult.PASS;
            if (player.isSpectator()) return ActionResult.PASS;

            long duration = world.getTime() - lastClickTime;

            if (hitResult.getBlockPos().equals(lastBlockPos) && duration > 0 && duration <= 5) {
                player.swingHand(hand);
                Chowl.CHANNEL.clientHandle().send(new DoubleClickableBlock.DoubleClickPacket(hitResult));

                return ActionResult.FAIL;
            } else {
                lastClickTime = world.getTime();
                lastBlockPos = hitResult.getBlockPos().toImmutable();

                return ActionResult.PASS;
            }
        });
    }
}
