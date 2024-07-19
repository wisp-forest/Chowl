package com.chyzman.chowl.industries.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ExtendedParticleSpriteBlock {
    BlockState getParticleState(World world, BlockPos pos, BlockState state);
}
