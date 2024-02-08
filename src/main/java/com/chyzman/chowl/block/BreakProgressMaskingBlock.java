package com.chyzman.chowl.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface BreakProgressMaskingBlock {
    float calcMaskedBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos);
}
