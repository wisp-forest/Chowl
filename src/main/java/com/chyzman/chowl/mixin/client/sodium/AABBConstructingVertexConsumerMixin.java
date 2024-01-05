package com.chyzman.chowl.mixin.client.sodium;

import com.chyzman.chowl.asm.OnlyWithMod;
import com.chyzman.chowl.classes.AABBConstructingVertexConsumerProvider;
import net.caffeinemc.mods.sodium.api.vertex.attributes.CommonVertexAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.minecraft.client.render.VertexConsumer;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@OnlyWithMod(modid = "sodium")
@Mixin(AABBConstructingVertexConsumerProvider.VertexConsumerImpl.class)
public abstract class AABBConstructingVertexConsumerMixin implements VertexBufferWriter {
    @Shadow
    public abstract VertexConsumer vertex(double x, double y, double z);

    @Override
    public void push(MemoryStack stack, long ptr, int count, VertexFormatDescription format) {
        long stride = format.stride();
        long offsetPosition = format.getElementOffset(CommonVertexAttribute.POSITION);

        for(int vertexIndex = 0; vertexIndex < count; ++vertexIndex) {
            float x = PositionAttribute.getX(ptr + offsetPosition);
            float y = PositionAttribute.getY(ptr + offsetPosition);
            float z = PositionAttribute.getZ(ptr + offsetPosition);

            vertex(x, y, z);

            ptr += stride;
        }
    }
}
