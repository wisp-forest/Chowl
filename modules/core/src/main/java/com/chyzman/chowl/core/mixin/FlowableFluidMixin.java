package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.block.api.FluidFillHandler;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {
    @Inject(at = @At("TAIL"), method = "canFill", cancellable = true)
    private void addPredicates(BlockView world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        for (FluidFillHandler.CanFill predicate : FluidFillHandler.getPredicates()) {
            if (predicate.canNotFill(state)) {
                cir.setReturnValue(false);
            }
        }
    }
}
