package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.graph.CrudeGraphState;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.TransferState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
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
        if (TransferState.TRAVERSING.get()) return null;
        if (!(blockEntity.getWorld() instanceof ServerWorld sw)) return null;

        CrudeGraphState state = CrudeGraphState.getFor(sw);
        var graph = state.getGraphFor(blockEntity.getPos());

        if (graph == null) return null;

        try {
            TransferState.TRAVERSING.set(true);

            List<SlottedStorage<ItemVariant>> storages = new ArrayList<>();
            for (var node : graph.nodes.values()) {
                if (!node.state().isOf(ChowlRegistry.DRAWER_FRAME_BLOCK)) continue;

                var otherBE = sw.getBlockEntity(node.pos());
                if (!(otherBE instanceof DrawerFrameBlockEntity otherFrame)) continue;

                otherFrame.collectPanelStorages(storages);
            }

            return new CombinedSlottedStorage<>(storages);
        } finally {
            TransferState.TRAVERSING.set(false);
        }
    }
}
