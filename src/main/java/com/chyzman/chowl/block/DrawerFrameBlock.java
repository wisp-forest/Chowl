package com.chyzman.chowl.block;

import com.chyzman.chowl.block.button.BlockButton;
import com.chyzman.chowl.block.button.BlockButtonProvider;
import com.chyzman.chowl.block.button.ButtonRenderCondition;
import com.chyzman.chowl.classes.AttackInteractionReceiver;
import com.chyzman.chowl.graph.ServerGraphStore;
import com.chyzman.chowl.item.component.LockablePanelItem;
import com.chyzman.chowl.item.component.PanelItem;
import com.chyzman.chowl.block.button.ButtonRenderer;
import com.chyzman.chowl.registry.ChowlRegistry;
import com.chyzman.chowl.util.BlockSideUtils;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.chyzman.chowl.item.component.LockablePanelItem.LOCK_BUTTON;

public class DrawerFrameBlock extends BlockWithEntity implements Waterloggable, BlockButtonProvider, AttackInteractionReceiver {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty TICKING = BooleanProperty.of("ticking");

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

    public static final BlockButton DEFAULT_PANEL_BUTTON = PanelItem.buttonBuilder(2, 2, 14, 14)
            .onUse((world, drawerFrame, side, stack, player, hand) -> {
                var stacks = drawerFrame.stacks;
                var stackInHand = player.getStackInHand(hand);

                if (stackInHand.isEmpty()) return ActionResult.PASS;
                if (!stack.isEmpty()) return ActionResult.PASS;

                var temp = stackInHand.copy();
                temp.setCount(1);
                stacks.set(side.getId(), new Pair<>(temp, stacks.get(side.getId()).getRight()));
                stackInHand.decrement(1);
                drawerFrame.markDirty();

                return ActionResult.SUCCESS;
            })
            .onAttack((world, drawerFrame, side, stack, player) -> {
                var stacks = drawerFrame.stacks;

                if (stack.isEmpty()) return ActionResult.PASS;

                player.getInventory().offerOrDrop(stack);
                stacks.set(side.getId(), new Pair<>(ItemStack.EMPTY, 0));
                drawerFrame.markDirty();
                return ActionResult.SUCCESS;
            }).build();
    public static final BlockButton REMOVE_BUTTON = BlockButton.builder(14, 14, 16, 16)
            .onAttack((world, state, hitResult, player) -> {
                if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
                    return ActionResult.PASS;

                var side = BlockSideUtils.getSide(hitResult);
                var selected = blockEntity.stacks.get(side.getId()).getLeft();

                player.getInventory().offerOrDrop(selected);
                blockEntity.stacks.set(side.getId(), new Pair<>(ItemStack.EMPTY, 0));
                blockEntity.markDirty();

                return ActionResult.SUCCESS;
            })
            .renderWhen(ButtonRenderCondition.BUTTON_FOCUSED)
            .renderer(ButtonRenderer.stack(Items.BARRIER.getDefaultStack()))
            .build();
    public static final BlockButton CONFIG_BUTTON = BlockButton.builder(12, 14, 14, 16)
            .onUse((state, world, pos, player, hand, hitResult) -> {
                if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
                    return ActionResult.PASS;

                var side = BlockSideUtils.getSide(hitResult);
                var selected = blockEntity.stacks.get(side.getId()).getLeft();

                if (!(selected.getItem() instanceof PanelItem panel)) return ActionResult.FAIL;
                if (!panel.hasConfig()) return ActionResult.FAIL;

                panel.openConfig(selected, player, newStack -> {
                    var currentRotation = blockEntity.stacks.get(side.getId()).getRight();
                    blockEntity.stacks.set(side.getId(), new Pair<>(newStack, currentRotation));

                    blockEntity.markDirty();
                });
                return ActionResult.SUCCESS;
            })
            .renderWhen(ButtonRenderCondition.BUTTON_FOCUSED)
            .renderer(ButtonRenderer.stack(Items.STRUCTURE_VOID.getDefaultStack()))
            .build();

