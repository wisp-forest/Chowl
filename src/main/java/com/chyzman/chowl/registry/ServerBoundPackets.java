package com.chyzman.chowl.registry;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.graph.DestroyGraphPacket;
import com.chyzman.chowl.graph.SyncGraphPacket;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

import static com.chyzman.chowl.Chowl.CHANNEL;

public class ServerBoundPackets {
    public static void init() {
        PacketBufSerializer.register(BlockState.class,
            (buf, state) -> buf.writeRegistryValue(Block.STATE_IDS, state),
            buf -> buf.readRegistryValue(Block.STATE_IDS));

        CHANNEL.registerServerbound(AttackInteractionReceiver.InteractionPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getWorld();

            var state = world.getBlockState(message.hitResult().getBlockPos());
            if (!(state.getBlock() instanceof AttackInteractionReceiver receiver)) return;

            receiver.onAttack(world, state, message.hitResult(), player);
            player.swingHand(Hand.MAIN_HAND);
        });

        CHANNEL.registerClientboundDeferred(SyncGraphPacket.class);
        CHANNEL.registerClientboundDeferred(DestroyGraphPacket.class);
    }
}