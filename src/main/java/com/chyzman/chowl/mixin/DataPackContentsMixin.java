package com.chyzman.chowl.mixin;

import com.chyzman.chowl.util.CompressionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {
    @Shadow @Final private RecipeManager recipeManager;

    @Inject(method = "refresh", at = @At("RETURN"))
    private void compressionTm(DynamicRegistryManager dynamicRegistryManager, CallbackInfo ci) {
        CompressionManager.rebuild(this.recipeManager);
    }
}
