package com.chyzman.chowl.mixin.lavender;

import com.chyzman.chowl.pond.lavender.EntryAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.wispforest.lavender.book.Entry;
import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;

@Mixin(LavenderBookScreen.PageSupplier.class)
public class PageSupplierMixin {
    @ModifyExpressionValue(method = "lambda$buildEntryIndex$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private MutableText replaceDaText(MutableText original, String[] arg1, ArrayList<?> arg2, int[] arg3, Entry entry) {
        EntryAccess acc = (EntryAccess)(Object) entry;

        if (acc.chowl$isFake() && acc.chowl$realText() != null) {
            return acc.chowl$realText().copy();
        }

        return original;
    }

    @ModifyArgs(method = "lambda$buildEntryIndex$5", at = @At(value = "INVOKE", target = "Lio/wispforest/lavender/client/LavenderBookScreen;navPush(Lio/wispforest/lavender/client/LavenderBookScreen$PageSupplier;)V"))
    private void replaceDaText(Args args, Entry entry, double arg1, double arg2, int arg3) {
        EntryAccess acc = (EntryAccess)(Object) entry;

        if (acc.chowl$isFake() && acc.chowl$realPage() != null) {
            args.set(0, acc.chowl$realPage().get());
        }
    }
}
