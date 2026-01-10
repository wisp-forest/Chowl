package com.chyzman.chowl.core.multipart.mixin.client;

import com.chyzman.chowl.core.multipart.api.client.render.PartRenderManager;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public class RenderSectionMixin {
    @Shadow @Final BlockPos.MutableBlockPos renderOrigin;

    @Inject(at = @At("HEAD"), method = "createCompileTask")
    private void updateBakedPartRenderer(RenderRegionCache renderRegionCache, CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.CompileTask> cir) {
        PartRenderManager.rebakeRegion(this.renderOrigin);
    }
}
