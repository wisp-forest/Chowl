package com.chyzman.chowl.core.ext;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface BreakProgressMaskingBlock {
    float calcMaskedBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos);
}
