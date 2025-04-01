package com.chyzman.chowl.oddities.attachable.renderer;

import com.chyzman.chowl.core.attachable.client.AttachableRenderer;
import com.chyzman.chowl.oddities.attachable.PinAttachable;
import com.chyzman.chowl.oddities.attachable.StickyNoteAttachable;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import io.wispforest.owo.util.Wisdom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class PinRenderer implements AttachableRenderer<PinAttachable> {
    @Override
    public void render(
            PinAttachable pin,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        matrices.translate(pin.pos());

        matrices.multiply(pin.rotation());
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        matrices.scale(1 / 6f, 1 / 6f, 1 / 6f);

        matrices.translate(0, 0.4, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                OdditiesItems.PIN.getDefaultStack(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0
        );

        matrices.pop();
    }
}
