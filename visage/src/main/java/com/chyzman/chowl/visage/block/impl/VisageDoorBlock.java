package com.chyzman.chowl.visage.block.impl;

import com.chyzman.chowl.visage.block.VisageBlockEntity;
import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VisageDoorBlock extends DoorBlock implements VisageBlockTemplate {

    public VisageDoorBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    //region visage template
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage) {
            visage.spreadTemplate();
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return doOnUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        doRandomDisplayTick(state, world, pos, random);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage &&
            !(visage.templateState() == null) &&
            !(visage.templateState().getBlock() instanceof VisageBlockTemplate)) {
            return visage.templateState().isTransparent(world, pos);
        }
        return super.isTransparent(state, world, pos);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
    //endregion
}
