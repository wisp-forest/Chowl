package com.chyzman.chowl.core.attachable.client;

import com.chyzman.chowl.core.attachable.AttachableContainer;
import com.chyzman.chowl.core.attachable.AttachableHolder;
import com.chyzman.chowl.core.pond.MinecraftClientDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class AttachableRendererEvents {
    public static final List<Vec3d> DEBUG_POSITIONS = new ArrayList<>();

    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            var client = MinecraftClient.getInstance();

            var world = ctx.world();
            if (world == null) return;


            if (client.options.hudHidden) {
                DEBUG_POSITIONS.clear();
            } else {
                var buffer = ctx.consumers().getBuffer(RenderLayer.getDebugLineStrip(10));
                Vec3d previousPos = null;
                for (Vec3d debugPosition : DEBUG_POSITIONS) {
                    if (previousPos == null) {
                        previousPos = debugPosition;
                        continue;
                    }
                    var start = previousPos.subtract(ctx.camera().getPos());
                    var end = debugPosition.subtract(ctx.camera().getPos());
                    buffer.vertex(ctx.matrixStack().peek(), start.toVector3f()).normal(0, 0, 1).color(1f, 1f, 0, 1);
                    buffer.vertex(ctx.matrixStack().peek(), end.toVector3f()).normal(0, 0, 1).color(1f, 1, 0, 1);
                    previousPos = debugPosition;
                }
            }

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
