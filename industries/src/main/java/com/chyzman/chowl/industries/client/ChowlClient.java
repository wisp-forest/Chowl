package com.chyzman.chowl.industries.client;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.industries.block.DrawerFrameBlockModel;
import com.chyzman.chowl.industries.graph.ClientGraphStore;
import com.chyzman.chowl.industries.item.model.BlankPanelItemModel;
import com.chyzman.chowl.industries.item.renderer.AccessPanelItemRenderer;
import com.chyzman.chowl.industries.item.renderer.DrawerFrameItemRenderer;
import com.chyzman.chowl.industries.item.renderer.GenericPanelItemRenderer;
import com.chyzman.chowl.industries.registry.ChowlBlocks;
import com.chyzman.chowl.industries.registry.ChowlItems;
import com.chyzman.chowl.industries.registry.client.ClientBoundPackets;
import com.chyzman.chowl.industries.registry.client.ClientEventListeners;
import com.chyzman.chowl.industries.screen.PanelConfigScreen;
import com.chyzman.chowl.industries.screen.PanelConfigSreenHandler;
import com.chyzman.chowl.industries.util.BlockSideUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.Function;

import static com.chyzman.chowl.industries.registry.ChowlBlocks.Entities.DRAWER_FRAME;
import static com.chyzman.chowl.industries.Chowl.id;

public class ChowlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();
        ClientBoundPackets.init();
        ClientGraphStore.init();
        DoubleClickTracker.init();
        BlockEntityRendererFactories.register(DRAWER_FRAME, DrawerFrameBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ChowlBlocks.DRAWER_FRAME, RenderLayer.getCutout());
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlBlocks.DRAWER_FRAME.asItem(), new DrawerFrameItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlItems.DRAWER_PANEL, new GenericPanelItemRenderer(id("item/drawer_panel_base")));
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlItems.MIRROR_PANEL, new GenericPanelItemRenderer(id("item/mirror_panel_base")));
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlItems.COMPRESSING_PANEL, new GenericPanelItemRenderer(id("item/compressing_panel_base")));
        BuiltinItemRendererRegistry.INSTANCE.register(ChowlItems.ACCESS_PANEL, new AccessPanelItemRenderer(id("item/access_panel_base")));
        HandledScreens.register(PanelConfigSreenHandler.TYPE, PanelConfigScreen::new);

        SetIngredientComponent.init();

        ModelLoadingPlugin.register(ctx -> {
            ctx.addModels(id("item/drawer_panel_base"), id("item/mirror_panel_base"),
                    id("block/drawer_frame_base"), id("item/compressing_panel_base"),
                    id("item/access_panel_base"), id("block/drawer_frame"),
                    id("item/cog"), id("item/lock"), id("item/remove"));

            ctx.resolveModel().register(context -> {
                if (context.id().equals(id("block/drawer_frame"))) {
                    return DrawerFrameBlockModel.Unbaked.create(id("block/drawer_frame_base"), id("block/panel"));
                } else if (context.id().equals(id("item/phantom_panel"))) {
                    return new BlankPanelItemModel.Unbaked(id("item/phantom_panel_base"));
                } else {
                    return null;
                }
            });
        });
    }

    public static void reloadPos(World world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (world == client.world) {
            client.worldRenderer.scheduleBlockRender(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        }
    }

    public static ItemStack getDrawerFramePickStack(BlockView world, BlockPos pos, BlockState state, Function<BlockState, ItemStack> original) {
        var client = MinecraftClient.getInstance();
        if (world instanceof World worldWorld && worldWorld.isClient()) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DrawerFrameBlockEntity frame) {
                var player = client.player;
                if (player.isSneaking()) {

                } else if (client.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == HitResult.Type.BLOCK) {
                    var side = BlockSideUtils.getSide(blockHitResult);
                    var selected = frame.stacks.get(side.getId()).stack();
                    if (!selected.isEmpty()) {
                        return selected.copy();
                    }
                }
            }
        }
        return original.apply(state);
    }
}