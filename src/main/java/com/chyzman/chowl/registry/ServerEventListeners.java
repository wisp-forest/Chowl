package com.chyzman.chowl.registry;

import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

public class ServerEventListeners {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!hitResult.isInsideBlock()) {
                if (world.getBlockState(hitResult.getBlockPos().offset(hitResult.getSide())).getBlock() instanceof DrawerFrameBlock &&
                        world.getBlockEntity(hitResult.getBlockPos().offset(hitResult.getSide())) instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
                    if (player.getStackInHand(hand).getItem() instanceof BlockItem blockItem && !player.isSneaking()) {
                        var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, player.getStackInHand(hand), hitResult));
                        if (drawerFrameBlockEntity.templateState != targetState) {
                            drawerFrameBlockEntity.templateState = targetState;
                        } else {
                            return ActionResult.PASS;
                        }
                    } else if (drawerFrameBlockEntity.templateState != null && player.isSneaking()) {
                        drawerFrameBlockEntity.templateState = null;
                    } else {
                        return ActionResult.PASS;
                    }
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}