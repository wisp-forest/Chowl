package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.item.DrawerComponent;
import com.chyzman.chowl.item.DrawerPanelItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable, BlockButtonProvider, AttackInteractionReceiver {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape BASE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 1, 1),
            Block.createCuboidShape(0, 0, 15, 16, 1, 16),
            Block.createCuboidShape(0, 15, 15, 16, 16, 16),
            Block.createCuboidShape(0, 15, 0, 16, 16, 1),
            Block.createCuboidShape(0, 15, 1, 1, 16, 15),
            Block.createCuboidShape(0, 0, 1, 1, 1, 15),
            Block.createCuboidShape(15, 0, 1, 16, 1, 15),
            Block.createCuboidShape(15, 15, 1, 16, 16, 15),
            Block.createCuboidShape(15, 1, 0, 16, 15, 1),
            Block.createCuboidShape(0, 1, 0, 1, 15, 1),
            Block.createCuboidShape(0, 1, 15, 1, 15, 16),
            Block.createCuboidShape(15, 1, 15, 16, 15, 16)
    );

    public static final VoxelShape[] SIDES = {
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(1, 15, 1, 15, 16, 15),
            Block.createCuboidShape(1, 1, 0, 15, 15, 1),
            Block.createCuboidShape(1, 1, 15, 15, 15, 16),
            Block.createCuboidShape(0, 1, 1, 1, 15, 15),
            Block.createCuboidShape(15, 1, 1, 16, 15, 15),
    };

    public static final BlockButtonProvider.Button REMOVE_BUTTON = new Button(0.8f, 0.8f, 1, 1, true);

    public DrawerFrameBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.FALSE));
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
                } else {
                    var stack = stacks[side.getId()];
                    if (stack.getItem() instanceof DrawerPanelItem panel) {
                        if (!world.isClient) {
                            panel.insert(stack, stackInHand);
                            blockEntity.markDirty();
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public List<Button> listButtons(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
            return List.of();

        var selected = blockEntity.stacks[hitResult.getSide().getId()];

        if (selected.isEmpty()) return List.of();

        return List.of(REMOVE_BUTTON);
    }

    @Override
    public ActionResult attackButton(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player, Button button) {
        if (button != REMOVE_BUTTON) return ActionResult.PASS;
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
            return ActionResult.PASS;

        var selected = blockEntity.stacks[hitResult.getSide().getId()];

        player.getInventory().offerOrDrop(selected);
        blockEntity.stacks[hitResult.getSide().getId()] = ItemStack.EMPTY;
        blockEntity.markDirty();

        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        var res = BlockButtonProvider.super.onAttack(world, state, hitResult, player);
        if (res != ActionResult.PASS) return res;
        var side = getSide(hitResult);
        var blockEntity = world.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var selected = stacks[side.getId()];
            if (!selected.isEmpty()) {
                if (selected.getItem() instanceof DrawerPanelItem panel && !panel.getVariant(selected).isBlank()) {
                    var extracted = panel.extract(selected, player.isSneaking());
                    if (!extracted.isEmpty()) {
                        player.getInventory().offerOrDrop(extracted);
                        if (panel.getCount(selected).compareTo(BigInteger.ZERO) <= 0) {
                            var component = selected.get(DrawerPanelItem.COMPONENT);
                            component.setVariant(ItemVariant.blank());
                            selected.put(DrawerPanelItem.COMPONENT, component);
                        }
                        blockEntity.markDirty();
                        return ActionResult.SUCCESS;
                    }
                } else {
                    player.getInventory().offerOrDrop(selected);
                    stacks[side.getId()] = ItemStack.EMPTY;
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
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