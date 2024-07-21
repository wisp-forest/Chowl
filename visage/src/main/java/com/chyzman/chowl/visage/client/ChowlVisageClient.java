package com.chyzman.chowl.visage.client;

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

public class ChowlVisageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_BLOCK.asItem(), new RenameMeLaterItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_STAIRS.asItem(), new RenameMeLaterItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(VISAGE_SLAB.asItem(), new RenameMeLaterItemRenderer());

        BlockRenderLayerMap.INSTANCE.putBlock(VisageBlocks.VISAGE_BLOCK, RenderLayer.getCutout());

        ModelLoadingPlugin.register(ctx -> {
            addTemplated(
                ctx,
                id("block/visage_block"),
                id("block/visage_block_base"),
                Identifier.of("minecraft:block/diamond_block")
            );

            addTemplated(
                ctx,
                id("block/visage_stairs"),
                id("block/visage_stairs_base"),
                Identifier.of("minecraft:block/oak_stairs")
            );

            addTemplated(
                ctx,
                id("block/visage_stairs_inner"),
                id("block/visage_stairs_inner_base"),
                Identifier.of("minecraft:block/oak_stairs_inner")
            );

            addTemplated(
                ctx,
                id("block/visage_stairs_outer"),
                id("block/visage_stairs_outer_base"),
                Identifier.of("minecraft:block/oak_stairs_outer")
            );

            addTemplated(
                ctx,
                id("block/visage_slab"),
                id("block/visage_slab_base"),
                Identifier.of("minecraft:block/oak_slab")
            );

            addTemplated(
                ctx,
                id("block/visage_slab_double"),
                id("block/visage_slab_double_base"),
                Identifier.of("minecraft:block/oak_planks")
            );

            addTemplated(
                ctx,
                id("block/visage_slab_top"),
                id("block/visage_slab_top_base"),
                Identifier.of("minecraft:block/oak_slab_top")
            );
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
