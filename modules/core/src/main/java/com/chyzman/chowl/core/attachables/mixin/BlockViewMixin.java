package com.chyzman.chowl.core.attachables.mixin;

import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("UnstableApiUsage")
@Mixin(BlockGetter.class)
public interface BlockViewMixin {
    @Inject(method = "clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(value = "RETURN"), cancellable = true)
    private void makeRaycastsHitAttachables$RaycastContextEdition(ClipContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        attachablesRaycast(context.getFrom(), cir.getReturnValue().getLocation(), cir);
    }

    @Inject(method = "isBlockInLine(Lnet/minecraft/world/level/ClipBlockStateContext;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(value = "RETURN"), cancellable = true)
    private void makeRaycastsHitAttachables$blockstateRaycastContextEdition(ClipBlockStateContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        attachablesRaycast(context.getFrom(), cir.getReturnValue().getLocation(), cir);
    }

    @Unique
    private void attachablesRaycast(Vec3 start, Vec3 end, CallbackInfoReturnable<BlockHitResult> cir) {
        if (!(((BlockGetter) this) instanceof Level world)) return;
        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);
        var candidates = attachableHolder.attachables.values()
                .stream()
                .map(container -> new Tuple<>(container.getContained().raycast(start, end), container))
                .filter(pair -> pair.getA() != null)
                .toList();
        if (candidates.isEmpty()) return;
        var closest = candidates.stream().min(Comparator.comparing(pair -> pair.getA().distanceTo(start))).orElse(null);
        var oldReturn = cir.getReturnValue();
        var newReturn = BlockHitResult.miss(
                closest.getA(),
                oldReturn.getDirection(),
                oldReturn.getBlockPos()
        );
        ((HitResultDuck) newReturn).chowl$setHitAttachable(closest.getB());
//        AttachableRendererEvents.DEBUG_POSITIONS.add(closest.getLeft());
        cir.setReturnValue(newReturn);
    }
}
