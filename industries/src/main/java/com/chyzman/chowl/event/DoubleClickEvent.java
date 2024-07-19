package com.chyzman.chowl.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface DoubleClickEvent {
    Event<DoubleClickEvent> EVENT = EventFactory.createArrayBacked(DoubleClickEvent.class,
        (listeners) -> (player, world, state, hitResult) -> {
            for (DoubleClickEvent event : listeners) {
                ActionResult result = event.onDoubleClick(player, world, state, hitResult);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        }
    );

    ActionResult onDoubleClick(PlayerEntity player, World world, BlockState state, BlockHitResult hitResult);
}
