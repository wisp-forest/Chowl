package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.FilteringPanelItem;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.item.component.UpgradeablePanelItem;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.CombinedSingleSlotStorage;
import com.chyzman.chowl.transfer.PanelStorageContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MirrorPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, DisplayingPanelItem, UpgradeablePanelItem {
    public static final BlockButton SET_FILTER_BUTTON = PanelItem.buttonBuilder(2, 2, 14, 14)
        .onUse((world, drawerFrame, side, stack, player) -> {
            var stackInHand = player.getStackInHand(Hand.MAIN_HAND);

            if (stackInHand.isEmpty()) return ActionResult.PASS;
            if (world.isClient) return ActionResult.SUCCESS;

            stack.set(ChowlRegistry.ITEM_FILTER, ItemVariant.of(stackInHand));
            drawerFrame.markDirty();

            return ActionResult.SUCCESS;

        })
        .build();

    public MirrorPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SingleSlotStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        ItemVariant filter = ctx.stack().getOrDefault(ChowlRegistry.ITEM_FILTER, ItemVariant.blank());
        if (filter.isBlank()) return null;

        List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();

        ctx.traverseNetwork(storage -> {
            for (var slot : storage.getSlots()) {
                if (!slot.getResource().equals(filter)) continue;

                slots.add(slot);
            }
        });

        return new CombinedSingleSlotStorage<>(slots, filter);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getNetworkStorage(PanelStorageContext ctx) {
        return null;
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        if (stack.getOrDefault(ChowlRegistry.ITEM_FILTER, ItemVariant.blank()).isBlank()) {
            return List.of(SET_FILTER_BUTTON);
        } else {
            return List.of(STORAGE_BUTTON);
        }
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public ItemVariant currentFilter(ItemStack stack) {
        return stack.getOrDefault(ChowlRegistry.ITEM_FILTER, ItemVariant.blank());
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        return true;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.set(ChowlRegistry.ITEM_FILTER, newFilter);
    }
}