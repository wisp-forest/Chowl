package com.chyzman.chowl.classes;

import net.minecraft.client.render.*;
import net.minecraft.item.Items;

import static net.minecraft.client.render.item.ItemRenderer.getItemGlintConsumer;

public class FunniVertexConsumerProvider implements VertexConsumerProvider {
    public static FunniVertexConsumer consumer = new FunniVertexConsumer();

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return consumer;
    }
}