package com.chyzman.chowl.core.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface DoubleClickEvent {
    Event<DoubleClickEvent> EVENT = EventFactory.createArrayBacked(DoubleClickEvent.class,
        (listeners) -> (player, world, state, hitResult) -> {
            for (DoubleClickEvent event : listeners) {
                InteractionResult result = event.onDoubleClick(player, world, state, hitResult);

                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            return InteractionResult.PASS;
        }
    );

    InteractionResult onDoubleClick(Player player, Level world, BlockState state, BlockHitResult hitResult);
}
