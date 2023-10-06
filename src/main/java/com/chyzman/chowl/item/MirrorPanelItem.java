package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.graph.GraphStore;
import com.chyzman.chowl.item.component.DrawerCustomizationHolder;
import com.chyzman.chowl.item.component.DrawerFilterHolder;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.CombinedSingleSlotStorage;
import com.chyzman.chowl.transfer.TransferState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MirrorPanelItem extends Item implements PanelItem, DrawerFilterHolder, DrawerCustomizationHolder {

    public static final PanelItem.Button SET_FILTER_BUTTON = new PanelItem.Button(2, 2, 14, 14,
        (world, drawerFrame, side, stack, player, hand) -> {
            var stackInHand = player.getStackInHand(hand);
            if (!stackInHand.isEmpty()) {
                if (!world.isClient) {
                    stack.put(FILTER, ItemVariant.of(stackInHand));
                    drawerFrame.markDirty();
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        },
        (world, drawerFrame, side, stack, player) -> ActionResult.PASS,
        null,
        null);

    public MirrorPanelItem(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        World w = blockEntity.getWorld();

        if (TransferState.TRAVERSING.get()) return null;
        if (w == null) return null;

        ItemVariant filter = stack.get(FILTER);
        if (filter.isBlank()) return null;

        GraphStore state = GraphStore.get(w);
        var graph = state.getGraphFor(blockEntity.getPos());

        if (graph == null) return null;

        try {
            TransferState.TRAVERSING.set(true);

            List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();
            for (var node : graph.nodes()) {
                if (!node.state().isOf(ChowlRegistry.DRAWER_FRAME_BLOCK)) continue;

                var otherBE = w.getBlockEntity(node.pos());
                if (!(otherBE instanceof DrawerFrameBlockEntity otherFrame)) continue;

                otherFrame.collectPanelStorages(storage -> {
                    for (var slot : storage.getSlots()) {
                        if (!slot.getResource().equals(filter)) continue;

                        slots.add(slot);
                    }
                });
            }

            if (slots.isEmpty()) return null;

            return new CombinedSingleSlotStorage<>(slots);
        } finally {
            TransferState.TRAVERSING.set(false);
        }
    }

    @Override
    public List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        if (stack.get(FILTER).isBlank()) {
            return List.of(SET_FILTER_BUTTON);
        } else {
            return List.of(STORAGE_BUTTON);
        }
    }
}