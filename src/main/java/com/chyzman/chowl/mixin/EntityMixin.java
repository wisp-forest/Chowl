package com.chyzman.chowl.mixin;

import com.chyzman.chowl.block.ExtendedParticleSpriteBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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
}
