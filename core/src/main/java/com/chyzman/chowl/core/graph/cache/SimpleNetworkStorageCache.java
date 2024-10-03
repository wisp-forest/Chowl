package com.chyzman.chowl.core.graph.cache;

import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.LinkEntity;
import com.kneelawk.graphlib.api.graph.user.NodeEntity;
import com.kneelawk.graphlib.api.util.LinkPos;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleNetworkStorageCache implements NetworkStorageCache {
    private GraphEntityContext context;

    private final Map<Class<? extends TransferVariant<?>>, CombinedStorage<? extends TransferVariant<?>, ? extends Storage<? extends TransferVariant<?>>>> cachedStorage = new HashMap<>();

    private boolean updating = false;

    @Override
    public <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> get(Class<V> variantClass, Class<S> storageClass) {
        if (cachedStorage.isEmpty()) update();
        return (CombinedStorage<V, S>) cachedStorage.get(variantClass);
    }

    private void clear() {
        if (updating) return;
        cachedStorage.clear();
    }

    @Override
    public void update() {
        try {
            updating = true;
            cachedStorage.clear();
            context.getGraph()
                    .getNodes()
                    .forEach(node -> {
                        if (node.getBlockEntity() instanceof FrameBlockEntity frame) {
                            frame.streamStorages().forEach(storage -> cachedStorage..add(storage));
                        }
                    });
            cachedStorage.parts.sort(null);
        } finally {
            updating = false;
        }
    }

    @Override
    public void forceUpdate() {
        update();
    }

    @Override
    public void onSortingChanged() {
        if (cachedStorage != null)
            cachedStorage.parts.sort(null);
    }

    @Override
    public void onPostNodeCreated(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity) {
        clear();
    }

    @Override
    public void onPostNodeDestroyed(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity, Map<LinkPos, LinkEntity> linkEntities) {
        clear();
    }

    @Override
    public void onNodeUnloaded(BlockPos pos) {
        clear();
    }

    @Override
    public void onNodeReloaded(BlockPos pos) {
        clear();
    }

    @Override
    public @NotNull NetworkStorageCache split(@NotNull BlockGraph originalGraph, @NotNull BlockGraph newGraph) {
        clear();
        return new SimpleNetworkStorageCache();
    }

    @Override
    public List<Text> getDebugInfo() {
        var list = new ArrayList<Text>();
        list.add(Text.literal("Simple Storage Cache Debug Info").formatted(Formatting.BOLD, Formatting.YELLOW));
        list.add(Text.literal("  Cached: %s".formatted(cachedStorage != null)));
        return list;
    }

    @Override
    public Text getDebugInfo(BlockPos pos) {
        return Text.literal("-");
    }

    @Override
    public void onInit(@NotNull GraphEntityContext ctx) {
        this.context = ctx;
    }

    @Override
    public @NotNull GraphEntityContext getContext() {
        return context;
    }

    @Override
    public void merge(@NotNull NetworkStorageCache other) {
        clear();
    }
}
