package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.graph.ServerGraphStore;
import com.chyzman.chowl.item.PanelItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable, BlockButtonProvider, AttackInteractionReceiver {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape BASE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 2, 2),
            Block.createCuboidShape(0, 0, 14, 16, 2, 16),
            Block.createCuboidShape(0, 14, 14, 16, 16, 16),
            Block.createCuboidShape(0, 14, 0, 16, 16, 2),
            Block.createCuboidShape(0, 14, 2, 2, 16, 14),
            Block.createCuboidShape(0, 0, 2, 2, 2, 14),
            Block.createCuboidShape(14, 0, 2, 16, 2, 14),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(14, 2, 0, 16, 14, 2),
            Block.createCuboidShape(0, 2, 0, 2, 14, 2),
            Block.createCuboidShape(0, 2, 14, 2, 14, 16),
            Block.createCuboidShape(14, 2, 14, 16, 14, 16)
    );

    public static final VoxelShape[] SIDES = {
            Block.createCuboidShape(2, 0, 2, 14, 1, 14),
            Block.createCuboidShape(2, 14, 2, 14, 16, 14),
            Block.createCuboidShape(2, 2, 0, 14, 14, 1),
            Block.createCuboidShape(2, 2, 14, 14, 14, 16),
            Block.createCuboidShape(0, 2, 2, 1, 14, 14),
            Block.createCuboidShape(14, 2, 2, 16, 14, 14),
    };

    public static final PanelItem.Button DEFAULT_PANEL_BUTTON = new PanelItem.Button(
        1 / 8f, 1 / 8f, 7 / 8f, 7 / 8f,
        (world, drawerFrame, side, stack, player, hand) -> {
            var stacks = drawerFrame.stacks;
            var stackInHand = player.getStackInHand(hand);

            if (stackInHand.isEmpty()) return ActionResult.PASS;
            if (!stack.isEmpty()) return ActionResult.PASS;

            var temp = stackInHand.copy();
            temp.setCount(1);
            stacks[side.getId()] = temp;
            stackInHand.decrement(1);
            drawerFrame.markDirty();

            return ActionResult.SUCCESS;
        },
        (world, drawerFrame, side, stack, player) -> {
            var stacks = drawerFrame.stacks;

            if (stack.isEmpty()) return ActionResult.PASS;

            player.getInventory().offerOrDrop(stack);
            stacks[side.getId()] = ItemStack.EMPTY;
            drawerFrame.markDirty();
            return ActionResult.SUCCESS;
        },
        null);
    public static final BlockButtonProvider.Button REMOVE_BUTTON = new Button(7 / 8f, 7 / 8f, 1, 1, null,
            (world, state, hitResult, player) -> {
                if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
                    return ActionResult.PASS;

                var selected = blockEntity.stacks[hitResult.getSide().getId()];

                player.getInventory().offerOrDrop(selected);
                blockEntity.stacks[hitResult.getSide().getId()] = ItemStack.EMPTY;
                blockEntity.markDirty();

                return ActionResult.SUCCESS;
            },
            (client, entity, vertexConsumers, matrices) -> {
                var stack = Items.BARRIER.getDefaultStack();
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, client.getItemRenderer().getModels().getModel(stack));
            });

    public DrawerFrameBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() != this && world instanceof ServerWorld sw) {
            ServerGraphStore.get(sw).tryAdd(pos, state, findLinks(world, pos));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.getBlock() != this && world instanceof ServerWorld sw) {
            ServerGraphStore.get(sw).tryRemove(pos);
        }
    }

    private Set<BlockPos> findLinks(World world, BlockPos pos) {
        Set<BlockPos> links = new HashSet<>();

        for (BlockPos possible : BlockPos.iterate(
            pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
            pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
        )) {
            if (pos.equals(possible) || world.getBlockState(possible).getBlock() != this) continue;

            links.add(possible.toImmutable());
        }

        return links;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DrawerFrameBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var shape = BASE;
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            for (int i = 0; i < drawerFrameBlockEntity.stacks.length; i++) {
                var stack = drawerFrameBlockEntity.stacks[i];
                if (!stack.isEmpty()) {
                    shape = VoxelShapes.union(shape, SIDES[i]);
                }
            }

        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var shape = BASE;
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            for (int i = 0; i < drawerFrameBlockEntity.stacks.length; i++) {
                var stack = drawerFrameBlockEntity.stacks[i];
                if (!stack.isEmpty()) {
                    shape = VoxelShapes.union(shape, SIDES[i]);
                }
            }

        }
        return shape;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var res = BlockButtonProvider.super.onUse(state, world, pos, player, hand, hit);
        if (res != ActionResult.PASS) return res;
        var side = getSide(hit);
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var stackInHand = player.getStackInHand(hand);
            if (!stackInHand.isEmpty()) {
                if (stacks[side.getId()].isEmpty()) {
                    var temp = stackInHand.copy();
                    temp.setCount(1);
                    stacks[side.getId()] = temp;
                    stackInHand.decrement(1);
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public List<Button> listButtons(World world, BlockState state, BlockHitResult hitResult) {
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
            return List.of();

        var selected = blockEntity.stacks[hitResult.getSide().getId()];

        if (selected.isEmpty()) return List.of();

        List<Button> buttons = new ArrayList<>();
        buttons.add(REMOVE_BUTTON);

        if (selected.getItem() instanceof PanelItem panelItem) {
            var panelButtons = panelItem.listButtons(blockEntity, hitResult.getSide(), blockEntity.stacks[hitResult.getSide().getId()]);

            for (var panelButton : panelButtons) {
                buttons.add(panelButton.toBlockButton());
            }
        } else {
            buttons.add(DEFAULT_PANEL_BUTTON.toBlockButton());
        }

        return buttons;
    }

    @Override
    public @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        return BlockButtonProvider.super.onAttack(world, state, hitResult, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        var list = super.getDroppedStacks(state, builder);
        if (builder.getOptional(LootContextParameters.BLOCK_ENTITY) instanceof DrawerFrameBlockEntity blockEntity) {
            ItemStack stack = new ItemStack(this.asItem());
            BlockItem.setBlockEntityNbt(stack, blockEntity.getType(), blockEntity.createNbt());
            list.add(stack);
        }
        return list;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            if (!world.isClient && player.isCreative() && !Arrays.stream(drawerFrameBlockEntity.stacks).allMatch(ItemStack::isEmpty)) {
                ItemStack stack = new ItemStack(this.asItem());
                BlockItem.setBlockEntityNbt(stack, blockEntity.getType(), blockEntity.createNbt());
                if (!player.getInventory().contains(stack)) {
                    ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, stack);
                    itemEntity.setToDefaultPickupDelay();
                    world.spawnEntity(itemEntity);
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return super.getPickStack(world, pos, state);
    }

    public static Direction getSide(BlockHitResult hitResult) {
        return Arrays.stream(DIRECTIONS).min(Comparator.comparingDouble(
                i -> Vec3d.of(i.getVector()).squaredDistanceTo(
                        hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos())
                ))).orElse(hitResult.getSide());
    }
}