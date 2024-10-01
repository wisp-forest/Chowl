package com.chyzman.chowl.core.graph.cache;

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
    @Nullable
    private CombinedStorage<ItemVariant, DrawerStorage> cachedStorage = null;

    public static class DummyStorage {
        private final Map<Class<? extends TransferVariant<?>>, CombinedStorage<? extends TransferVariant<?>, ? extends Storage<? extends TransferVariant<?>>>> map = new HashMap<>();

        @Nullable
        public <T, V extends TransferVariant<T>, S extends Storage<V>> CombinedStorage<V, S> getStorage(Class<V> variantClass, Class<S> storageClass) {
            return (CombinedStorage<V, S>) map.get(variantClass);
        }

        public <T, V extends TransferVariant<T>, S extends Storage<V>> void setStorage(Class<V> variantClass, CombinedStorage<V, S> storage) {
            map.put(variantClass, storage);
        }
    }


    private boolean updating = false;

    @Override
    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        if (cachedStorage == null) update();
        return cachedStorage;
    }

    private void clear() {
        if (updating) {
            return;
        }
        cachedStorage = null;
    }

    @Override
    public void update() {
        try {
            updating = true;
            cachedStorage = new CombinedStorage<>(new ArrayList<>());
            context.getGraph()
                    .getNodes()
                    .forEach(node -> {
                        if (node.getBlockEntity() instanceof StorageDrawerBlockEntity drawer) {
                            drawer.streamStorages().forEach(storage -> cachedStorage.parts.add(storage));
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
