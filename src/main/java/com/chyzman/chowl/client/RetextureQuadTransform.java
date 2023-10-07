package com.chyzman.chowl.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RetextureQuadTransform implements RenderContext.QuadTransform {
    private static final LoadingCache<BlockState, RetextureQuadTransform> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build(CacheLoader.from(RetextureQuadTransform::new));

    static {
        // TODO: reload cache on resource pack reload
    }

    private final DirectionInfo[] directions = new DirectionInfo[6];
    private Function<Direction, Direction> rotater = dir -> dir;

    private RetextureQuadTransform(BlockState template) {
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
    }

    public static RetextureQuadTransform get(BlockState template) {
        return CACHE.getUnchecked(template).withRotation(dir -> dir);
    }

    public RetextureQuadTransform withRotation(Function<Direction, Direction> rotater) {
        this.rotater = rotater;
        return this;
    }

    @Override
    public boolean transform(MutableQuadView quad) {
        Direction face = rotater.apply(quad.nominalFace());
        if (face == null) return true;
        if (directions[face.getId()] == null) return false;

        quad.spriteBake(directions[face.getId()].sprite, MutableQuadView.BAKE_LOCK_UV);

//        switch (face) {
//            case DOWN -> {
//                int color = Formatting.BLACK.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//            case UP -> {
//                int color = Formatting.WHITE.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//            case NORTH -> {
//                int color = Formatting.RED.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//            case SOUTH -> {
//                int color = Formatting.GREEN.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//            case EAST -> {
//                int color = Formatting.BLUE.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//            case WEST -> {
//                int color = Formatting.YELLOW.getColorValue() | 0xff000000;
//                quad.color(color, color, color, color);
//            }
//        }

        // todo: handle color providers.

        return true;
    }

    private record DirectionInfo(Sprite sprite, boolean hasColor, int colorIdx) {
    }
}
