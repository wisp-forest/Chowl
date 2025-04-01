package com.chyzman.chowl.oddities.mixin;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(CollisionView.class)
public interface CollisionViewMixin {

    @ModifyReturnValue(method = "getBlockCollisions", at = @At("RETURN"))
    private Iterable<VoxelShape> injectExtraCollisions(Iterable<VoxelShape> original, Entity entity, Box box) {
//        if (entity != null && entity.isSneaking()) return original;
        var shape = VoxelShapes.cuboid(
                -5,
                100,
                -5,
                5,
                101,
                5
        );

        return VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(box), BooleanBiFunction.AND)
                ? Iterables.concat(original, List.of(shape))
                : original;
    }

}
