package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.pond.MixinState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;playSecondaryStepSound(Lnet/minecraft/block/BlockState;)V"))
    private void set(BlockPos pos, BlockState state, CallbackInfo ci) {
        MixinState.SECONDARY_STEP_POS.set(pos);
    }

    @Inject(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;playCombinationStepSounds(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V"))
    private void set2(BlockPos pos, BlockState state, CallbackInfo ci, @Local(ordinal = 1) BlockPos pos2) {
        MixinState.STEP_POS.set(pos);
    }
}
