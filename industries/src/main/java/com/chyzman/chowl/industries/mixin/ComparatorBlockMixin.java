package com.chyzman.chowl.industries.mixin;

import com.chyzman.chowl.industries.block.SidedComparatorOutput;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin {
    @WrapOperation(method = "getPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getComparatorOutput(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I"))
    private int sidedComparator(BlockState instance, World world, BlockPos blockPos, Operation<Integer> original, World world2, BlockPos comparatorPos, BlockState comparatorState) {
        if (instance.getBlock() instanceof SidedComparatorOutput sided) {
            return sided.getSidedComparatorOutput(instance, world, blockPos, comparatorState.get(Properties.HORIZONTAL_FACING).getOpposite());
        } else {
            return original.call(instance, world, blockPos);
        }
    }
}
