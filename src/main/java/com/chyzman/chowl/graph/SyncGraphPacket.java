package com.chyzman.chowl.graph;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public record SyncGraphPacket(UUID graphId, List<Node> nodes) implements GraphStore.Graph {
    public record Node(BlockPos pos, BlockState state, long[] links) implements GraphStore.GraphNode { }
}
