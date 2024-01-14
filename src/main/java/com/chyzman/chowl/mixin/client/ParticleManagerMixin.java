package com.chyzman.chowl.mixin.client;

import com.chyzman.chowl.block.ExtendedParticleSpriteBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Shadow protected ClientWorld world;

    @ModifyArgs(method = {"addBlockBreakingParticles", "method_34020"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BlockDustParticle;<init>(Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V"))
    private void owls(Args args) {
        if (args.<BlockState>get(7).getBlock() instanceof ExtendedParticleSpriteBlock block) {
            args.set(7, block.getParticleState(this.world, args.get(8), args.get(7)));
        }
    }
}
