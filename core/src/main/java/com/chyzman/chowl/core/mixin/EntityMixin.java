package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.ext.ExtendedParticleSpriteBlock;
import com.chyzman.chowl.core.ext.ExtendedSoundGroupBlock;
import com.chyzman.chowl.core.pond.MixinState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow private World world;

    @ModifyArg(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/particle/BlockStateParticleEffect;<init>(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)V"))
    private BlockState owls(BlockState original, @Local(ordinal = 0) BlockPos pos) {
        if (original.getBlock() instanceof ExtendedParticleSpriteBlock block) {
            return block.getParticleState(world, pos, original);
        }

        return original;
    }

    @ModifyExpressionValue(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
    private BlockSoundGroup extend(BlockSoundGroup original, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof ExtendedSoundGroupBlock block) {
            return block.getSoundGroup(world, pos, state);
        }

        return original;
    }

    @ModifyExpressionValue(method = "playSecondaryStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
    private BlockSoundGroup extend(BlockSoundGroup original, BlockState state) {
        BlockPos pos = MixinState.SECONDARY_STEP_POS.get();

        if (state.getBlock() instanceof ExtendedSoundGroupBlock block && pos != null) {
            return block.getSoundGroup(world, pos, state);
        }

        return original;
    }

    @ModifyExpressionValue(method = "playCombinationStepSounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
    private BlockSoundGroup extend(BlockSoundGroup original, BlockState state1, BlockState state2) {
        BlockPos pos = MixinState.STEP_POS.get();

        if (state1.getBlock() instanceof ExtendedSoundGroupBlock block && pos != null) {
            return block.getSoundGroup(world, pos, state1);
        }

        return original;
    }
}
