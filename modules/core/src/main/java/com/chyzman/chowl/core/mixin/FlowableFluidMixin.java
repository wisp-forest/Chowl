package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.block.api.FluidFillHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public class FlowableFluidMixin {
    @Inject(at = @At("TAIL"), method = "canHoldAnyFluid(Lnet/minecraft/world/level/block/state/BlockState;)Z", cancellable = true)
    private static void addPredicates(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        for (FluidFillHandler.CanFill predicate : FluidFillHandler.getPredicates()) {
            if (predicate.canNotFill(state)) {
                cir.setReturnValue(false);
            }
        }
    }
}
