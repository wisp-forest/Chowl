package com.chyzman.chowl.core.format.mixin.client;

import com.chyzman.chowl.core.format.number.api.NumberFormatterTypes;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @ModifyArgs(method = "onFinishedLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;collectLoadTimes(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V"))
    private void clearFormattingCaches(Args args) {
        for (NumberFormatterTypes formatter : NumberFormatterTypes.values()) formatter.type.invalidateCache();
    }
}
