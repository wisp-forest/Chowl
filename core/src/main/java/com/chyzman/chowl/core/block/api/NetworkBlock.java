package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.graph.NetworkMember;
import com.chyzman.chowl.core.graph.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlock extends Block {
    public NetworkBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world instanceof ServerWorld serverWorld && neighborState.getBlock() instanceof NetworkMember) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

}
