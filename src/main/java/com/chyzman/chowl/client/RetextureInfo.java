package com.chyzman.chowl.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RetextureInfo {
    private static final LoadingCache<BlockState, RetextureInfo> CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(CacheLoader.from(RetextureInfo::new));

    private final BlockState template;
    private final DirectionInfo[] directions = new DirectionInfo[6];
    private final RenderMaterial material;

    private RetextureInfo(BlockState template) {
        this.template = template;

        BakedModel templateModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(template);

        for (int dirId = 0; dirId < 6; dirId++) {
            Direction dir = Direction.byId(dirId);

            var quads = templateModel.getQuads(template, dir, Random.create());
            if (quads.isEmpty()) {
                quads = templateModel.getQuads(template, null, Random.create());
            }
            if (quads.isEmpty()) continue;

            var quad = quads.get(0);

            directions[dirId] = new DirectionInfo(quad.getSprite(), quad.hasColor(), quad.getColorIndex());
        }

        this.material = RendererAccess.INSTANCE.getRenderer()
            .materialFinder()
            .blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(template)))
            .find();
    }

    public static RetextureInfo get(BlockState template) {
        return CACHE.getUnchecked(template);
    }

    public boolean changeSprite(MutableQuadView quad, Direction face) {
        var info = directions[face.getId()];
        if (info == null) return false;

        quad.spriteBake(info.sprite, MutableQuadView.BAKE_LOCK_UV);
        quad.material(material);
        return true;
    }

    public void changeColor(MutableQuadView quad, Direction face, BlockRenderView world, BlockPos pos) {
        var info = directions[face.getId()];
        if (info == null) return;

        if (info.hasColor) {
            int color = MinecraftClient.getInstance().getBlockColors().getColor(template, world, pos, info.colorIdx);
            color |= 0xFF000000;

            quad.color(color, color, color, color);
        }
    }

    private record DirectionInfo(Sprite sprite, boolean hasColor, int colorIdx) { }
}
