package com.chyzman.chowl.oddities.block;

import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import com.chyzman.chowl.oddities.screen.TestScreen;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ClipboardBlock extends BlockWithEntity implements Waterloggable {
    public static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final Map<Orientation, VoxelShape> SHAPES = Util.make(Maps.newEnumMap(Orientation.class), (map) -> {
        map.put(Orientation.DOWN_EAST, VoxelShapes.union(
                Block.createCuboidShape(0, 15, 2, 15, 16, 14),
                Block.createCuboidShape(14, 15, 6, 16, 16, 10)
        ));
        map.put(Orientation.DOWN_WEST, VoxelShapes.union(
                Block.createCuboidShape(1,15, 2, 16, 16, 14),
                Block.createCuboidShape(0, 15, 6, 2, 16, 10)
        ));

        map.put(Orientation.DOWN_NORTH, VoxelShapes.union(
                Block.createCuboidShape(2, 15, 1, 14, 16, 16),
                Block.createCuboidShape(6, 15, 0, 10, 16, 1)
        ));
        map.put(Orientation.DOWN_SOUTH, VoxelShapes.union(
                Block.createCuboidShape(2, 15, 0, 14, 16, 15),
                Block.createCuboidShape(6, 15, 15, 10, 16, 16)
        ));

        map.put(Orientation.UP_EAST, VoxelShapes.union(
                Block.createCuboidShape(0, 0, 2, 15, 1, 14),
                Block.createCuboidShape(14, 0, 6, 16, 1, 10)
        ));
        map.put(Orientation.UP_WEST, VoxelShapes.union(
                Block.createCuboidShape(1,0, 2, 16, 1, 14),
                Block.createCuboidShape(0, 0, 6, 2, 1, 10)
        ));

        map.put(Orientation.UP_NORTH, VoxelShapes.union(
                Block.createCuboidShape(2, 0, 1, 14, 1, 16),
                Block.createCuboidShape(6, 0, 0, 10, 1, 1)
        ));
        map.put(Orientation.UP_SOUTH, VoxelShapes.union(
                Block.createCuboidShape(2, 0, 0, 14, 1, 15),
                Block.createCuboidShape(6, 0, 15, 10, 1, 16)
        ));

        map.put(Orientation.EAST_UP, VoxelShapes.union(
                Block.createCuboidShape(0, 0, 2, 1, 15, 14),
                Block.createCuboidShape(0, 15, 6, 1, 16, 10)
        ));

        map.put(Orientation.WEST_UP, VoxelShapes.union(
                Block.createCuboidShape(15, 0, 2, 16, 15, 14),
                Block.createCuboidShape(15, 15, 6, 16, 16, 10)
        ));

        map.put(Orientation.NORTH_UP, VoxelShapes.union(
                Block.createCuboidShape(2, 0, 15, 14, 15, 16),
                Block.createCuboidShape(6, 15, 15, 10, 16, 16)
        ));

        map.put(Orientation.SOUTH_UP, VoxelShapes.union(
                Block.createCuboidShape(2, 0, 0, 14, 15, 1),
                Block.createCuboidShape(6, 15, 0, 10, 16, 1)
        ));
    });

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    public ClipboardBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getDefaultState()
                        .with(ORIENTATION, Orientation.NORTH_UP)
                        .with(WATERLOGGED, false)
        );
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClipboardBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ORIENTATION, WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = this.getDefaultState();

        for (Direction direction : ctx.getPlacementDirections()) {
            var facing = direction.getOpposite();
            Direction rotation;
            if (facing.getAxis() == Direction.Axis.Y) {
                rotation = ctx.getHorizontalPlayerFacing();
                if (facing.equals(Direction.DOWN)) rotation = rotation.getOpposite();
            } else {
                rotation = Direction.UP;
            }
            state = state.with(ORIENTATION, Orientation.byDirections(facing, rotation));
            if (state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) return state.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.offset(state.get(ORIENTATION).getFacing().getOpposite())).isSolid();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(ORIENTATION));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return direction == state.get(ORIENTATION).getFacing().getOpposite() && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) MinecraftClient.getInstance().setScreen(new TestScreen());
        return ActionResult.SUCCESS;
    }
}
