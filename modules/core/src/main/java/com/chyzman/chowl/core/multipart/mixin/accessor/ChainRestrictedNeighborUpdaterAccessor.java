package com.chyzman.chowl.core.multipart.mixin.accessor;

import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CollectingNeighborUpdater.class)
public interface ChainRestrictedNeighborUpdaterAccessor {
    @Accessor int getMaxChainedNeighborUpdates();
}
