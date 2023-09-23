package com.chyzman.chowl.block;

import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.item.DrawerComponent;
import com.chyzman.chowl.item.DrawerPanelItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable, AttackInteractionReceiver {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

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
                    if (stack.hasNbt() || stack.getItem() instanceof DrawerPanelItem) {
                        var nbt = stack.getOrCreateNbt();
                        if (nbt.contains("DrawerComponent") || stack.getItem() instanceof DrawerPanelItem) {
                            if (!world.isClient) {
                                var drawerComponent = new DrawerComponent();
                                if (nbt.contains("DrawerComponent")) {
                                    drawerComponent.readNbt(nbt.getCompound("DrawerComponent"));
                                } else {
                                    drawerComponent.itemVariant = ItemVariant.of(stackInHand);
                                }
                                stackInHand.setCount(drawerComponent.insert(stackInHand));
                                var drawerNbt = new NbtCompound();
                                drawerComponent.writeNbt(drawerNbt);
                                nbt.put("DrawerComponent", drawerNbt);
                                stacks[hit.getSide().getId()] = stack;
                                drawerFrameBlockEntity.stacks = stacks;
                                drawerFrameBlockEntity.markDirty();
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        var blockEntity = world.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var panel = stacks[hitResult.getSide().getId()];
            if (!panel.isEmpty()) {
                Vector3f vec = hitResult.getPos().subtract(hitResult.getBlockPos().toCenterPos()).toVector3f();
                vec.rotate(hitResult.getSide().getRotationQuaternion().invert()).rotate(Direction.WEST.getRotationQuaternion());
                vec.add(0.5f, 0.5f, 0.5f);
                if ((vec.y > 0.8f && vec.z > 0.8f) || !(panel.getItem() instanceof DrawerPanelItem)) {
                    player.getInventory().offerOrDrop(panel);
                    stacks[hitResult.getSide().getId()] = ItemStack.EMPTY;
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                } else {
                    var drawerComponent = new DrawerComponent();
                    if (panel.hasNbt()) {
                        var nbt = panel.getOrCreateNbt();
                        if (nbt.contains("DrawerComponent")) {
                            drawerComponent.readNbt(nbt.getCompound("DrawerComponent"));
                            var amount = player.isSneaking() ? drawerComponent.itemVariant.getItem().getMaxCount() : 1;
                            var stack = drawerComponent.extract(amount);
                            if (!stack.isEmpty()) {
                                var drawerNbt = new NbtCompound();
                                if (drawerComponent.count.compareTo(BigInteger.ZERO) > 0) {
                                    drawerComponent.writeNbt(drawerNbt);
                                }
                                nbt.put("DrawerComponent", drawerNbt);
                                player.getInventory().offerOrDrop(stack);
                                stacks[hitResult.getSide().getId()] = panel;
                                drawerFrameBlockEntity.stacks = stacks;
                                drawerFrameBlockEntity.markDirty();
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            ItemScatterer.spawn((World) world, pos, DefaultedList.copyOf(ItemStack.EMPTY, drawerFrameBlockEntity.stacks));
        }
    }
}