package com.chyzman.chowl.classes;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.HashMap;
import java.util.Map;

public class AABBConstructingVertexConsumerProvider implements VertexConsumerProvider {
    public float minX = Float.MAX_VALUE;
    public float minY = Float.MAX_VALUE;
    public float minZ = Float.MAX_VALUE;
    public float maxX = -Float.MAX_VALUE;
    public float maxY = -Float.MAX_VALUE;
    public float maxZ = -Float.MAX_VALUE;

    private final Map<RenderLayer, VertexConsumerImpl> consumers = new HashMap<>();

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return consumers.computeIfAbsent(layer, unused -> new VertexConsumerImpl());
    }

    public class VertexConsumerImpl implements VertexConsumer {
        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);

            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);

            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }
    }
}