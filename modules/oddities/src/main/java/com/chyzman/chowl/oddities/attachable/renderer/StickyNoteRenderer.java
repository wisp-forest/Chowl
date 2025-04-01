package com.chyzman.chowl.oddities.attachable.renderer;

import com.chyzman.chowl.core.attachable.client.AttachableRenderer;
import com.chyzman.chowl.oddities.attachable.StickyNoteAttachable;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import io.wispforest.owo.util.Wisdom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class StickyNoteRenderer implements AttachableRenderer<StickyNoteAttachable> {
    @Override
    public void render(
            StickyNoteAttachable stickyNote,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        matrices.translate(stickyNote.pos());

        matrices.multiply(stickyNote.rotation());
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        matrices.scale(1 / 4f, 1 / 4f, 1 / 4f);

        matrices.translate(0, -0.5, -1 / 16f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-5));
        matrices.translate(0, 0.1, 0);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                OdditiesItems.STICKY_NOTE.getDefaultStack(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0
        );
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        //TODO: make this renderer include the textRenderer from the context instead of getting it here
        var textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.translate(-0.5, 0.5, 0);

        matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);

        matrices.translate(0.5, 1, 0.51);

        matrices.scale(1.5f, 1.5f, 1.5f);

        var fontSpacing = textRenderer.fontHeight + 3f;

        matrices.scale(1 / fontSpacing, -1 / fontSpacing, 1 / fontSpacing);

        matrices.translate(0, fontSpacing * 2, 0);

        var wrapped = textRenderer.wrapLines(Text.literal(Wisdom.ALL_THE_WISDOM.get(new Random(stickyNote.pos().hashCode()).nextInt(Wisdom.ALL_THE_WISDOM.size()))), 120);

        wrapped = wrapped.subList(0, Math.min(7, wrapped.size()));

        var offset = 0;
        for (var orderedText : wrapped) {
            textRenderer.draw(
                    orderedText,
                    0,
                    offset,
                    0,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            offset += fontSpacing;
        }

//        VertexRendering.drawVector(
//                matrices,
//                vertexConsumers.getBuffer(RenderLayer.getLines()),
//                Vec3d.ZERO.toVector3f(),
//                new Vec3d(0, 0, 1),
//                -16776961
//        );

//        new Vec3d(stickyNote.rotation().transform(Vec3d.ZERO.toVector3f())).multiply(2),

        matrices.pop();
    }
}
