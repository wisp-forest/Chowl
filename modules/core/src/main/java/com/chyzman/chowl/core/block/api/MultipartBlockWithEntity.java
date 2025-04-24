package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.blockentity.api.MultipartBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class MultipartBlockWithEntity extends BlockWithEntity {
    private final BlockEntityFactory<?> factory;

    protected MultipartBlockWithEntity(BlockEntityFactory<?> factory, Settings settings) {
        super(settings);
        this.factory = factory;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return factory.create(pos, state);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends MultipartBlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