    public DrawerFrameBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.FALSE).with(TICKING, Boolean.FALSE));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (oldState.getBlock() != this && world instanceof ServerWorld sw) {
            ServerGraphStore.get(sw).tryAdd(pos, state, findLinks(world, pos));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
                for (Pair<ItemStack, Integer> stack : drawerFrameBlockEntity.stacks) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack.getLeft());
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        if (newState.getBlock() != this && world instanceof ServerWorld sw) {
            ServerGraphStore.get(sw).tryRemove(pos);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (state.get(TICKING)) {
            return (world1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
                    drawerFrameBlockEntity.tick(world1, pos, state1);
                }
            };
        } else {
            return super.getTicker(world, state, type);
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
            for (int i = 0; i < drawerFrameBlockEntity.stacks.size(); i++) {
                var stack = drawerFrameBlockEntity.stacks.get(i).getLeft();
                if (!stack.isEmpty() && stack.getItem() != ChowlRegistry.PHANTOM_PANEL_ITEM) {
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
            for (int i = 0; i < drawerFrameBlockEntity.stacks.size(); i++) {
                var stack = drawerFrameBlockEntity.stacks.get(i).getLeft();
                if (!stack.isEmpty() && stack.getItem() != ChowlRegistry.PHANTOM_PANEL_ITEM) {
                    shape = VoxelShapes.union(shape, SIDES[i]);
                }
            }

        }
        return shape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, TICKING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var side = BlockSideUtils.getSide(hit);
        var orientation = 0;
        if (side == Direction.UP || (side == Direction.DOWN && player.getHorizontalFacing().getAxis() == Direction.Axis.Z)) {
            orientation = (int) player.getHorizontalFacing().getOpposite().asRotation() / 90;
        } else if (side == Direction.DOWN) {
            orientation = (int) player.getHorizontalFacing().asRotation() / 90;

        }
        var res = BlockButtonProvider.super.onUse(state, world, pos, player, hand, hit);
        if (res != ActionResult.PASS) return res;
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var stacks = drawerFrameBlockEntity.stacks;
            var stackInHand = player.getStackInHand(hand);
            if (!stackInHand.isEmpty()) {
                if (stacks.get(side.getId()).getLeft().isEmpty()) {
                    var temp = ItemOps.singleCopy(stackInHand);
                    stacks.set(side.getId(), new Pair<>(ItemOps.singleCopy(temp), orientation));
                    stackInHand.decrement(1);
                    drawerFrameBlockEntity.markDirty();
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public List<BlockButton> listButtons(World world, BlockState state, BlockHitResult hitResult) {
        if (!(world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerFrameBlockEntity blockEntity))
            return List.of();

        var side = BlockSideUtils.getSide(hitResult);

        var selected = blockEntity.stacks.get(side.getId()).getLeft();

        if (selected.isEmpty()) return List.of();

        List<BlockButton> buttons = new ArrayList<>();
        buttons.add(REMOVE_BUTTON);

        if (selected.getItem() instanceof LockablePanelItem) {
            buttons.add(LOCK_BUTTON);
        }

        if (selected.getItem() instanceof PanelItem panelItem) {
            if (panelItem.hasConfig())
                buttons.add(CONFIG_BUTTON);

            buttons.addAll(panelItem.listButtons(blockEntity, side, blockEntity.stacks.get(side.getId()).getLeft()));
        } else {
            buttons.add(DEFAULT_PANEL_BUTTON);
        }

        return buttons;
    }

    @Override
    public @NotNull ActionResult onAttack(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        return BlockButtonProvider.super.onAttack(world, state, hitResult, player);
    }

    @Override
    public @NotNull ActionResult onDoubleClick(World world, BlockState state, BlockHitResult hitResult, PlayerEntity player) {
        return BlockButtonProvider.super.onDoubleClick(world, state, hitResult, player);
    }

//    @Override
//    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
//        var list = super.getDroppedStacks(state, builder);
//        if (builder.getOptional(LootContextParameters.BLOCK_ENTITY) instanceof DrawerFrameBlockEntity blockEntity) {
//            ItemStack stack = new ItemStack(this.asItem());
//            BlockItem.setBlockEntityNbt(stack, blockEntity.getType(), blockEntity.createNbt());
//            list.add(stack);
//        }
//        return list;
//    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return super.getPickStack(world, pos, state);
    }


    public static int getOrientation(World world, BlockHitResult hitResult) {
        var blockEntity = world.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity instanceof DrawerFrameBlockEntity drawerFrameBlockEntity) {
            var side = BlockSideUtils.getSide(hitResult);
            return drawerFrameBlockEntity.stacks.get(side.getId()).getRight();
        }
        return 0;
    }
}