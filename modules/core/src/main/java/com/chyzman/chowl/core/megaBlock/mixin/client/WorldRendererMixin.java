package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @WrapOperation(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape getMegaBlockShape(
            BlockState instance,
            BlockView blockView,
            BlockPos blockPos,
            ShapeContext shapeContext,
            Operation<VoxelShape> original,
            @Local(argsOnly = true) BlockState state
    ) {
        if (!(state.getBlock() instanceof MegaBlock block)) return original.call(instance, blockView, blockPos, shapeContext);
        return block.getFullShape(blockView, state, blockPos, shapeContext);
    }
}
