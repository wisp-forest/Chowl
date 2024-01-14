package com.chyzman.chowl.block;

import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ExtendedSoundGroupBlock {
    BlockSoundGroup getSoundGroup(World world, BlockPos pos, BlockState state);
}
