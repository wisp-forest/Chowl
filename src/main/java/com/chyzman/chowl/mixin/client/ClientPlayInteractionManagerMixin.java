package com.chyzman.chowl.mixin.client;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.client.ChowlClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @WrapOperation(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private float fakeBreakProgressForFrames(BlockState instance, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos, Operation<Float> original) {
        if (client.world.getBlockEntity(blockPos) instanceof DrawerFrameBlockEntity frame && !(frame.templateState == null)) {
            client.player.sendMessage(Text.literal("Breaking progress: " + ChowlClient.breakingProgressWorkaround), true);
            ChowlClient.breakingProgressWorkaround += original.call(instance, playerEntity, blockView, blockPos);
            client.player.sendMessage(Text.literal("Breaking progress: " + ChowlClient.breakingProgressWorkaround), true);
            return frame.templateState.calcBlockBreakingDelta(this.client.player, this.client.player.getWorld(), blockPos);
        }
        return original.call(instance, playerEntity, blockView, blockPos);
    }

    @ModifyExpressionValue(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F", ordinal = 2))
    private float fakeFinishBreaking(float original) {
        if (ChowlClient.breakingProgressWorkaround >= 1) {
            return ChowlClient.breakingProgressWorkaround;
        }
        return original;
    }
    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F", ordinal = 3))
    private void clearWorkaroundBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        ChowlClient.breakingProgressWorkaround = 0;
    }
}