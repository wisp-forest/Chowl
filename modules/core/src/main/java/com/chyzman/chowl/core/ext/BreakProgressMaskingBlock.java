package com.chyzman.chowl.core.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BreakProgressMaskingBlock {
    float calcMaskedBlockBreakingDelta(BlockState state, Player player, BlockGetter world, BlockPos pos);
}
