package com.chyzman.chowl.visage.mixin;

import com.chyzman.chowl.core.network.ChowlCoreNetworking;
import com.chyzman.chowl.core.network.DoubleClickC2SPacket;
import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import com.chyzman.chowl.visage.item.VisageBlockItem;
import com.chyzman.chowl.visage.network.PickBlockWithVisageC2SPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public static MinecraftClient getInstance() {
        return null;
    }

    @Inject(
            method = "doItemPick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void templateVisageInHand(CallbackInfo ci, boolean bl, HitResult.Type type, BlockPos blockPos, BlockState blockState) {
        var player = getInstance().player;
        if (player.getStackInHand(player.getActiveHand()).getItem() instanceof VisageBlockItem) {
            ChowlCoreNetworking.CHANNEL.clientHandle().send(new PickBlockWithVisageC2SPacket(blockPos));
            ci.cancel();
        }
    }
}
