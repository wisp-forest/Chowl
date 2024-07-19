package com.chyzman.chowl.visage.item.renderer;



import com.chyzman.chowl.visage.client.RenderGlobals;
import com.chyzman.chowl.visage.block.VisageRenameMeLaterBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static com.chyzman.chowl.visage.ChowlVisage.id;

@Environment(EnvType.CLIENT)
public class RenameMeLaterItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!RenderGlobals.shouldRender()) return;

        try (var ignored = RenderGlobals.enterRender()) {
            var state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            var blockEntity = new VisageRenameMeLaterBlockEntity(BlockPos.ORIGIN, state);
            stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).applyToBlockEntity(blockEntity, MinecraftClient.getInstance().player.getRegistryManager());

            try {
                RenderGlobals.VISAGE.set(blockEntity);

                var model = MinecraftClient.getInstance().getBakedModelManager().getModel(id("block/rename_me_later"));
                if (model != null) {
                    matrices.push();
                    matrices.translate(0.5F, 0.5F, 0.5F);
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model);
                    matrices.pop();
                }
            } finally {
                RenderGlobals.VISAGE.remove();
            }
        }
    }
}
