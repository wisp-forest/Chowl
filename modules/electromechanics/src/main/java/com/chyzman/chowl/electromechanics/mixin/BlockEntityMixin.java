package com.chyzman.chowl.electromechanics.mixin;

import com.chyzman.chowl.electromechanics.block.WatcherBlock;
import com.chyzman.chowl.electromechanics.mixin.access.ObserverBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "setChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("HEAD"))
    private static void updateWatchers(Level level, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        for (Direction direction : Direction.values()) {
            var neighbor = pos.relative(direction);
            var neighborState = level.getBlockState(neighbor);
            if (!(neighborState.getBlock() instanceof WatcherBlock watcher)) continue;
            if (neighborState.getValue(WatcherBlock.FACING) != direction.getOpposite()) continue;
            ((ObserverBlockAccessor) watcher).chowlElectromechanics$callStartSignal(level, level, neighbor);
        }
    }
}
