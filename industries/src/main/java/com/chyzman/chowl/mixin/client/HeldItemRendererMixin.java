package com.chyzman.chowl.mixin.client;

import com.chyzman.chowl.item.BasePanelItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"))
    private void renderArmsIfItsAPanel(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof BasePanelItem) {
            var arm = hand == Hand.MAIN_HAND ? 1.0F : -1.0F;
            matrices.push();
            matrices.translate(arm * 0.15, -0.2, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * 10.0F));
            renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite());
            matrices.pop();
        }
    }
}