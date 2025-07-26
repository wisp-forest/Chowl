package com.chyzman.chowl.test.multipart;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.test.registry.TestParts;
import com.mojang.serialization.Codec;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class FramePanel extends Part {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 2, 0, 14, 14, 2);
    public static final Codec<ItemStack> OPTIONAL_UNCOUNTED_CODEC = Codecs.optional(ItemStack.UNCOUNTED_CODEC).xmap(optional -> optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    public static final StructEndec<FramePanel> ENDEC = StructEndecBuilder.of(
      CodecUtils.toEndec(Direction.CODEC).fieldOf("face", FramePanel::getFace),
      CodecUtils.toEndec(OPTIONAL_UNCOUNTED_CODEC).fieldOf("item", FramePanel::getItem),
      CodecUtils.toEndec(ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(5)).fieldOf("upgrades", FramePanel::getUpgrades),
      Endec.INT.fieldOf("count", FramePanel::getCount),
      Endec.INT.fieldOf("size", FramePanel::getSize),
      FramePanel::new
    );

    private final Direction face;
    private ItemStack item = ItemStack.EMPTY;
    private final DefaultedList<ItemStack> upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int count = 0;
    private int size = 64;

    public FramePanel(List<Part> parts) {
        super(TestParts.FRAME_PANEL_PART);
        this.face = Direction.NORTH;
    }

    public FramePanel(Direction face, ItemStack item, List<ItemStack> upgrades, int count, int size) {
        super(TestParts.FRAME_PANEL_PART);
        this.face = face;
        this.item = item;
        for (int i = 0; i < upgrades.size(); i++) {
            this.upgrades.set(i, upgrades.get(i));
        }
        this.count = count;
        this.size = size;

        this.addSubPart(new RemovePart());
    }

    public Direction getFace() {
        return face;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    public ItemStack getUpgrade(int index) {
        return this.upgrades.get(index);
    }

    public void setUpgrade(int index, ItemStack upgrade) {
        this.upgrades.set(index, upgrade);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSize() {
        return size;
    }

    @Override
    public ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isEmpty() && item.isEmpty()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (stack.isEmpty()) {
            setItem(ItemStack.EMPTY);
            count = 0;
            return ActionResult.SUCCESS;
        }

        if (count >= size) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (item.isEmpty()) {
            setItem(stack.copyWithCount(1));
        }

        if (ItemStack.areItemsAndComponentsEqual(stack, item)) {
            ItemStack split = stack.splitUnlessCreative(size - count, player);
            count += split.getCount();
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (face) {
            case NORTH -> SHAPE;
            case SOUTH -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, 2);
            case WEST -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, 1);
            case EAST -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, -1);
            case UP -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.X, -1);
            case DOWN -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.X, 1);
        };
    }

    public class RemovePart extends Part {
        private static final VoxelShape SHAPE = Block.createCuboidShape(0, 14, -0.25, 2, 16, 0);

        protected RemovePart() {
            super(null);
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
            FramePanel.this.getHolder().removePart(FramePanel.this);
            return ActionResult.SUCCESS;
        }

        @Override
        public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
            return switch (face) {
                case NORTH -> SHAPE;
                case SOUTH -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, 2);
                case WEST -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, 1);
                case EAST -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.Y, -1);
                case UP -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.X, -1);
                case DOWN -> VoxelShapeHelper.rotate(SHAPE, Direction.Axis.X, 1);
            };
        }
    }
}
