package com.chyzman.chowl.core.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.concurrent.TimeUnit;

public class RetextureInfo {
    private static final LoadingCache<test, RetextureInfo> CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(CacheLoader.from(RetextureInfo::new));

    private final BlockState template;
    private final BlockPos pos;
    private final DirectionInfo[] directions = new DirectionInfo[6];
    private final RenderMaterial material;

    public record test(BlockState state, BlockPos pos) {}

    private RetextureInfo(test test) {
        this.template = test.state;
        this.pos = test.pos;

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
                .ambientOcclusion(templateModel.useAmbientOcclusion() && template.getLuminance() == 0 ? TriState.TRUE : TriState.FALSE)
                .find();
    }

    public static RetextureInfo get(BlockState template, BlockPos pos) {
        return CACHE.getUnchecked(new test(template, pos));
    }

    public boolean changeSprite(MutableQuadView quad, Direction face) {
        var info = directions[face.getId()];
        if (info == null) return false;

        quad.spriteBake(info.sprite, MutableQuadView.BAKE_LOCK_UV);

        int scale = 16;

        var uLen = quad.u(2) - quad.u(0);
        var vLen = quad.v(2) - quad.v(0);

        var basePosU = quad.u(0);
        var basePosV = quad.v(0);

        int uIndex;
        int vIndex;

        switch (face) {
            case DOWN -> {
                uIndex = pos.getX() % scale;
                vIndex = pos.getZ() % scale;
            }
            case UP -> {
                uIndex = pos.getZ() % scale;
                vIndex = pos.getX() % scale;
            }
            case WEST, EAST -> {
                uIndex = pos.getZ() % scale;
                vIndex = (scale - 1) - pos.getY() % scale;
            }
            case NORTH, SOUTH -> {
                uIndex = pos.getX() % scale;
                vIndex = (scale - 1) - pos.getY() % scale;
            }
            case null, default -> throw new IllegalStateException("Unexpected value: " + face);
        }

        var uIncrement = (uLen / scale);
        var vIncrement = (vLen / scale);

        var startPosU = basePosU + (uIncrement * uIndex);
        var startPosV = basePosV + (vIncrement * vIndex);

        quad.uv(0, startPosU, startPosV);
        quad.uv(1, startPosU, startPosV + vIncrement);
        quad.uv(2, startPosU + uIncrement, startPosV + vIncrement);
        quad.uv(3, startPosU + uIncrement, startPosV);
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

    private record DirectionInfo(Sprite sprite, boolean hasColor, int colorIdx) {}
}
