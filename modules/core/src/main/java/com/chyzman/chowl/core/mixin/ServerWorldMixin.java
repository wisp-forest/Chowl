package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.attachable.AttachableContainer;
import com.chyzman.chowl.core.attachable.AttachableHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "onBlockChanged", at = @At(value = "HEAD"))
    private void updateAttachables(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        var attachableHolder = ((ServerWorld) (Object) this).getAttachedOrCreate(AttachableHolder.TYPE);

        var toRemove = attachableHolder.chunkPosToAttachables
                .get(new ChunkPos(new BlockPos(pos)))
                .stream()
                .map(attachableHolder.attachables::get)
                .filter(Objects::nonNull)
                .filter(container -> !container.getContained().isSupported(((ServerWorld) (Object) this)))
                .toList();

        for (AttachableContainer container : toRemove) {
            attachableHolder.removeAttachable(container.getUuid());
            var attachable = container.getContained();
            attachable.onBroken(
                    ((ServerWorld) (Object) this),
                    attachable.getClosestPointTo(pos.toCenterPos())
            );
        }

        ((ServerWorld) (Object) this).setAttached(AttachableHolder.TYPE, attachableHolder);
    }
}
