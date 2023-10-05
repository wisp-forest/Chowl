package com.chyzman.chowl.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface DoubleClickableBlock {
    @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);

    record DoubleClickPacket(BlockHitResult hitResult) {}
}