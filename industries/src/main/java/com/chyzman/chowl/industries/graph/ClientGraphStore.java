package com.chyzman.chowl.industries.graph;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.math.BlockPos;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class ClientGraphStore implements GraphStore {
    public static final ClientGraphStore STORE = new ClientGraphStore();

    private final Map<UUID, SyncGraphPacket> graphs = new HashMap<>();
    private final Long2ObjectMap<UUID> blockToGraph = new Long2ObjectOpenHashMap<>();
//    private int gcTick = 0;

    private ClientGraphStore() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            graphs.clear();
            blockToGraph.clear();
        });

//        ClientTickEvents.END_WORLD_TICK.register(world -> {
//            gcTick++;
//            if (gcTick < 80) return;
//            gcTick = 0;
//
//            graphs.values().removeIf(graph -> {
//                for (var node : graph.nodes()) {
//                    if (world.getChunkManager().isChunkLoaded(node.pos().getX() >> 4, node.pos().getZ() >> 4)) {
//                        return false;
//                    }
//                }
//
//                for (var node : graph.nodes()) {
//                    blockToGraph.remove(node.pos().asLong());
//                }
//
//                return true;
//            });
//        });
    }

    public static void init() { }

    public void insert(SyncGraphPacket packet) {
        graphs.put(packet.graphId(), packet);

        for (var node : packet.nodes()) {
            blockToGraph.put(node.pos().asLong(), packet.graphId());
        }
    }

    public void remove(UUID graphId) {
        var graph = graphs.remove(graphId);
        if (graph == null) return;

        for (var node : graph.nodes()) {
            blockToGraph.remove(node.pos().asLong());
        }
    }

    @Override
    public SyncGraphPacket getGraphFor(BlockPos pos) {
        var graphId = blockToGraph.get(pos.asLong());
        if (graphId == null) return null;

        return graphs.get(graphId);
    }
}
