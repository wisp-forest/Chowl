package com.chyzman.chowl.visage.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {
    @Invoker("getPlaceSound")
    SoundEvent chowlVisage$getPlaceSound(BlockState state);
}
