package com.chyzman.chowl.registry;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.event.DoubleClickEvent;
import com.chyzman.chowl.util.CompressionManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

public class ServerEventListeners {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CompressionManager.rebuild(server.getOverworld());
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hitResult.isInsideBlock()) return ActionResult.PASS;

            var pos = hitResult.getBlockPos().offset(hitResult.getSide());
            var state = world.getBlockState(pos);

            if (!(state.getBlock() instanceof DrawerFrameBlock) ||
                !(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrameBlockEntity)) {
                return ActionResult.PASS;
            }

            var handStack = player.getStackInHand(hand);

            if (handStack.getItem() instanceof BlockItem blockItem
                && !handStack.isOf(ChowlBlocks.DRAWER_FRAME.asItem())
                && !player.isSneaking()) {
                var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, handStack, hitResult));
                if (drawerFrameBlockEntity.templateState != targetState && targetState != null) {
                    drawerFrameBlockEntity.templateState = targetState;
                    world.setBlockState(pos, state.with(DrawerFrameBlock.LIGHT_LEVEL, targetState.getLuminance()));

                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            } else if (drawerFrameBlockEntity.templateState != null && player.isSneaking()) {
                drawerFrameBlockEntity.templateState = null;
                world.setBlockState(pos, state.with(DrawerFrameBlock.LIGHT_LEVEL, 0));

                drawerFrameBlockEntity.markDirty();
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        DoubleClickEvent.EVENT.register((player, world, state, hitResult) -> {
            if (!Chowl.CHOWL_CONFIG.double_click_templating()) return ActionResult.PASS;
            if (hitResult.isInsideBlock()) return ActionResult.PASS;

            var pos = hitResult.getBlockPos().offset(hitResult.getSide());

            if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity drawerFrameBlockEntity)) {
                return ActionResult.PASS;
            }

            drawerFrameBlockEntity.scheduleSpreadTemplate();

            return ActionResult.SUCCESS;
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