package com.chyzman.chowl.item.component;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.transfer.TransferState;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public interface PanelItem {
    Button STORAGE_BUTTON = new Button(2, 2, 14, 14,
        (world, frame, side, stack, player, hand) -> {
            var stackInHand = player.getStackInHand(hand);
            if (stackInHand.isEmpty()) return ActionResult.PASS;
            if (!(stack.getItem() instanceof PanelItem)) return ActionResult.PASS;

            PanelItem panel = (PanelItem) stack.getItem();

            if (world.isClient) return ActionResult.SUCCESS;

            var storage = panel.getStorage(stack, frame, side);

            try (var tx = Transaction.openOuter()) {
                StorageUtil.move(
                    PlayerInventoryStorage.of(player).getHandSlot(hand),
                    storage,
                    variant -> true,
                    stackInHand.getCount(),
                    tx
                );

                tx.commit();
            }

            return ActionResult.SUCCESS;
        },
        (world, drawerFrame, side, stack, player) -> {
            PanelItem panel = (PanelItem) stack.getItem();

            if (panel.canExtractFromButton()) {
                var storage = panel.getStorage(stack, drawerFrame, side);

                if (storage == null) return ActionResult.FAIL;
                if (world.isClient) return ActionResult.SUCCESS;

                try (var tx = Transaction.openOuter()) {
                    var resource = StorageUtil.findExtractableResource(storage, tx);

                    if (resource != null) {
                        var extracted = storage.extract(resource, player.isSneaking() ? resource.toStack().getMaxCount() : 1, tx);

                        if (extracted > 0) {
                            PlayerInventoryStorage.of(player).offerOrDrop(resource, extracted, tx);
                            tx.commit();
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }

            player.getInventory().offerOrDrop(stack);
            drawerFrame.stacks.set(side.getId(), new Pair<>(ItemStack.EMPTY, 0));
            drawerFrame.markDirty();
            return ActionResult.SUCCESS;
        },
        (world, frame, side, stack, player) -> {
            try {
                TransferState.NO_BLANK_DRAWERS.set(true);

                var panel = (PanelItem) stack.getItem();
                var storage = panel.getStorage(stack, frame, side);

                if (storage == null) return ActionResult.FAIL;
                if (world.isClient) return ActionResult.SUCCESS;

                try (var tx = Transaction.openOuter()) {
                    StorageUtil.move(PlayerInventoryStorage.of(player), storage, variant -> true, Long.MAX_VALUE, tx);

                    tx.commit();

                    return ActionResult.SUCCESS;
                }
            } finally {
                TransferState.NO_BLANK_DRAWERS.set(false);
            }
        },
        null);

    @SuppressWarnings("UnstableApiUsage")
    @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side);

    default List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return Collections.emptyList();
    }

    default boolean canExtractFromButton() {
        return true;
    }

    record Button(float minX, float minY, float maxX, float maxY, UseFunction use,
                  AttackFunction attack, DoubleClickFunction doubleClick, BlockButtonProvider.RenderConsumer render) {
        public BlockButtonProvider.Button toBlockButton() {
            return new BlockButtonProvider.Button(
                minX, minY, maxX, maxY,
                use != null ? (state, world, pos, player, hand, hit) -> {
                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;
                    var side = DrawerFrameBlock.getSide(hit);

                    return use.onUse(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player, hand);
                } : null,
                attack != null ? (world, state, hit, player) -> {
                    var pos = hit.getBlockPos();
                    var side = DrawerFrameBlock.getSide(hit);

                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;

                    return attack.onAttack(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player);
                } : null,
                doubleClick != null ? (world, state, hit, player) -> {
                    var pos = hit.getBlockPos();
                    var side = DrawerFrameBlock.getSide(hit);

                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;

                    return doubleClick.onDoubleClick(world, drawerFrame, side, drawerFrame.stacks.get(side.getId()).getLeft(), player);
                } : null,
                render
            );
        }
    }

    @FunctionalInterface
    interface UseFunction {
        ActionResult onUse(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player, Hand hand);
    }

    @FunctionalInterface
    interface AttackFunction {
        ActionResult onAttack(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player);
    }

    @FunctionalInterface
    interface DoubleClickFunction {
        ActionResult onDoubleClick(World world, DrawerFrameBlockEntity frame, Direction side, ItemStack stack, PlayerEntity player);
    }
}