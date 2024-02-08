package com.chyzman.chowl.item.renderer;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.block.DrawerFrameBlockEntityRenderer;
import com.chyzman.chowl.client.RenderGlobals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static com.chyzman.chowl.util.ChowlRegistryHelper.id;

@Environment(EnvType.CLIENT)
public class DrawerFrameItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!RenderGlobals.shouldRender()) return;

        try (var ignored = RenderGlobals.enterRender()) {
            var state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            var blockEntity = new DrawerFrameBlockEntity(BlockPos.ORIGIN, state);
            blockEntity.readNbt(stack.getSubNbt("BlockEntityTag"));

            try {
                RenderGlobals.DRAWER_FRAME.set(blockEntity);

                var model = MinecraftClient.getInstance().getBakedModelManager().getModel(id("block/drawer_frame"));
                if (model != null) {
                    matrices.push();
                    matrices.translate(0.5F, 0.5F, 0.5F);
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model);
                    matrices.pop();
                }

                DrawerFrameBlockEntityRenderer.renderPanels(blockEntity, MinecraftClient.getInstance(), null, 0, matrices, vertexConsumers, light, overlay);
            } finally {
                RenderGlobals.DRAWER_FRAME.remove();
            }
        }
    }
}