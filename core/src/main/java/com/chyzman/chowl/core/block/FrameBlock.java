package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.block.api.NetworkBlockWithEntity;
import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.chyzman.chowl.core.registry.ChowlCoreBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FrameBlock extends NetworkBlockWithEntity<FrameBlockEntity> {
    public FrameBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockEntityType<FrameBlockEntity> getType() {
        return ChowlCoreBlocks.Entities.FRAME;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FrameBlockEntity(pos, state);
    }
}
