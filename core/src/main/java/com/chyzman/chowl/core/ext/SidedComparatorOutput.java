package com.chyzman.chowl.core.ext;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface SidedComparatorOutput {
    int getSidedComparatorOutput(BlockState state, World world, BlockPos pos, Direction side);
}
