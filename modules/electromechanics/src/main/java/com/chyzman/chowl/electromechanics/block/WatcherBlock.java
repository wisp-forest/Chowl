package com.chyzman.chowl.electromechanics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WatcherBlock extends ObserverBlock {
    public WatcherBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(
        BlockState blockState,
        LevelReader levelReader,
        ScheduledTickAccess scheduledTickAccess,
        BlockPos blockPos,
        Direction direction,
        BlockPos blockPos2,
        BlockState blockState2,
        RandomSource randomSource
    ) {
        return blockState;
    }
}
