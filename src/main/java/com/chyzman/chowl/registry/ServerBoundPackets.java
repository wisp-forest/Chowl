package com.chyzman.chowl.registry;

import com.chyzman.chowl.block.DoubleClickableBlock;
import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.graph.DestroyGraphPacket;
import com.chyzman.chowl.graph.SyncGraphPacket;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import eu.pb4.common.protection.api.CommonProtection;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Style;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static com.chyzman.chowl.Chowl.CHANNEL;

public class ServerBoundPackets {
    public static void init() {
        PacketBufSerializer.register(
                DisplayingPanelItem.Config.class,
                (buf, config) -> {
                    buf.writeBoolean(config.hideCount());
                    buf.writeBoolean(config.hideCapacity());
                    buf.writeBoolean(config.hideName());
                    buf.writeBoolean(config.hideItem());
                    buf.writeBoolean(config.showPercentage());
                    buf.writeBoolean(config.hideUpgrades());
                    buf.writeBoolean(config.hideButtons());
                    buf.writeBoolean(config.ignoreTemplating());
                    buf.encode(NbtOps.INSTANCE, Style.CODEC, config.textStyle());
                }, buf -> {
                    var config = new DisplayingPanelItem.Config();

                    config.hideCount(buf.readBoolean());
                    config.hideCapacity(buf.readBoolean());
                    config.hideName(buf.readBoolean());
                    config.hideItem(buf.readBoolean());
                    config.showPercentage(buf.readBoolean());
                    config.hideUpgrades(buf.readBoolean());
                    config.hideButtons(buf.readBoolean());
                    config.ignoreTemplating(buf.readBoolean());
                    config.textStyle(buf.decode(NbtOps.INSTANCE, Style.CODEC));

                    return config;
                }
        );


        PacketBufSerializer.register(
                BlockState.class,
                (buf, state) -> buf.writeRegistryValue(Block.STATE_IDS, state),
                buf -> buf.readRegistryValue(Block.STATE_IDS)
        );

        CHANNEL.registerServerbound(AttackInteractionReceiver.InteractionPacket.class, (message, access) -> {
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
        CHANNEL.registerServerbound(DoubleClickableBlock.DoubleClickPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getWorld();
            BlockPos pos = message.hitResult().getBlockPos();

            var state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof DoubleClickableBlock receiver)) return;

            if (!CommonProtection.canInteractBlock(world, pos, player.getGameProfile(), player)) {
                // TODO: tell client interaction failed.
                return;
            }

            receiver.onDoubleClick(world, state, message.hitResult(), player);
            player.swingHand(Hand.MAIN_HAND);
        });

        CHANNEL.registerClientboundDeferred(SyncGraphPacket.class);
        CHANNEL.registerClientboundDeferred(DestroyGraphPacket.class);
    }
}