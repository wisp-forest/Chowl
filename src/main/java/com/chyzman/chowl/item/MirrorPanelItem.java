package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.item.component.DisplayingPanelItem;
import com.chyzman.chowl.item.component.FilteringPanelItem;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.transfer.BigStorageView;
import com.chyzman.chowl.transfer.CombinedSingleSlotStorage;
import com.chyzman.chowl.transfer.PanelStorageContext;
import com.chyzman.chowl.util.NbtKeyTypes;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MirrorPanelItem extends BasePanelItem implements PanelItem, FilteringPanelItem, DisplayingPanelItem {
    public static final NbtKey<ItemVariant> FILTER = new NbtKey<>("Filter", NbtKeyTypes.ITEM_VARIANT);

    public static final BlockButton SET_FILTER_BUTTON = PanelItem.buttonBuilder(2, 2, 14, 14)
        .onUse((world, drawerFrame, side, stack, player, hand) -> {
            var stackInHand = player.getStackInHand(hand);
            if (!stackInHand.isEmpty()) {
                if (!world.isClient) {
                    stack.put(FILTER, ItemVariant.of(stackInHand));
                    drawerFrame.markDirty();
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        })
        .build();

    public MirrorPanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable SingleSlotStorage<ItemVariant> getStorage(PanelStorageContext ctx) {
        ItemVariant filter = ctx.stack().get(FILTER);
        if (filter.isBlank()) return null;

        List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();
        if (!ctx.traverseNetwork(storage -> {
            for (var slot : storage.getSlots()) {
                if (!slot.getResource().equals(filter)) continue;

                slots.add(slot);
            }
        }))
            return null;

        if (slots.isEmpty()) return null;

        return new CombinedSingleSlotStorage<>(slots);
    }

    @Override
    public @Nullable SlottedStorage<ItemVariant> getNetworkStorage(PanelStorageContext ctx) {
        return null;
    }

    @Override
    public List<BlockButton> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        if (stack.get(FILTER).isBlank()) {
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
        return stack.get(FILTER);
    }

    @Override
    public boolean canSetFilter(ItemStack stack, ItemVariant to) {
        return true;
    }

    @Override
    public void setFilter(ItemStack stack, ItemVariant newFilter) {
        stack.put(FILTER, newFilter);
    }

    @Override
    public ItemVariant displayedVariant(ItemStack stack) {
        return currentFilter(stack);
    }

    @Override
    public BigInteger displayedCount(ItemStack stack, @Nullable DrawerFrameBlockEntity drawerFrame, @Nullable Direction side) {
        if (drawerFrame == null || side == null) return BigInteger.ZERO;

        var storage = this.getStorage(PanelStorageContext.forRendering(stack));

        if (!(storage instanceof BigStorageView<?> big)) return BigInteger.ZERO;

        return big.bigAmount();
    }
}