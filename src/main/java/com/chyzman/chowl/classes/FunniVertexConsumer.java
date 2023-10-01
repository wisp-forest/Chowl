package com.chyzman.chowl.classes;

import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;

import static net.minecraft.client.render.item.ItemRenderer.*;

public class FunniVertexConsumer implements VertexConsumer {
    public ArrayList<Vec3d> vertices = new ArrayList<>();

    public VoxelShape processVertices() {
        VoxelShape shape = Block.createCuboidShape(0,0,0,0,0,0);
        for (int i = 0; i < vertices.size(); i += 4) {
            shape = VoxelShapes.union(shape, Block.createCuboidShape(
                     Math.min(vertices.get(i).x, vertices.get(i+2).x),
                     Math.min(vertices.get(i).y, vertices.get(i+2).y),
                     Math.min(vertices.get(i).z, vertices.get(i+2).z),
                     Math.max(vertices.get(i).x, vertices.get(i+2).x),
                     Math.max(vertices.get(i).y, vertices.get(i+2).y),
                     Math.max(vertices.get(i).z, vertices.get(i+2).z)
            ));
        }
        vertices.clear();
        return shape;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        vertices.add(new Vec3d(x, y, z));
        return null;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return null;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return null;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return null;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return null;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return null;
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