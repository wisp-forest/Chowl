package com.chyzman.chowl.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public DrawerFrameBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.FALSE));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DrawerFrameBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.PASS;
        } else if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            if (stacks[hit.getSide().getId()].isEmpty()) {
                stacks[hit.getSide().getId()] = player.getStackInHand(hand);
                player.setStackInHand(hand, ItemStack.EMPTY);
                drawerFrameBlockEntity.markDirty();
                return ActionResult.SUCCESS;
            } else {
                if (player.getStackInHand(hand).isEmpty()) {
                    player.setStackInHand(hand, stacks[hit.getSide().getId()]);
                    stacks[hit.getSide().getId()] = ItemStack.EMPTY;
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }
        }
        return ActionResult.FAIL;
    }
}