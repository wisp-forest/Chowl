package com.chyzman.chowl.registry;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.graph.DestroyGraphPacket;
import com.chyzman.chowl.graph.SyncGraphPacket;
import com.chyzman.chowl.item.DrawerComponent;
import com.chyzman.chowl.network.C2SConfigPanel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.util.Hand;

import static com.chyzman.chowl.Chowl.CHANNEL;
import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public class ServerBoundPackets {
    public static void init() {
        PacketBufSerializer.register(
                DrawerComponent.DrawerConfig.class,
                (buf, config) -> {
                    buf.writeBoolean(config.locked);
                    buf.writeBoolean(config.hideCount);
                    buf.writeBoolean(config.hideName);
                    buf.writeBoolean(config.hideItem);
                    buf.writeString(GSON.toJson(config.textStyle));
                }, buf -> {
                    var config = new DrawerComponent.DrawerConfig();
                    config.locked = buf.readBoolean();
                    config.hideCount = buf.readBoolean();
                    config.hideName = buf.readBoolean();
                    config.hideItem = buf.readBoolean();
                    config.textStyle = GSON.fromJson(buf.readString(), Style.class);
                    return config;
                });


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