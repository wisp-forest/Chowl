package com.chyzman.chowl.logistics.client;

import com.chyzman.chowl.industries.item.renderer.GenericPanelItemRenderer;
import com.chyzman.chowl.logistics.registry.LogisticsItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import static com.chyzman.chowl.logistics.ChowlLogistics.id;

public class ChowlLogisticsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(LogisticsItems.IMPORT_PANEL, new GenericPanelItemRenderer(id("item/import_panel_base")));

        ModelLoadingPlugin.register(ctx -> {
            ctx.addModels(id("item/import_panel_base"));
        });
    }
}
