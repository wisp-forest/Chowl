package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.industries.block.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class VisageRenameMeLaterBlock extends BlockWithEntity implements DoubleClickableBlock, ExtendedParticleSpriteBlock, ExtendedSoundGroupBlock, BreakProgressMaskingBlock {

    public static final IntProperty LIGHT_LEVEL = Properties.LEVEL_15;

    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LIGHT_LEVEL);

    public static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 16, 16);

    public VisageRenameMeLaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        // bruh
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VisageRenameMeLaterBlockEntity(pos, state);
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage) {
            visage.spreadTemplate();
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage) {
            if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof VisageRenameMeLaterBlock)) {
                var targetState = blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, stack, hit));
                if (visage.templateState == null && targetState != null) {
                    visage.templateState = targetState;
                    world.setBlockState(pos, state.with(LIGHT_LEVEL, targetState.getLuminance()));

                    visage.markDirty();
                    return ItemActionResult.SUCCESS;
                }
            } else if (stack.getItem() instanceof AxeItem) {
                if (visage.templateState != null) {
                    visage.templateState = null;
                    world.setBlockState(pos, state.with(LIGHT_LEVEL, 0));
                    if (player instanceof ServerPlayerEntity serverPlayerEntity) Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayerEntity, pos, stack);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    stack.damage(1, player, LivingEntity.getSlotForHand(hand));

                    visage.markDirty();
                    return ItemActionResult.SUCCESS;

                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getParticleState(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage)) return state;
        if (visage.templateState == null) return state;

        return visage.templateState;
    }

    @Override
    public BlockSoundGroup getSoundGroup(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage)) return getSoundGroup(state);
        if (visage.templateState == null) return getSoundGroup(state);

        return visage.templateState.getSoundGroup();
    }

    @Override
    public BlockSoundGroup getSoundGroup(World world, BlockPos pos, BlockState state, ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (data == null) return getSoundGroup(state);
        if (!data.getNbt().contains("TemplateState", NbtElement.COMPOUND_TYPE)) return getSoundGroup(state);

        return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), data.getNbt().getCompound("TemplateState")).getSoundGroup();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage
                && !(visage.templateState == null)
                && visage.templateState != state
                && !(visage.templateState.getBlock() instanceof VisageRenameMeLaterBlock)) {
            visage.templateState.getBlock().randomDisplayTick(visage.templateState, world, pos, random);
        } else {
            super.randomDisplayTick(state, world, pos, random);
        }
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage &&
                !(visage.templateState == null) &&
                !(visage.templateState.getBlock() instanceof VisageRenameMeLaterBlock)) {
            return visage.templateState.isTransparent(world, pos);
        }
        return super.isTransparent(state, world, pos);
    }

    @Override
    public float calcMaskedBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof VisageRenameMeLaterBlockEntity visage &&
                visage.templateState != null &&
                !(visage.templateState.getBlock() instanceof VisageRenameMeLaterBlock)) {
            return visage.templateState.calcBlockBreakingDelta(player, world, pos);
        }

        return calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        return ActionResult.PASS;
    }
}
