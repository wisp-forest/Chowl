package com.chyzman.chowl.visage.client;

import com.chyzman.chowl.industries.item.renderer.DrawerFrameItemRenderer;
import com.chyzman.chowl.industries.registry.ChowlBlocks;
import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlockModel;
import com.chyzman.chowl.visage.item.renderer.RenameMeLaterItemRenderer;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import static com.chyzman.chowl.visage.ChowlVisage.id;
import static com.chyzman.chowl.visage.registry.VisageBlocks.RENAME_ME_LATER;

public class ChowlVisageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(RENAME_ME_LATER.asItem(), new RenameMeLaterItemRenderer());


        ModelLoadingPlugin.register(ctx -> {
            ctx.resolveModel().register(context -> {
                if (context.id().equals(id("block/rename_me_later"))) {
                    return VisageRenameMeLaterBlockModel.Unbaked.create(id("block/rename_me_later_base"));
                } else {
                    return null;
                }
            });
        });
    }
}
