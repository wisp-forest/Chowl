package com.chyzman.chowl.block;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.classes.FunniVertexConsumerProvider;
import com.chyzman.chowl.item.DrawerPanelItem;
import com.chyzman.chowl.item.PanelItem;
import com.chyzman.chowl.registry.ChowlRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class DrawerFrameBlockEntityRenderer implements BlockEntityRenderer<DrawerFrameBlockEntity> {
    public DrawerFrameBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DrawerFrameBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var client = MinecraftClient.getInstance();
        var world = entity.getWorld();

        renderSelectedButton(client, entity, vertexConsumers, matrices);

        renderPanels(entity, client, world, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    public void renderSelectedButton(MinecraftClient client, DrawerFrameBlockEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices) {
        if (!(client.crosshairTarget instanceof BlockHitResult hitResult)) return;
        if (!hitResult.getBlockPos().equals(entity.getPos())) return;
        if (!(entity.getCachedState().getBlock() instanceof BlockButtonProvider buttonProvider)) return;

        var button = buttonProvider.findButton(entity.getWorld(), entity.getCachedState(), hitResult);

        if (button == null) {
            if (!client.player.isBlockBreakingRestricted(client.world, hitResult.getBlockPos(), client.interactionManager.getCurrentGameMode())) {
                WorldRenderer.drawCuboidShapeOutline(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), DrawerFrameBlock.BASE, 0, 0, 0, 0, 0, 0, 0.4f);
            }
            return;
        }

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(hitResult.getSide().getRotationQuaternion());
        matrices.translate(0, 0.5, 0);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        matrices.translate(0.5, -0.5, 0);
        matrices.translate(-button.maxX(), button.maxY(), 0);
        matrices.scale(button.maxX() - button.minX(), button.maxY() - button.minY(), 1);
        if (!client.player.isBlockBreakingRestricted(client.world, hitResult.getBlockPos(), client.interactionManager.getCurrentGameMode())) {
            var shape = Block.createCuboidShape(0, 0, 0, 16, 16, 1);
            WorldRenderer.drawShapeOutline(matrices, vertexConsumers.getBuffer(RenderLayer.LINES), shape, 0, -1, 0, 0, 0, 0, 0.4f, false);
        }
        matrices.translate(0.5, -0.5, 0);

//        matrices.scale(-1, -1, -1);

//        var drawCtx = OwoUIDrawContext.of(new DrawContext(client, client.getBufferBuilders().getEntityVertexConsumers()));
//        ((DrawContextAccessor) drawCtx).setMatrices(matrices);
//        drawCtx.drawPanel(0, 0, 1, 1, false);

        if (button.render() != null) {
            button.render().consume(client, entity, hitResult, vertexConsumers, matrices);
        }

        matrices.pop();
    }

    public static void renderPanels(DrawerFrameBlockEntity entity, MinecraftClient client, World world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        for (int i = 0; i < entity.stacks.length; i++) {
            var stack = entity.stacks[i];
            if (!stack.isEmpty()) {
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                matrices.multiply(Direction.byId(i).getRotationQuaternion());
                matrices.translate(0, 0.5 - 1 / 32f, 0);
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
                if (!(stack.getItem() instanceof PanelItem)) {
                    matrices.translate(0, 0,-1 / 32f);
                    matrices.scale(3 / 4f, 3 / 4f, 3/4f);
                    matrices.translate(0, 0,1 / 32f);
                }
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, overlay, client.getItemRenderer().getModels().getModel(stack));
                matrices.pop();
            }
        }
    }
}