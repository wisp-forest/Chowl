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
                var pos = hitResult.getBlockPos().offset(hitResult.getSide());
                var state = world.getBlockState(pos);
                if (state.getBlock() instanceof DrawerFrameBlock &&
                        world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
                    if (player.getStackInHand(hand).getItem() instanceof BlockItem blockItem && !player.isSneaking()) {
                        var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, player.getStackInHand(hand), hitResult));
                        if (drawerFrameBlockEntity.templateState != targetState && targetState != null) {
                            drawerFrameBlockEntity.templateState = targetState;
                            world.setBlockState(pos, state.with(DrawerFrameBlock.LIGHT_LEVEL, targetState.getLuminance()));
                        } else {
                            return ActionResult.PASS;
                        }
                    } else if (drawerFrameBlockEntity.templateState != null && player.isSneaking()) {
                        drawerFrameBlockEntity.templateState = null;
                        world.setBlockState(pos, state.with(DrawerFrameBlock.LIGHT_LEVEL, 0));
                    } else {
                        return ActionResult.PASS;
                    }
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
//        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//            if (hitResult != null &&
//                    (hitResult.getPos().distanceTo(entity.getEyePos()) < entity.getWidth()) &&
//                    (Math.abs(hitResult.getPos().getY() - entity.getEyePos().getY()) < (entity.getHeight() - entity.getEyeHeight(entity.getPose()))) &&
//                    (Math.abs(entity.getHeadYaw() - (180 + player.getHeadYaw())) < 70)) {
//                player.sendMessage(Text.literal("you just punched a " + entity.getName().getString() + " in the fucking face"), true);
//                return ActionResult.SUCCESS;
//            }
//            player.sendMessage(Text.empty(), true);
//            return ActionResult.PASS;
//        });
    }
}