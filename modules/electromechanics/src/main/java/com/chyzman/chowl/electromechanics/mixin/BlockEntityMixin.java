package com.chyzman.chowl.electromechanics.mixin;

import com.chyzman.chowl.electromechanics.block.WatcherBlock;
import com.chyzman.chowl.electromechanics.mixin.access.ObserverBlockAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "markDirty(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"))
    private static void updateWatchers(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        for (Direction direction : Direction.values()) {
            var neighbor = pos.offset(direction);
            var neighborState = world.getBlockState(neighbor);
            if (!(neighborState.getBlock() instanceof WatcherBlock watcher)) continue;
            if (neighborState.get(WatcherBlock.FACING) != direction.getOpposite()) continue;
            ((ObserverBlockAccessor) watcher).chowlElectromechanics$callScheduleTick(world, world, neighbor);
        }
    }
}
