package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.graph.GraphStore;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.TransferState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AccessPanelItem extends Item implements PanelItem {
    public AccessPanelItem(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        World w = blockEntity.getWorld();

        if (TransferState.TRAVERSING.get()) return null;
        if (w == null) return null;

        GraphStore store = GraphStore.get(w);
        var graph = store.getGraphFor(blockEntity.getPos());

        if (graph == null) return null;

        try {
            TransferState.TRAVERSING.set(true);

            List<SlottedStorage<ItemVariant>> storages = new ArrayList<>();
            for (var node : graph.nodes()) {
                if (!node.state().isOf(ChowlRegistry.DRAWER_FRAME_BLOCK)) continue;

                var otherBE = w.getBlockEntity(node.pos());
                if (!(otherBE instanceof DrawerFrameBlockEntity otherFrame)) continue;

                otherFrame.collectPanelStorages(storages);
            }

            return new CombinedSlottedStorage<>(storages);
        } finally {
            TransferState.TRAVERSING.set(false);
        }
    }
}
