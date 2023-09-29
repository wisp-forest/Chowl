package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.graph.CrudeGraphState;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.transfer.CombinedSingleSlotStorage;
import com.chyzman.chowl.transfer.TransferState;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class MirrorPanelItem extends Item implements PanelItem {
    public static final NbtKey.Type<ItemVariant> ITEM_VARIANT_TYPE = NbtKey.Type.COMPOUND.then(ItemVariant::fromNbt, TransferVariant::toNbt);
    public static final NbtKey<ItemVariant> FILTER = new NbtKey<>("Filter", ITEM_VARIANT_TYPE);
    public static final PanelItem.Button SET_FILTER_BUTTON = new PanelItem.Button(1 / 8f, 1 / 8f, 7 / 8f, 7 / 8f,
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
        null);

    public static final PanelItem.Button DRAWER_BUTTON = new PanelItem.Button(1 / 8f, 1 / 8f, 7 / 8f, 7 / 8f,
        (world, drawerFrame, side, stack, player, hand) -> {
            var stackInHand = player.getStackInHand(hand);
            if (stackInHand.isEmpty()) return ActionResult.PASS;

            MirrorPanelItem panel = (MirrorPanelItem) stack.getItem();

            if (!world.isClient) {
                var storage = panel.getStorage(stack, drawerFrame, side);

                try (var tx = Transaction.openOuter()) {
                    var from = PlayerInventoryStorage.of(player).getHandSlot(hand);
                    StorageUtil.move(from, storage, variant -> true, stackInHand.getCount(), tx);
                    tx.commit();
                }

                drawerFrame.markDirty();
            }

            return ActionResult.SUCCESS;

        },
        (world, drawerFrame, side, stack, player) -> {
            var stacks = drawerFrame.stacks;
            MirrorPanelItem panel = (MirrorPanelItem) stack.getItem();
            var filter = stack.get(FILTER);

            if (!filter.isBlank()) {
                if (world.isClient) return ActionResult.SUCCESS;

                var storage = panel.getStorage(stack, drawerFrame, side);

                if (storage == null) return ActionResult.FAIL;

                try (var tx = Transaction.openOuter()) {
                    var extracted = storage.extract(filter, player.isSneaking() ? filter.toStack().getMaxCount() : 1, tx);
                    PlayerInventoryStorage.of(player).offerOrDrop(filter, extracted, tx);
                    tx.commit();

                    return ActionResult.SUCCESS;
                }
            }

            player.getInventory().offerOrDrop(stack);
            stacks[side.getId()] = ItemStack.EMPTY;
            drawerFrame.markDirty();
            return ActionResult.SUCCESS;
        },
        null);

    public MirrorPanelItem(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side) {
        if (TransferState.TRAVERSING.get()) return null;
        if (!(blockEntity.getWorld() instanceof ServerWorld sw)) return null;

        ItemVariant filter = stack.get(FILTER);
        if (filter.isBlank()) return null;

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

            List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();
            for (var storage : storages) {
                slots.addAll(storage.getSlots());
            }

            slots.removeIf(x -> !x.getResource().equals(filter));

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
            return List.of(DRAWER_BUTTON);
        }
    }
}
