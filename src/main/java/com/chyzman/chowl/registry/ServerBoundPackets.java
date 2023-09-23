package com.chyzman.chowl.registry;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import net.minecraft.util.Hand;

import static com.chyzman.chowl.Chowl.CHANNEL;

public class ServerBoundPackets {
    public static void init() {
        CHANNEL.registerServerbound(AttackInteractionReceiver.InteractionPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getWorld();

            var state = world.getBlockState(message.hitResult().getBlockPos());
            if (!(state.getBlock() instanceof AttackInteractionReceiver receiver)) return;

            receiver.onAttack(world, state, message.hitResult(), player);
            player.swingHand(Hand.MAIN_HAND);
        });
    }
}