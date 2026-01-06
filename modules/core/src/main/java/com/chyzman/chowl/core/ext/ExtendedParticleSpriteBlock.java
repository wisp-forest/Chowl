package com.chyzman.chowl.core.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ExtendedParticleSpriteBlock {
    BlockState getParticleState(Level world, BlockPos pos, BlockState state);
}
