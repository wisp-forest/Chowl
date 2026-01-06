package com.chyzman.chowl.core.mixin.client;

import com.chyzman.chowl.core.ext.ExtendedSoundGroupBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: FIX

/*@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyExpressionValue(method = "processWorldEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
    private BlockSoundGroup extend(BlockSoundGroup original, int eventId, BlockPos pos, int data, @Local BlockState state) {
        if (state.getBlock() instanceof ExtendedSoundGroupBlock block) {
            return block.getSoundGroup(client.world, pos, state);
        }

        return original;
    }
}*/
