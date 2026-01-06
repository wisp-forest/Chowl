package com.chyzman.chowl.core.attachables.mixin;

import com.chyzman.chowl.core.attachables.impl.AttachableContainer;
import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
    @Inject(method = "updatePOIOnBlockStateChange", at = @At(value = "HEAD"))
    private void updateAttachables(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        var attachableHolder = ((ServerLevel) (Object) this).getAttachedOrCreate(AttachableHolder.TYPE);

        var toRemove = attachableHolder.chunkPosToAttachables
                .get(new ChunkPos(new BlockPos(pos)))
                .stream()
                .map(attachableHolder.attachables::get)
                .filter(Objects::nonNull)
                .filter(container -> !container.getContained().isSupported(((ServerLevel) (Object) this)))
                .toList();

        for (AttachableContainer container : toRemove) {
            attachableHolder.removeAttachable(container.getUuid());
            var attachable = container.getContained();
            attachable.onBroken(
                    ((ServerLevel) (Object) this),
                    attachable.getClosestPointTo(pos.getCenter())
            );
        }

        ((ServerLevel) (Object) this).setAttached(AttachableHolder.TYPE, attachableHolder);
    }
}
