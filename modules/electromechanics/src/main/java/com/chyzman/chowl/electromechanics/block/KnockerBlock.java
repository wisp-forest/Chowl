package com.chyzman.chowl.electromechanics.block;

import com.chyzman.chowl.electromechanics.mixin.access.ObserverBlockAccessor;
import com.chyzman.chowl.electromechanics.registry.ElectromechanicsSounds;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class KnockerBlock extends ObserverBlock {
    public static final BooleanProperty PUNCH = BooleanProperty.of("punch");

    public KnockerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACING, Direction.SOUTH)
                        .with(POWERED, false)
                        .with(PUNCH, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PUNCH);
    }


    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return state;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;
        state = state.cycle(PUNCH);
        var pitch = state.get(PUNCH) ? 0.55F : 0.5F;
        world.playSound(player, pos, ElectromechanicsSounds.BLOCK_KNOCKER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
                .with(PUNCH, false);
    }

    public static void initEventListeners() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onKnock(player, world, hitResult.getBlockPos(), hitResult.getSide(), false));
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> onKnock(playerEntity, world, blockPos, direction, true));
    }

    private static ActionResult onKnock(PlayerEntity player, World world, BlockPos pos, Direction direction, boolean isAttack) {
        if (!player.isSneaking()) {
            for (Direction dir : Direction.values()) {
                var neighbor = pos.offset(dir);
                var neighborState = world.getBlockState(neighbor);
                if (!(neighborState.getBlock() instanceof KnockerBlock knockerBlock)) continue;
                if (neighborState.get(FACING) != dir.getOpposite()) continue;
                var attack = neighborState.get(PUNCH);
                if (attack != isAttack) continue;
                ((ObserverBlockAccessor) knockerBlock).chowlElectromechanics$callScheduleTick(world, world, neighbor);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
