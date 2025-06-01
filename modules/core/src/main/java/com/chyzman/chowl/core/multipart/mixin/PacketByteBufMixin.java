package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.multipart.pond.MultipartHitResult;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin {
    @Shadow public abstract short readShort();

    @Shadow public abstract PacketByteBuf writeShort(int i);

    @Inject(at = @At("RETURN"), method = "writeBlockHitResult")
    private void addMultipartData(BlockHitResult hitResult, CallbackInfo ci) {
        writeShort(((MultipartHitResult) hitResult).chowl$getHitMultipart());
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "readBlockHitResult")
    private BlockHitResult getMultipartData(BlockHitResult original) {
        ((MultipartHitResult) original).chowl$setHitMultipart(readShort());
        return original;
    }
}
