package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SectionBuilder.class)
public abstract class SectionBuilderMixin {

    @Inject(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"), cancellable = true)
    private void renderMegaBlock(
            ChunkSectionPos sectionPos,
            ChunkRendererRegion renderRegion,
            VertexSorter vertexSorter,
            BlockBufferAllocatorStorage allocatorStorage,
            CallbackInfoReturnable<SectionBuilder.RenderData> cir,
            @Local BlockState state,
            @Local MatrixStack matrices
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return;
        var size = block.size(state);
        matrices.translate(new Vec3d(Vec3i.ZERO.subtract(block.localPos(state))));
        matrices.scale(size.getX(), size.getY(), size.getZ());
    }
}
