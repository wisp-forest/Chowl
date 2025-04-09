package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.attachable.AttachableHolder;
import com.chyzman.chowl.core.attachable.client.AttachableRendererEvents;
import com.chyzman.chowl.core.pond.HitResultDuck;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;

@SuppressWarnings("UnstableApiUsage")
@Mixin(BlockView.class)
public interface BlockViewMixin {
    @Inject(method = "raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;", at = @At(value = "RETURN"), cancellable = true)
    private void makeRaycastsHitAttachables$RaycastContextEdition(RaycastContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        attachablesRaycast(context.getStart(), cir.getReturnValue().getPos(), cir);
    }

    @Inject(method = "raycast(Lnet/minecraft/world/BlockStateRaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;", at = @At(value = "RETURN"), cancellable = true)
    private void makeRaycastsHitAttachables$blockstateRaycastContextEdition(BlockStateRaycastContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        attachablesRaycast(context.getStart(), cir.getReturnValue().getPos(), cir);
    }

    @Unique
    private void attachablesRaycast(Vec3d start, Vec3d end, CallbackInfoReturnable<BlockHitResult> cir) {
        if (!(((BlockView) this) instanceof World world)) return;
        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);
        var candidates = attachableHolder.attachables.values()
                .stream()
                .map(container -> new Pair<>(container.getContained().raycast(start, end), container))
                .filter(pair -> pair.getLeft() != null)
                .toList();
        if (candidates.isEmpty()) return;
        var closest = candidates.stream().min(Comparator.comparing(pair -> pair.getLeft().distanceTo(start))).orElse(null);
        var oldReturn = cir.getReturnValue();
        var newReturn = BlockHitResult.createMissed(
                closest.getLeft(),
                oldReturn.getSide(),
                oldReturn.getBlockPos()
        );
        ((HitResultDuck) newReturn).chowl$setHitAttachable(closest.getRight());
//        AttachableRendererEvents.DEBUG_POSITIONS.add(closest.getLeft());
        cir.setReturnValue(newReturn);
    }
}
