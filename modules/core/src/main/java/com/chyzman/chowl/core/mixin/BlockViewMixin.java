package com.chyzman.chowl.core.mixin;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.pond.MultipartHitResult;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;

@Mixin(BlockView.class)
public interface BlockViewMixin {
    @ModifyReturnValue(method = "method_17743", at = @At(value = "RETURN"))
    private BlockHitResult makeRaycastsHitMultipart$RaycastContextEdition(BlockHitResult original, RaycastContext context) {
        if (original == null) return null;
        return multipartRaycast(context.shapeContext, context.getStart(), context.getEnd(), original);
    }

    @ModifyReturnValue(method = "method_32881", at = @At(value = "RETURN"))
    private BlockHitResult makeRaycastsHitMultipart$blockstateRaycastContextEdition(BlockHitResult original, BlockStateRaycastContext context) {
        if (original == null) return null;
        return multipartRaycast(ShapeContext.absent(), context.getStart(), context.getEnd(), original);
    }

    @Unique
    private BlockHitResult multipartRaycast(ShapeContext shapeContext, Vec3d start, Vec3d end, BlockHitResult original) {
        if (!(((BlockView) this) instanceof World world)) return original;

        BlockPos hitPos = original.getBlockPos();
        BlockEntity hitEntity = world.getBlockEntity(hitPos);
        if (!(hitEntity instanceof MultipartHolderBlockEntity entity)) return original;

        var candidates = entity.getParts().stream()
          .map(part -> new Pair<>(part.raycast(start, end, shapeContext), part))
          .filter(pair -> pair.getLeft() != null)
          .toList();

        if (candidates.isEmpty()) return original;
        var closest = candidates.stream().min(Comparator.comparing(pair -> pair.getLeft().distanceTo(start))).orElse(null);
        var newReturn = new BlockHitResult(closest.getLeft(), original.getSide(), original.getBlockPos(), true);

        ((MultipartHitResult) newReturn).chowl$setHitMultipart(closest.getRight());
        return newReturn;
    }
}
