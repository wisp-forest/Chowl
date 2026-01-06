package com.chyzman.chowl.electromechanics.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.ObserverBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObserverBlock.class)
public interface ObserverBlockAccessor {
    @Invoker("startSignal")
    void chowlElectromechanics$callStartSignal(LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos);
}
