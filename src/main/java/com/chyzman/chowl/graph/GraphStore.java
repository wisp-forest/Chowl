package com.chyzman.chowl.graph;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GraphStore {
    static GraphStore get(World world) {
        if (world.isClient) {
            return ClientGraphStore.STORE;
        } else {
            return ServerGraphStore.get((ServerWorld) world);
        }
    }

    @Nullable Graph getGraphFor(BlockPos pos);

    interface Graph {
        Collection<? extends GraphNode> nodes();
    }

    interface GraphNode {
        BlockPos pos();
        BlockState state();
    }
}
