package com.chyzman.chowl.electromechanics.block;

import com.chyzman.chowl.electromechanics.mixin.access.ObserverBlockAccessor;
import com.chyzman.chowl.electromechanics.registry.ElectromechanicsSounds;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class KnockerBlock extends ObserverBlock {
    public static final BooleanProperty PUNCH = BooleanProperty.create("punch");

    public KnockerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(FACING, Direction.SOUTH)
                        .setValue(POWERED, false)
                        .setValue(PUNCH, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUNCH);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
        return state;
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hitResult
    ) {
        if (!player.getAbilities().mayBuild) return InteractionResult.PASS;
        state = state.cycle(PUNCH);
        var pitch = state.getValue(PUNCH) ? 0.55F : 0.5F;
        level.playSound(player, pos, ElectromechanicsSounds.BLOCK_KNOCKER_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
        level.setBlock(pos, state, Block.UPDATE_ALL);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return super.getStateForPlacement(blockPlaceContext)
                .setValue(PUNCH, false);
    }

    public static void initEventListeners() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> onKnock(player, world, hitResult.getBlockPos(), hitResult.getDirection(), false));
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> onKnock(playerEntity, world, blockPos, direction, true));
    }

    private static InteractionResult onKnock(Player player, Level world, BlockPos pos, Direction direction, boolean isAttack) {
        if (!player.isShiftKeyDown()) {
            for (Direction dir : Direction.values()) {
                var neighbor = pos.relative(dir);
                var neighborState = world.getBlockState(neighbor);
                if (!(neighborState.getBlock() instanceof KnockerBlock knockerBlock)) continue;
                if (neighborState.getValue(FACING) != dir.getOpposite()) continue;
                var attack = neighborState.getValue(PUNCH);
                if (attack != isAttack) continue;
                ((ObserverBlockAccessor) knockerBlock).chowlElectromechanics$callStartSignal(world, world, neighbor);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
