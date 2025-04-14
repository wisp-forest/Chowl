package com.chyzman.chowl.oddities.infoDisplay.block;

import com.chyzman.chowl.oddities.infoDisplay.blockEntity.InfoDisplayBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInfoDisplayBlock<T extends InfoDisplayBlockEntity> extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private final BlockEntityType<T> type;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    protected AbstractInfoDisplayBlock(BlockEntityType<T> type, Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getDefaultState()
                        .with(WATERLOGGED, false)
                        .with(FACING, Direction.NORTH)
        );
        this.type = type;
        type.addSupportedBlock(this);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfoDisplayBlockEntity(type, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}
