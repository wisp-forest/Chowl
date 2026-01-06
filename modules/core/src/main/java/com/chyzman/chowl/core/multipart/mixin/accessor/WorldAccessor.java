package com.chyzman.chowl.core.multipart.mixin.accessor;

import net.minecraft.world.World;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {
    @Accessor ChainRestrictedNeighborUpdater getNeighborUpdater();
}
