package com.chyzman.chowl.classes;

import net.minecraft.client.render.VertexConsumer;

public class AABBConstructingVertexConsumer implements VertexConsumer {
    public double minX = Double.MAX_VALUE;
    public double minY = Double.MAX_VALUE;
    public double minZ = Double.MAX_VALUE;
    public double maxX = -Double.MAX_VALUE;
    public double maxY = -Double.MAX_VALUE;
    public double maxZ = -Double.MAX_VALUE;

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
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

    @Override
    public void next() {

    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {

    }

    @Override
    public void unfixColor() {

    }
}