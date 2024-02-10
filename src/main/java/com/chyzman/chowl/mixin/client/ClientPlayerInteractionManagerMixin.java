package com.chyzman.chowl.mixin.client;

import com.chyzman.chowl.block.BreakProgressMaskingBlock;
import com.chyzman.chowl.block.ExtendedSoundGroupBlock;
import com.chyzman.chowl.client.DoubleClickTracker;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique private static Float fakeBreakProgress = null;

    @ModifyExpressionValue(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"))
    private BlockSoundGroup extend(BlockSoundGroup original, @Local BlockPos pos, @Local BlockState state) {
        if (state.getBlock() instanceof ExtendedSoundGroupBlock block) {
            return block.getSoundGroup(client.world, pos, state);
        }

        return original;
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void resetDoubleClick(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted()) {
            DoubleClickTracker.reset();
        }
    }

    @Inject(method = "interactItem", at = @At("RETURN"))
    private void resetDoubleClick(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted()) {
            DoubleClickTracker.reset();
        }
    }

    @WrapOperation(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private float fakeBreakProgressForFrames(BlockState instance, PlayerEntity player, BlockView blockView, BlockPos blockPos, Operation<Float> original) {
        if (instance.getBlock() instanceof BreakProgressMaskingBlock block) {
            if (fakeBreakProgress == null) fakeBreakProgress = 0f;
            fakeBreakProgress += block.calcMaskedBlockBreakingDelta(instance, this.client.player, this.client.player.getWorld(), blockPos);
        }
        return original.call(instance, player, blockView, blockPos);
    }
    
    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F", ordinal = 4))
    private void clearWorkaroundBreakingProgressWhenFinished(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        fakeBreakProgress = null;
    }

    @Inject(method = "cancelBlockBreaking", at = @At(value = "HEAD"))
    private void clearWorkaroundBreakingProgressWhenCanceled(CallbackInfo ci) {
        fakeBreakProgress = null;
    }

    @ModifyArg(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setBlockBreakingInfo(ILnet/minecraft/util/math/BlockPos;I)V"), index = 2)
    private int displayFakeBreakProgress(int breakProgress) {
        if (fakeBreakProgress != null) {
            if (fakeBreakProgress >= 1) {
                fakeBreakProgress = 0f;
            }
            return (int) (fakeBreakProgress * 10);
        }
        return breakProgress;
    }
}
