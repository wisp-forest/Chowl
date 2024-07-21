package com.chyzman.chowl.visage.client;

import com.chyzman.chowl.industries.item.renderer.DrawerFrameItemRenderer;
import com.chyzman.chowl.industries.registry.ChowlBlocks;
import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlockModel;
import com.chyzman.chowl.visage.item.renderer.RenameMeLaterItemRenderer;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import static com.chyzman.chowl.visage.ChowlVisage.id;
import static com.chyzman.chowl.visage.registry.VisageBlocks.RENAME_ME_LATER;

public class ChowlVisageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(RENAME_ME_LATER.asItem(), new RenameMeLaterItemRenderer());

        BlockRenderLayerMap.INSTANCE.putBlock(VisageBlocks.RENAME_ME_LATER, RenderLayer.getCutout());

        ModelLoadingPlugin.register(ctx -> {
            ctx.resolveModel().register(context -> {
                if (context.id().equals(id("block/rename_me_later"))) {
                    return new VisageRenameMeLaterBlockModel.Unbaked(
                        id("block/rename_me_later_base"),
                        Identifier.of("minecraft:block/diamond_block")
                    );
                } else {
                    return null;
                }
            });
        });
    }
}
