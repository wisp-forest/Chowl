package com.chyzman.chowl.item;

import com.chyzman.chowl.block.BlockButtonProvider;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface PanelItem {
    @SuppressWarnings("UnstableApiUsage")
    @Nullable SlottedStorage<ItemVariant> getStorage(ItemStack stack, DrawerFrameBlockEntity blockEntity, Direction side);

    default List<Button> listButtons(DrawerFrameBlockEntity drawerFrame, Direction side, ItemStack stack) {
        return Collections.emptyList();
    }

    record Button(float minX, float minY, float maxX, float maxY, UseFunction use,
                  AttackFunction attack, DoubleClickFunction doubleClick, BlockButtonProvider.RenderConsumer render) {
        public BlockButtonProvider.Button toBlockButton() {
            return new BlockButtonProvider.Button(
                minX, minY, maxX, maxY,
                (state, world, pos, player, hand, hit) -> {
                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;

                    return use.onUse(world, drawerFrame, hit.getSide(), drawerFrame.stacks[hit.getSide().getId()], player, hand);
                },
                (world, state, hit, player) -> {
                    var pos = hit.getBlockPos();

                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;

                    return attack.onAttack(world, drawerFrame, hit.getSide(), drawerFrame.stacks[hit.getSide().getId()], player);
                },
                (world, state, hit, player) -> {
                    var pos = hit.getBlockPos();

                    if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrame)) return ActionResult.PASS;

                    return doubleClick.onDoubleClick(world, drawerFrame, hit.getSide(), drawerFrame.stacks[hit.getSide().getId()], player);
                },
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
