package com.chyzman.chowl.core.multipart.mixin;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class PacketByteBufMixin {
    @Shadow public abstract FriendlyByteBuf writeInt(int i);
    @Shadow public abstract int readInt();

    @Inject(at = @At("HEAD"), method = "writeBlockHitResult", cancellable = true)
    private void addMultipartData(BlockHitResult hitResult, CallbackInfo ci) {
        if (hitResult instanceof MultipartHitResult result) {
            writeInt(Chowl.MODID.hashCode());
            MultipartHitResult.PACKET_CODEC.encode((FriendlyByteBuf) (Object) this, result);
            ci.cancel();
        } else {
            writeInt(0);
        }
    }

    @Inject(at = @At("HEAD"), method = "readBlockHitResult", cancellable = true)
    private void getMultipartData(CallbackInfoReturnable<BlockHitResult> cir) {
        if (readInt() == Chowl.MODID.hashCode()) {
            cir.setReturnValue(MultipartHitResult.PACKET_CODEC.decode((FriendlyByteBuf) (Object) this));
        }
    }
}
