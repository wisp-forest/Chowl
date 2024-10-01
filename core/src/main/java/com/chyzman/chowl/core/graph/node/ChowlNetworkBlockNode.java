package com.chyzman.chowl.core.graph.node;

import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.chyzman.chowl.core.graph.UpdateHandler;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

public interface ChowlNetworkBlockNode extends FullWireBlockNode {
    @Override
    default void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self) {
        var graph = self.getGraphWorld().getGraph(self.getGraphId());
        if (graph != null) graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE).scheduleUpdate(UpdateHandler.ChangeType.CONTENT);
    }

    default void update(ServerWorld world, NodeHolder<BlockNode> node) {
    }
}
