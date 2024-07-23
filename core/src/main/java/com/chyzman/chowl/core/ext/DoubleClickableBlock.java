package com.chyzman.chowl.core.ext;

import com.chyzman.chowl.core.event.DoubleClickEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface DoubleClickableBlock {
    @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);

    static ActionResult doDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        var resEvent = DoubleClickEvent.EVENT.invoker().onDoubleClick(player, world, state, hitResult);
        if (resEvent != ActionResult.PASS) return resEvent;

        if (!(state.getBlock() instanceof DoubleClickableBlock block)) return ActionResult.PASS;

        return block.onDoubleClick(world, state, hitResult, player);
    }
}