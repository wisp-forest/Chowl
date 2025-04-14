package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

    @Shadow @Final private BlockColors blockColors;

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void renderMegaBlock(
            BlockState state,
            BlockPos pos,
            BlockRenderView world,
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            boolean cull,
            Random random,
            CallbackInfo ci
    ) {
//        if (!(state.getBlock() instanceof MegaBlock block)) return;
//        MinecraftClient.getInstance().player.sendMessage(Text.literal("Rendering MegaBlock at: " + pos.toShortString()), false);
//        var size = block.size(state);
//        matrices.translate(pos.getX(), pos.getY(), pos.getZ());
//        matrices.scale(size.getX(), size.getY(), size.getZ());
//        ci.cancel();
    }



    @Inject(method = "renderDamage", at = @At("HEAD"))
    private void renderMegaBlockDamage(
            BlockState state,
            BlockPos pos,
            BlockRenderView world,
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            CallbackInfo ci
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return;
        var origin = pos.subtract(block.localPos(state));
        matrices.translate(new Vec3d(origin.subtract(pos)));
        var size = block.size(state);
        matrices.scale(size.getX(), size.getY(), size.getZ());
    }
}
