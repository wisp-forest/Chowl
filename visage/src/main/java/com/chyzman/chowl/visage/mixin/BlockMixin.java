package com.chyzman.chowl.visage.mixin;


import com.chyzman.chowl.visage.block.VisageBlockEntity;
import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void makeVisagesCull(
            BlockState state,
            BlockView world,
            BlockPos pos,
            Direction side,
            BlockPos otherPos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (state.getBlock() instanceof VisageBlockTemplate) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VisageBlockEntity visageBlockEntity) {
                var trueState = visageBlockEntity.templateState();
                if (trueState == null) return;
                var otherState = world.getBlockState(otherPos);
                if (otherState.getBlock() instanceof VisageBlockTemplate) {
                    var otherBlockEntity = world.getBlockEntity(otherPos);
                    if (otherBlockEntity instanceof VisageBlockEntity otherVisageBlockEntity &&
                            otherVisageBlockEntity.templateState() != null) otherState = otherVisageBlockEntity.templateState();
                }
                if (trueState.isSideInvisible(otherState, side)) cir.setReturnValue(false);
            }
        }
    }
}
