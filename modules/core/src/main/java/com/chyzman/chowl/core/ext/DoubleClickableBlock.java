package com.chyzman.chowl.core.ext;

import com.chyzman.chowl.core.event.DoubleClickEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public interface DoubleClickableBlock {
    @NotNull InteractionResult onDoubleClick(Level world, BlockState state, BlockHitResult hitResult, Player player);

    static InteractionResult doDoubleClick(Level world, BlockState state, BlockHitResult hitResult, Player player) {
        var resEvent = DoubleClickEvent.EVENT.invoker().onDoubleClick(player, world, state, hitResult);
        if (resEvent != InteractionResult.PASS) return resEvent;

        if (!(state.getBlock() instanceof DoubleClickableBlock block)) return InteractionResult.PASS;

        return block.onDoubleClick(world, state, hitResult, player);
    }
}