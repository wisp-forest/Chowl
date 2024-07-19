package com.chyzman.chowl.event;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public final class UpgradeInteractionEvents {
    private UpgradeInteractionEvents() {
    }

    public static final Event<Inserted> UPGRADE_INSERTED = EventFactory.createArrayBacked(Inserted.class, callbacks -> (player, frame, side, panel, upgrade) -> {
        for (Inserted callback : callbacks) {
            callback.onUpgradeInserted(player, frame, side, panel, upgrade);
        }
    });

    public static final Event<Extracted> UPGRADE_EXTRACTED = EventFactory.createArrayBacked(Extracted.class, callbacks -> (player, frame, side, panel, upgrade) -> {
        for (Extracted callback : callbacks) {
            callback.onUpgradeExtracted(player, frame, side, panel, upgrade);
        }
    });

    @FunctionalInterface
    public interface Inserted {
        void onUpgradeInserted(ServerPlayerEntity player, DrawerFrameBlockEntity frame, Direction side, ItemStack panel, ItemStack upgrade);
    }

    @FunctionalInterface
    public interface Extracted {
        void onUpgradeExtracted(ServerPlayerEntity player, DrawerFrameBlockEntity frame, Direction side, ItemStack panel, ItemStack upgrade);
    }
}