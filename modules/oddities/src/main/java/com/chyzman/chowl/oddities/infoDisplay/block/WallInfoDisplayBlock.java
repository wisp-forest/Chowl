package com.chyzman.chowl.oddities.infoDisplay.block;

import com.chyzman.chowl.oddities.infoDisplay.blockEntity.InfoDisplayBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WallInfoDisplayBlock<T extends InfoDisplayBlockEntity> extends AbstractInfoDisplayBlock<T> {
    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, Block.createCuboidShape(0, 0, 0, 1, 16, 16),
            Direction.SOUTH, Block.createCuboidShape(15, 0, 0, 16, 16, 16),
            Direction.EAST, Block.createCuboidShape(0, 0, 0, 16, 16, 1),
            Direction.WEST, Block.createCuboidShape(0, 0, 15, 16, 16, 16)
    );

    public WallInfoDisplayBlock(BlockEntityType<T> type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.offset(state.get(FACING).getOpposite())).isSolid();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Map.of(
                Direction.NORTH,Block.createCuboidShape(0, 0, 15, 16, 16, 16),
                Direction.SOUTH,  Block.createCuboidShape(0, 0, 0, 16, 16, 1),
                Direction.EAST, Block.createCuboidShape(0, 0, 0, 1, 16, 16),
                Direction.WEST, Block.createCuboidShape(15, 0, 0, 16, 16, 16)
        ).get(state.get(FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.with(FACING, direction2);
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState;
                }
            }
        }

        return null;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
}
