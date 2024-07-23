package com.chyzman.chowl.core.mixin.client;

import com.chyzman.chowl.core.network.AttackInteractionC2SPacket;
import com.chyzman.chowl.core.network.ChowlCoreNetworking;
import com.chyzman.chowl.core.ext.AttackInteractionReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    public GameOptions options;

    @Inject(method = "doAttack", at = @At(value = "HEAD"), cancellable = true)
    private void stopBreakingDrawersSmh(CallbackInfoReturnable<Boolean> cir) {
        if (world == null || !(crosshairTarget instanceof BlockHitResult blockHit)) return;

        var pos = blockHit.getBlockPos();
        var state = this.world.getBlockState(pos);
        if (!(state.getBlock() instanceof AttackInteractionReceiver receiver)) return;

        var result = receiver.onAttack(this.world, state, blockHit, player);
        if (!result.isAccepted()) return;

        this.player.swingHand(Hand.MAIN_HAND);
        ChowlCoreNetworking.CHANNEL.clientHandle().send(new AttackInteractionC2SPacket(blockHit));

        options.attackKey.setPressed(false);
        cir.setReturnValue(true);
    }
}