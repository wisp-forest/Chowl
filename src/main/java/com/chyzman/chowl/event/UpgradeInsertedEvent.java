package com.chyzman.chowl.event;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public interface UpgradeInsertedEvent {
    Event<UpgradeInsertedEvent> EVENT = EventFactory.createArrayBacked(UpgradeInsertedEvent.class, callbacks -> (player, frame, side, panel, upgrade) -> {
        for (var cb : callbacks) {
            cb.onUpgradeInserted(player, frame, side, panel, upgrade);
        }
    });

    void onUpgradeInserted(ServerPlayerEntity player, DrawerFrameBlockEntity frame, Direction side, ItemStack panel, ItemStack upgrade);
}
