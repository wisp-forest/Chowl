package com.chyzman.chowl.core.network;

import com.chyzman.chowl.core.ChowlCore;
import com.chyzman.chowl.core.ext.AttackInteractionReceiver;
import com.chyzman.chowl.core.ext.DoubleClickableBlock;
import com.chyzman.chowl.core.util.ChannelUtil;
import eu.pb4.common.protection.api.CommonProtection;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ChowlCoreNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(ChannelUtil.getChannelId(ChowlCore.MODID));

    public static void init() {
        CHANNEL.registerServerbound(DoubleClickC2SPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getWorld();
            BlockPos pos = message.hitResult().getBlockPos();

            var state = world.getBlockState(pos);

            if (!CommonProtection.canInteractBlock(world, pos, player.getGameProfile(), player)) {
                // TODO: tell client interaction failed.
                return;
            }

            DoubleClickableBlock.doDoubleClick(world, state, message.hitResult(), player);
            player.swingHand(Hand.MAIN_HAND);
        });

        CHANNEL.registerServerbound(AttackInteractionC2SPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getWorld();
            BlockPos pos = message.hitResult().getBlockPos();

            var state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof AttackInteractionReceiver receiver)) return;

            if (!CommonProtection.canInteractBlock(world, pos, player.getGameProfile(), player)) {
                // TODO: tell client interaction failed.
                return;
            }

            receiver.onAttack(world, state, message.hitResult(), player);
            player.swingHand(Hand.MAIN_HAND);
        });
    }
}
