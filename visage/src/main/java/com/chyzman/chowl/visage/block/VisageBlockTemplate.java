package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.core.ext.BreakProgressMaskingBlock;
import com.chyzman.chowl.core.ext.ExtendedParticleSpriteBlock;
import com.chyzman.chowl.core.ext.ExtendedSoundGroupBlock;
import com.chyzman.chowl.core.registry.ChowlCoreComponents;
import com.chyzman.chowl.visage.mixin.BlockItemAccessor;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public interface VisageBlockTemplate extends /* Block, */ BlockEntityProvider, ExtendedParticleSpriteBlock, ExtendedSoundGroupBlock, BreakProgressMaskingBlock {
    IntProperty LIGHT_LEVEL = Properties.LEVEL_15;
    ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LIGHT_LEVEL);

    @Nullable
    @Override
    default BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VisageBlockEntity(pos, state);
    }

    default ItemActionResult doOnUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage) {
            if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof VisageBlockTemplate)) {
                var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, stack, hit));

                if (targetState == null) targetState = blockItem.getBlock().getDefaultState();

                if (visage.templateState() == null) {
                    visage.setTemplateState(targetState);

                    BlockSoundGroup blockSoundGroup = targetState.getSoundGroup();
                    world.playSound(
                            player,
                            pos,
                            ((BlockItemAccessor) blockItem).chowlVisage$getPlaceSound(targetState),
                            SoundCategory.BLOCKS,
                            (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
                            blockSoundGroup.getPitch() * 0.8F
                    );

                    return ItemActionResult.SUCCESS;
                }
            } else if (stack.getItem() instanceof AxeItem) {
                if (visage.templateState() != null) {
                    visage.setTemplateState(null);

                    if (player instanceof ServerPlayerEntity serverPlayerEntity) Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, pos, stack);

                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    stack.damage(1, player, LivingEntity.getSlotForHand(hand));

                    return ItemActionResult.SUCCESS;

                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    default BlockState getParticleState(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof VisageBlockEntity visage)) return state;
        if (visage.templateState() == null) return state;

        return visage.templateState();
    }

    @Override
    default BlockSoundGroup getSoundGroup(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof VisageBlockEntity visage)) return state.getSoundGroup();
        if (visage.templateState() == null) return state.getSoundGroup();

        return visage.templateState().getSoundGroup();
    }

    @Override
    default BlockSoundGroup getSoundGroup(World world, BlockPos pos, BlockState state, ItemStack stack) {
        BlockState templateState = stack.get(ChowlCoreComponents.TEMPLATE_STATE);

        if (templateState == null) return state.getSoundGroup();

        return templateState.getSoundGroup();
    }

    default void doRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage
            && !(visage.templateState() == null)
            && visage.templateState() != state
            && !(visage.templateState().getBlock() instanceof VisageBlockTemplate)) {
            visage.templateState().getBlock().randomDisplayTick(visage.templateState(), world, pos, random);
        }
    }

    @Override
    default float calcMaskedBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage &&
            visage.templateState() != null &&
            !(visage.templateState().getBlock() instanceof VisageBlockTemplate)) {
            return visage.templateState().calcBlockBreakingDelta(player, world, pos);
        }

        return state.calcBlockBreakingDelta(player, world, pos);
    }
}