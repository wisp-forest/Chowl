package com.chyzman.chowl.visage.block.impl;

import com.chyzman.chowl.visage.block.TrueVisageBlockEntity;
import com.chyzman.chowl.visage.block.VisageBlockEntity;
import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import com.chyzman.chowl.visage.mixin.BlockItemAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TrueVisageBlock extends Block implements VisageBlockTemplate {

    public static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 16, 16);

    public TrueVisageBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        // bruh
        return null;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof TrueVisageBlockEntity visage && visage.templateModel() != null) {
            return visage.templateModel().getOutlineShape(world, pos, context);
        }
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof TrueVisageBlockEntity visage && visage.templateModel() != null) {
            return visage.templateModel().getCollisionShape(world, pos, context);
        }
        return SHAPE;
    }

    //region visage template
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage) {
            visage.spreadTemplate();
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrueVisageBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    protected ItemActionResult onUseWithItem(
            ItemStack stack,
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (world.getBlockEntity(pos) instanceof TrueVisageBlockEntity visage) {
            if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof VisageBlockTemplate)) {
                var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, stack, hit));

                if (targetState == null) targetState = blockItem.getBlock().getDefaultState();

                var applied = false;

                if (visage.templateModel() == null) {
                    visage.setTemplateModel(targetState);
                    applied = true;
                } else if (visage.templateState() == null) {
                    visage.setTemplateState(targetState);
                    applied = true;
                }

                if (applied) {
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
                var scraped = false;

                if (visage.templateState() != null) {
                    visage.setTemplateState(null);
                    scraped = true;
                } else if (visage.templateModel() != null) {
                    visage.setTemplateModel(null);
                    scraped = true;
                }
                if (scraped) {
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
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        doRandomDisplayTick(state, world, pos, random);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof VisageBlockEntity visage &&
                !(visage.templateState() == null) &&
                !(visage.templateState().getBlock() instanceof VisageBlockTemplate)) {
            return visage.templateState().isTransparent(world, pos);
        }
        return super.isTransparent(state, world, pos);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
    //endregion


}
