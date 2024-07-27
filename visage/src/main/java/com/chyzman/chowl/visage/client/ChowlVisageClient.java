package com.chyzman.chowl.visage.client;

import com.chyzman.chowl.visage.block.TrueVisageBlockModel;
import com.chyzman.chowl.visage.block.VisageBlockModel;
import com.chyzman.chowl.visage.item.renderer.RenameMeLaterItemRenderer;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import static com.chyzman.chowl.visage.ChowlVisage.id;
import static com.chyzman.chowl.visage.registry.VisageBlocks.*;
import static com.chyzman.chowl.visage.registry.VisageBlocks.Entities.TRUE_VISAGE_BLOCK;

public class ChowlVisageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_BLOCK.asItem(), new RenameMeLaterItemRenderer(id("block/visage_block")));
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_STAIRS.asItem(), new RenameMeLaterItemRenderer(id("block/visage_stairs")));
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_SLAB.asItem(), new RenameMeLaterItemRenderer(id("block/visage_slab")));
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_FENCE.asItem(), new RenameMeLaterItemRenderer(id("block/visage_fence_inventory")));
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_WALL.asItem(), new RenameMeLaterItemRenderer(id("block/visage_wall_inventory")));

        BuiltinItemRendererRegistry.INSTANCE.register(TRUE_VISAGE.asItem(), new RenameMeLaterItemRenderer(id("block/true_visage")));


        BlockRenderLayerMap.INSTANCE.putBlock(VisageBlocks.VISAGE_BLOCK, RenderLayer.getCutout());

        ModelLoadingPlugin.register(ctx -> {
            ctx.addModels(
                    id("block/visage_block"),
                    id("block/visage_stairs"),
                    id("block/visage_slab"),
                    id("block/visage_fence_inventory"),
                    id("block/visage_wall_inventory"),
                    id("block/true_visage")
            );

            addTemplated(
                    ctx,
                    id("block/visage_block"),
                    id("block/base/block"),
                    Identifier.of("minecraft:block/diamond_block")
            );

            addTemplated(
                    ctx,
                    id("block/visage_stairs"),
                    id("block/base/stairs"),
                    id("block/template/stairs")
            );

            addTemplated(
                    ctx,
                    id("block/visage_stairs_inner"),
                    id("block/base/stairs_inner"),
                    id("block/template/stairs_inner")
            );

            addTemplated(
                    ctx,
                    id("block/visage_stairs_outer"),
                    id("block/base/stairs_outer"),
                    id("block/template/stairs_outer")
            );

            addTemplated(
                    ctx,
                    id("block/visage_slab"),
                    id("block/base/slab"),
                    Identifier.of("minecraft:block/oak_slab")
            );

            addTemplated(
                    ctx,
                    id("block/visage_slab_double"),
                    id("block/base/slab_double"),
                    Identifier.of("minecraft:block/oak_planks")
            );

            addTemplated(
                    ctx,
                    id("block/visage_slab_top"),
                    id("block/base/slab_top"),
                    Identifier.of("minecraft:block/oak_slab_top")
            );

            addTemplated(
                    ctx,
                    id("block/visage_fence_post"),
                    id("block/base/fence_post"),
                    id("block/template/fence_post")
            );

            addTemplated(
                    ctx,
                    id("block/visage_fence_side"),
                    id("block/base/fence_side"),
                    id("block/template/fence_side")
            );

            addTemplated(
                    ctx,
                    id("block/visage_fence_inventory"),
                    id("block/base/fence_inventory"),
                    id("block/template/fence_inventory")
            );

            addTemplated(
                    ctx,
                    id("block/visage_wall_post"),
                    id("block/base/wall_post"),
                    Identifier.of("minecraft:block/cobblestone_wall_post")
            );

            addTemplated(
                    ctx,
                    id("block/visage_wall_side"),
                    id("block/base/wall_side"),
                    Identifier.of("minecraft:block/cobblestone_wall_side")
            );

            addTemplated(
                    ctx,
                    id("block/visage_wall_side_tall"),
                    id("block/base/wall_side_tall"),
                    Identifier.of("minecraft:block/cobblestone_wall_side_tall")
            );

            addTemplated(
                    ctx,
                    id("block/visage_wall_inventory"),
                    id("block/base/wall_inventory"),
                    Identifier.of("minecraft:block/cobblestone_wall_inventory")
            );

            ctx.resolveModel().register(context -> {
                if (context.id().equals(id("block/true_visage"))) {
                    return new TrueVisageBlockModel.Unbaked(id("block/base/block"));
                } else {
                    return null;
                }
            });
        });
    }

    private static void addTemplated(ModelLoadingPlugin.Context ctx, Identifier id, Identifier baseId, Identifier templatedId) {
        ctx.resolveModel().register(context -> {
            if (context.id().equals(id)) {
                return new VisageBlockModel.Unbaked(baseId, templatedId);
            } else {
                return null;
            }
        });
    }
}
