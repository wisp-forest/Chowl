package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow protected ClientWorld world;

    @WrapOperation(method = "addBlockBreakingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape addMegaBlockBreakingParticles(
            BlockState instance,
            BlockView blockView,
            BlockPos blockPos,
            Operation<VoxelShape> original,
            @Local BlockState state
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return original.call(instance, blockView, blockPos);
        return block.getFullShape(blockView, state, blockPos, ShapeContext.absent());
    }

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void addMegaBlockBreakParticles(
            BlockPos pos,
            BlockState state,
            CallbackInfo ci
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return;
        if (!block.localPos(state).equals(Vec3i.ZERO)) ci.cancel();
    }

    @WrapOperation(method = "addBlockBreakParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape addMegaBlockBreakParticles(
            BlockState instance,
            BlockView blockView,
            BlockPos blockPos,
            Operation<VoxelShape> original,
            @Local(argsOnly = true) BlockState state
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return original.call(instance, blockView, blockPos);
        if (!block.localPos(state).equals(Vec3i.ZERO)) return VoxelShapes.empty();
        return block.getFullShape(blockView, state, blockPos, ShapeContext.absent());
    }

    @WrapOperation(method = "method_34020", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"))
    private double addMegaBlockBreakParticles(
            double a, double b, Operation<Double> original,
            @Local(argsOnly = true) BlockState state,
            @Local(argsOnly = true) BlockPos blockPos
    ) {
        if (!(state.getBlock() instanceof MegaBlock)) return original.call(a, b);
        return b;
    }

    @WrapOperation(method = "method_34020", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int addMegaBlockBreakParticles(
            int a, int b, Operation<Integer> original,
            @Local(argsOnly = true) BlockState state,
            @Local(argsOnly = true) BlockPos blockPos
    ) {
        if (!(state.getBlock() instanceof MegaBlock)) return original.call(a, b);
        return Math.min(10, original.call(a, b));
    }
}
