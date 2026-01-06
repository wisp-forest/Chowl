package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.graph.NetworkMember;
import com.chyzman.chowl.core.graph.NetworkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlock extends Block {
    public NetworkBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (world instanceof ServerLevel serverWorld && neighborState.getBlock() instanceof NetworkMember) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world instanceof ServerLevel serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
        NetworkRegistry.UNIVERSE.getGraphWorld(world).updateNodes(pos);
    }

}
