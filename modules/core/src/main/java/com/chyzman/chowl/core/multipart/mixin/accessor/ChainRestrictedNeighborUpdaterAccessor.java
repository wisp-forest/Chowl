package com.chyzman.chowl.core.multipart.mixin.accessor;

import net.minecraft.world.World;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import net.minecraft.world.block.NeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChainRestrictedNeighborUpdater.class)
public interface ChainRestrictedNeighborUpdaterAccessor {
    @Accessor int getMaxChainDepth();
}
