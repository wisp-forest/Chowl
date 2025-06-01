package com.chyzman.chowl.core.multipart.mixin.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldAccessor {
    @Accessor NeighborUpdater getNeighborUpdater();
    @Invoker EntityLookup<Entity> callGetEntityLookup();
}
