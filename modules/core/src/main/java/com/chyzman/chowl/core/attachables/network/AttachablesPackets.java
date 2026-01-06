package com.chyzman.chowl.core.attachables.network;

import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerAttackAttachable;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerInteractAttachable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import static com.chyzman.chowl.core.network.ChowlPackets.CHANNEL;

public class AttachablesPackets {
    public static void registerCommon() {
        CHANNEL.addEndecs(builder -> {

        });

        CHANNEL.registerServerbound(C2SPlayerInteractAttachable.class, (message, access) -> {
            var player = access.player();
            if (player == null) return;
            var world = player.level();
            if (world == null) return;

            var attachableHolder = access.player().level().getAttachedOrCreate(AttachableHolder.TYPE);

            var container = attachableHolder.attachables.get(message.attachableUuid());
            if (container == null) return;

            if (container.getContained().onUse(world, player, message.hand(), new AttachableHitResult(message.pos(), container)) instanceof InteractionResult.Success success
                && success.swingSource() == InteractionResult.SwingSource.SERVER) {
                player.swing(message.hand(), true);
            }
        });

        CHANNEL.registerServerbound(C2SPlayerAttackAttachable.class, (message, access) -> {
            var player = access.player();
            if (player == null) return;
            var world = player.level();
            if (world == null) return;

            var attachableHolder = access.player().level().getAttachedOrCreate(AttachableHolder.TYPE);

            var container = attachableHolder.attachables.get(message.attachableUuid());
            if (container == null) return;

            if (container.getContained().onAttack(world, player, new AttachableHitResult(message.pos(), container)) instanceof InteractionResult.Success success
                && success.swingSource() == InteractionResult.SwingSource.SERVER) {
                player.swing(InteractionHand.MAIN_HAND, true);
            }
        });

    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {

    }
}
