package com.chyzman.chowl.oddities.mixin.access;

import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor("skyRendering")
    SkyRendering chowlOddities$getSkyRendering();
}
