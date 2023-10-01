package com.chyzman.chowl.client;

import com.chyzman.chowl.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.graph.ClientGraphStore;
import com.chyzman.chowl.item.DrawerFrameItemRenderer;
import com.chyzman.chowl.item.DrawerPanelItemRenderer;
import com.chyzman.chowl.item.MirrorPanelItemRenderer;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.registry.client.ClientBoundPackets;
import com.chyzman.chowl.registry.client.ClientEventListeners;
import com.chyzman.chowl.screen.PanelConfigScreen;
import com.chyzman.chowl.screen.PanelConfigSreenHandler;
import io.wispforest.owo.Owo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.chyzman.chowl.Chowl.DRAWER_FRAME_BLOCK_ENTITY_TYPE;
import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

public class ChowlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();
        ClientBoundPackets.init();
        ClientGraphStore.init();
        DoubleClickTracker.init();
        BlockEntityRendererFactories.register(DRAWER_FRAME_BLOCK_ENTITY_TYPE, DrawerFrameBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ChowlRegistry.DRAWER_FRAME_BLOCK, RenderLayer.getCutout());
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlRegistry.DRAWER_FRAME_ITEM, new DrawerFrameItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlRegistry.DRAWER_PANEL_ITEM, new DrawerPanelItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlRegistry.MIRROR_PANEL_ITEM, new MirrorPanelItemRenderer());
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(id("item/drawer_panel_base"));
            out.accept(id("item/mirror_panel_base"));
        });
        HandledScreens.register(PanelConfigSreenHandler.TYPE, PanelConfigScreen::new);

        if (Owo.DEBUG) {
            ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
                if (stack.getNbt() != null && stack.getNbt().isEmpty()) {
                    lines.add(Text.literal("Contains non-null, but empty NBT").formatted(Formatting.RED));
                }
            });
        }
    }
}