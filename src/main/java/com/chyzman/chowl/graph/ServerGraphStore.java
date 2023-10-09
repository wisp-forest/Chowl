package com.chyzman.chowl.graph;

import com.chyzman.chowl.Chowl;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// I tried to look at how Kneelawk's graph library, and died inside from how complex it is.
// - Basique
public class ServerGraphStore extends PersistentState implements GraphStore {
    private final ServerWorld world;
    private final Map<UUID, GraphEntry> graphs = new HashMap<>();
    private final Long2ObjectMap<UUID> blockToGraph = new Long2ObjectOpenHashMap<>();

    private ServerGraphStore(ServerWorld world) {
        this.world = world;
    }

    private ServerGraphStore(ServerWorld world, NbtCompound tag) {
        this(world);

        NbtList graphsTag = tag.getList("Graphs", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < graphsTag.size(); i++) {
            NbtCompound graphTag = graphsTag.getCompound(i);
            GraphEntry graph = new GraphEntry(graphTag);

            graphs.put(graph.graphId, graph);
        }
    }

    public static ServerGraphStore get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            tag -> new ServerGraphStore(world, tag),
            () -> new ServerGraphStore(world),
            "chowl_graph"
        );
    }

    public Map<UUID, GraphEntry> graphs() {
        return graphs;
    }

    public void syncAllWith(ServerPlayerEntity player) {
        for (var graph : graphs.values()) {
            Chowl.CHANNEL.serverHandle(player).send(graph.toPacket());
        }
    }

    public void clear() {
        graphs.clear();
        blockToGraph.clear();
    }

    public void tryRemove(BlockPos pos) {
        var graphId = blockToGraph.get(pos.asLong());
        if (graphId == null) return;

        var graph = graphs.get(graphId);
        if (graph == null) return;

        var node = graph.nodes.get(pos.asLong());
        if (node == null) return;

        graph.removeAndSplitBy(node);

        if (graph.nodes.size() == 0) {
            graphs.remove(graphId);
            graph.syncRemove();
        }
    }

    public void tryAdd(BlockPos pos, BlockState state, Set<BlockPos> links) {
        UUID graphId = null;
        for (var link : links) {
            UUID linkGraphId = blockToGraph.get(link.asLong());

            if (linkGraphId == null) continue;

            graphId = linkGraphId;
            break;
        }

        if (graphId == null) {
            graphId = UUID.randomUUID();
            graphs.put(graphId, new GraphEntry(graphId, new Long2ObjectOpenHashMap<>()));
        }

        GraphEntry graph = graphs.get(graphId);
        graph.insert(pos, state, links);
    }

    public @Nullable GraphEntry getGraphFor(BlockPos pos) {
        var graphId = blockToGraph.get(pos.asLong());
        if (graphId == null) return null;

        return graphs.get(graphId);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList graphsTag = new NbtList();
        tag.put("Graphs", graphsTag);

        for (var graph : graphs.values()) {
            graphsTag.add(graph.write(new NbtCompound()));
        }

        return tag;
    }

    public class GraphEntry implements GraphStore.Graph {
        public final UUID graphId;
        public final Long2ObjectOpenHashMap<GraphNodeEntry> nodes;

        public GraphEntry(UUID graphId, Long2ObjectOpenHashMap<GraphNodeEntry> nodes) {
            this.graphId = graphId;
            this.nodes = nodes;
        }

        private GraphEntry(NbtCompound tag) {
            this.graphId = tag.getUuid("UUID");

            NbtList nodesTag = tag.getList("Nodes", NbtElement.COMPOUND_TYPE);
            this.nodes = new Long2ObjectOpenHashMap<>(nodesTag.size());

            for (int i = 0; i < nodesTag.size(); i++) {
                NbtCompound nodeTag = nodesTag.getCompound(i);
                var node = GraphNodeEntry.read(nodeTag);

                ServerGraphStore.this.blockToGraph.put(node.pos.asLong(), this.graphId);

                this.nodes.put(node.pos.asLong(), node);
            }
        }

        public NbtCompound write(NbtCompound tag) {
            tag.putUuid("UUID", graphId);

            NbtList nodesTag = new NbtList();
            tag.put("Nodes", nodesTag);

            for (var node : nodes.values()) {
                nodesTag.add(node.write(new NbtCompound()));
            }

            return tag;
        }

        public Set<ServerPlayerEntity> trackingPlayers() {
            Set<ChunkPos> chunkTargets = new HashSet<>();

            for (var node : nodes.values()) {
                chunkTargets.add(new ChunkPos(node.pos()));
            }

            Set<ServerPlayerEntity> targetPlayers = new HashSet<>();

            for (var chunkPos : chunkTargets) {
                targetPlayers.addAll(PlayerLookup.tracking(ServerGraphStore.this.world, chunkPos));
            }

            return targetPlayers;
        }

        public void sync() {
            Chowl.CHANNEL.serverHandle(trackingPlayers()).send(toPacket());
        }

        public void syncRemove() {
            Chowl.CHANNEL.serverHandle(trackingPlayers()).send(new DestroyGraphPacket(graphId));
        }

        public SyncGraphPacket toPacket() {
            var networkNodes = new ArrayList<SyncGraphPacket.Node>();

            for (var node : nodes.values()) {
                networkNodes.add(new SyncGraphPacket.Node(node.pos(), node.state(), node.links().toLongArray()));
            }

            return new SyncGraphPacket(graphId, networkNodes);
        }

        public GraphNodeEntry insert(BlockPos pos, BlockState state, Set<BlockPos> links) {
            GraphNodeEntry entry = new GraphNodeEntry(pos, state, new LongOpenHashSet());

            nodes.put(entry.pos.asLong(), entry);
            ServerGraphStore.this.blockToGraph.put(pos.asLong(), graphId);

            for (var linkPos : links) {
                entry.links.add(linkPos.asLong());

                var linkGraphId = ServerGraphStore.this.blockToGraph.get(linkPos.asLong());
                if (linkGraphId != null && linkGraphId != graphId) {
                    mergeIn(ServerGraphStore.this.graphs.get(linkGraphId));
                }

                var other = nodes.get(linkPos.asLong());
                if (other != null) other.links.add(pos.asLong());
            }

            sync();

            return entry;
        }

        public void mergeIn(GraphEntry other) {
            for (var node : other.nodes.values()) {
                this.nodes.put(node.pos.asLong(), node);
                ServerGraphStore.this.blockToGraph.put(node.pos.asLong(), this.graphId);
            }

            ServerGraphStore.this.graphs.values().remove(other);
            other.syncRemove();
        }

        public void removeAndSplitBy(GraphNodeEntry node) {
            nodes.remove(node.pos.asLong());
            ServerGraphStore.this.blockToGraph.remove(node.pos.asLong());
            for (var other : nodes.values()) {
                other.links().remove(node.pos().asLong());
            }

            if (nodes.size() == 0) return;

            while (nodes.size() > 0) {
                var graphEntries = new Long2ObjectOpenHashMap<GraphNodeEntry>();
                nodes.long2ObjectEntrySet().iterator().next().getValue().dfs(graphEntries, nodes);

                var graph = new GraphEntry(UUID.randomUUID(), graphEntries);
                for (var subEntry : graphEntries.values()) {
                    ServerGraphStore.this.blockToGraph.put(subEntry.pos.asLong(), graph.graphId);
                }
                graph.sync();
                ServerGraphStore.this.graphs.put(graph.graphId, graph);
            }
        }

        @Override
        public Collection<GraphNodeEntry> nodes() {
            return nodes.values();
        }
    }

    public record GraphNodeEntry(BlockPos pos, BlockState state, LongSet links) implements GraphStore.GraphNode {

        public static GraphNodeEntry read(NbtCompound tag) {
            BlockPos pos = BlockPos.fromLong(tag.getLong("Pos"));
            BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("State"));
            LongSet links = new LongOpenHashSet(tag.getLongArray("Links"));

            return new GraphNodeEntry(pos, state, links);
        }

        public NbtCompound write(NbtCompound tag) {
            tag.putLong("Pos", pos.asLong());
            tag.put("State", NbtHelper.fromBlockState(state));
            tag.putLongArray("Links", links.toLongArray());
            return tag;
        }

        private void dfs(Long2ObjectMap<GraphNodeEntry> visited, Long2ObjectMap<GraphNodeEntry> removeFrom) {
            visited.put(pos.asLong(), this);
            removeFrom.remove(pos.asLong());

            for (var link : links) {
                if (visited.containsKey(link.longValue())) continue;

                var linked = removeFrom.get(link.longValue());
                if (linked == null) continue;

                linked.dfs(visited, removeFrom);
            }
        }
    }
}
