package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V", at = @At("HEAD"), cancellable = true)
    private void renderMegaBlock(
            BlockRenderView world,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            boolean cull,
            Random random,
            long seed,
            int overlay,
            CallbackInfo ci
    ) {
//        if (!(state.getBlock() instanceof MegaBlock block)) return;
//        var size = block.size(state);
//        matrices.translate(new Vec3d(pos.subtract(block.localPos(state))));
//        matrices.scale(size.getX(), size.getY(), size.getZ());
    }
}
