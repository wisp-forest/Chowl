package com.chyzman.chowl.core.block.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlockWithEntity<T extends BlockEntity> extends NetworkBlock implements EntityBlock {
    protected NetworkBlockWithEntity(Properties settings) {
        super(settings);
    }

    protected abstract BlockEntityType<T> getType();

    @Nullable
    protected final T getBlockEntity(Level world, BlockPos pos) {
        return getType().getBlockEntity(world, pos);
    }
}
