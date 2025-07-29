package com.chyzman.chowl.oddities.block;

import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.oddities.blockentity.ClipboardBlockEntity;
import com.chyzman.chowl.oddities.screen.ClipboardScreen;
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

public class ClipboardBlock extends MultipartHolderBlockWithEntity implements Waterloggable {
    public static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(2, 0, 0, 14, 15, 1),
        Block.createCuboidShape(6, 15, 0, 10, 16, 1)
    );

    private static final Map<Orientation, VoxelShape> SHAPES = Util.make(Maps.newEnumMap(Orientation.class), (map) -> {
        for (Orientation orientation : Orientation.values()) map.put(orientation, VoxelShapeHelper.rotate(SHAPE, orientation));
    });

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    public ClipboardBlock(Settings settings) {
        super(ClipboardBlockEntity::new, settings);
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
                rotation = ctx.getHorizontalPlayerFacing().getOpposite();
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
    protected VoxelShape getBlockOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
    protected ActionResult onNonPartUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            if (world.getBlockEntity(pos) instanceof ClipboardBlockEntity clipboard) {
                MinecraftClient.getInstance().setScreen(new ClipboardScreen(clipboard));
            }

        }
        return ActionResult.SUCCESS;
    }
}
