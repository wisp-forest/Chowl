package com.chyzman.chowl.core.attachable.client;

import com.chyzman.chowl.core.attachable.Attachable;
import com.chyzman.chowl.core.attachable.AttachableHolder;
import com.chyzman.chowl.core.pond.MinecraftClientDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.SortedSet;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class AttachableRendererEvents {
    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx -> {
            var client = MinecraftClient.getInstance();

            var world = ctx.world();
            if (world == null) return;

            var attachables = world.getAttachedOrCreate(AttachableHolder.TYPE).attachables;
            if (attachables.isEmpty()) return;

            var dispatcher = ((MinecraftClientDuck) client).chowl$getAttachableRenderDispatcher();

            var matrices = ctx.matrixStack();
            if (matrices == null) return;

            for (Attachable attachable : attachables.values()) {
                matrices.push();

                matrices.translate( ctx.camera().getPos().multiply(-1));

                dispatcher.render(attachable, ctx.tickCounter().getTickDelta(false), matrices, ctx.consumers());

                matrices.pop();
            }
        });
    }
}
