package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("UnstableApiUsage")
public class AttachablesEvents {

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            var client = MinecraftClient.getInstance();

            var world = ctx.world();
            if (world == null) return;

            var attachables = world.getAttachedOrCreate(AttachableHolder.TYPE).attachables;
            if (attachables.isEmpty()) return;

            var dispatcher = ((MinecraftClientDuck) client).chowl$getAttachableRenderDispatcher();

            var matrices = ctx.matrixStack();
            if (matrices == null) return;

            for (AttachableContainer container : attachables.values()) {
                matrices.push();

                matrices.translate( ctx.camera().getPos().multiply(-1));

                dispatcher.render(container.getContained(), ctx.tickCounter().getTickDelta(false), matrices, ctx.consumers());

                matrices.pop();
            }
        });
    }
}
