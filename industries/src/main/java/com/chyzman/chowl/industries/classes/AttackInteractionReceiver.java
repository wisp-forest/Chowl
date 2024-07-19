package com.chyzman.chowl.industries.classes;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface AttackInteractionReceiver {
    @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player);

    record InteractionPacket(BlockHitResult hitResult) {}
}