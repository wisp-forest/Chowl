package com.chyzman.chowl.item;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.registry.ChowlRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DrawerFrameItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var state = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
        var blockEntity = new DrawerFrameBlockEntity(BlockPos.ORIGIN, state);
        blockEntity.readNbt(stack.getSubNbt("BlockEntityTag"));
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(ChowlRegistry.DRAWER_FRAME.getDefaultState(), matrices, vertexConsumers, light, overlay);
        DrawerFrameBlockEntityRenderer.renderPanels(blockEntity, MinecraftClient.getInstance(), null, 0, matrices, vertexConsumers, light, overlay);
    }
}