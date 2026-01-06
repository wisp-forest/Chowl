package com.chyzman.chowl.core.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public interface ExtendedSoundGroupBlock {
    SoundType getSoundGroup(Level world, BlockPos pos, BlockState state);

    default SoundType getSoundGroup(Level world, BlockPos pos, BlockState state, ItemStack stack) {
        return getSoundGroup(world, pos, state);
    }
}
