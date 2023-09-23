package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.item.DrawerComponent;
import com.chyzman.chowl.item.DrawerPanelItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.math.BigInteger;
import java.util.List;

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable, BlockButtonProvider, AttackInteractionReceiver {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
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
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var stackInHand = player.getStackInHand(hand);
            if (!stackInHand.isEmpty()) {
                if (stacks[hit.getSide().getId()].isEmpty()) {
                    var temp = stackInHand.copy();
                    temp.setCount(1);
                    stacks[hit.getSide().getId()] = temp;
                    stackInHand.decrement(1);
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                } else {
                    var stack = stacks[hit.getSide().getId()];
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
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity)) return List.of();

        var selected = blockEntity.stacks[hitResult.getSide().getId()];

        if (selected.isEmpty()) return List.of();

        return List.of(REMOVE_BUTTON);
    }

    @Override
    public ActionResult attackButton(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player, Button button) {
        if (button != REMOVE_BUTTON) return ActionResult.PASS;
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity)) return ActionResult.PASS;

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

        var blockEntity = world.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var selected = stacks[hitResult.getSide().getId()];
            if (!selected.isEmpty()) {
                if (selected.getItem() instanceof DrawerPanelItem panel) {
                    var extracted = panel.extract(selected, player.isSneaking());

                    if (!extracted.isEmpty()) {
                        player.getInventory().offerOrDrop(extracted);
                        blockEntity.markDirty();
                        return ActionResult.SUCCESS;
                    }
                } else {
                    player.getInventory().offerOrDrop(selected);
                    stacks[hitResult.getSide().getId()] = ItemStack.EMPTY;
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
}