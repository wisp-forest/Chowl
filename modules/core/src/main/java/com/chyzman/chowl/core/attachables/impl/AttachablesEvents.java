package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

@SuppressWarnings("UnstableApiUsage")
public class AttachablesEvents {

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            var gameRenderer = ctx.gameRenderer();
            if (gameRenderer == null) return;

            var client = gameRenderer.getClient();

            var world = client.world;
            if (world == null) return;

            var attachables = world.getAttachedOrCreate(AttachableHolder.TYPE).attachables;
            if (attachables.isEmpty()) return;

            var dispatcher = ((MinecraftClientDuck) client).chowl$getAttachableRenderDispatcher();

            var matrices = ctx.matrices();

            for (AttachableContainer container : attachables.values()) {
                matrices.push();

                matrices.translate(gameRenderer.getCamera().getCameraPos().multiply(-1));

                //TODO: idk if this should be fixed or dynamic
                dispatcher.render(container.getContained(), client.getRenderTickCounter().getFixedDeltaTicks(), matrices, ctx.consumers());

                matrices.pop();
            }
        });
    }
}
