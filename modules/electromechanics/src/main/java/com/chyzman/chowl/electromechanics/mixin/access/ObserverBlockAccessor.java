package com.chyzman.chowl.electromechanics.mixin.access;

import net.minecraft.block.ObserverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObserverBlock.class)
public interface ObserverBlockAccessor {
    @Invoker("scheduleTick")
    void chowlElectromechanics$callScheduleTick(WorldView world, ScheduledTickView tickView, BlockPos pos);
}
