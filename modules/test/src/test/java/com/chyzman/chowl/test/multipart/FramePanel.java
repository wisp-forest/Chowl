package com.chyzman.chowl.test.multipart;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import com.chyzman.chowl.test.registry.TestParts;
import com.mojang.serialization.Codec;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.BlockView;
import java.util.List;
import java.util.Optional;

public class FramePanel extends Part implements ItemOwner {
    private static final VoxelShape SHAPE = Block.box(2, 2, 0, 14, 14, 2);
    public static final Codec<ItemStack> OPTIONAL_UNCOUNTED_CODEC = ExtraCodecs.optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC).xmap(optional -> optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
    public static final StructEndec<FramePanel> ENDEC = StructEndecBuilder.of(
      CodecUtils.toEndec(Direction.CODEC).fieldOf("face", FramePanel::getFace),
      CodecUtils.toEndec(OPTIONAL_UNCOUNTED_CODEC).fieldOf("item", FramePanel::getItem),
      CodecUtils.toEndec(ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(8)).fieldOf("upgrades", FramePanel::getUpgrades),
      Endec.INT.fieldOf("count", FramePanel::getCount),
      Endec.INT.fieldOf("size", FramePanel::getSize),
      FramePanel::new
    );

    // This is to allow for rendering the item on the player's hand while keeping the rendering smooth
    private ItemOwner itemOwner;

    private final Direction face;
    private ItemStack item = ItemStack.EMPTY;
    private final NonNullList<ItemStack> upgrades = NonNullList.withSize(8, ItemStack.EMPTY);
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

    public void setItemOwner(ItemOwner itemOwner) {
        this.itemOwner = itemOwner;
    }

    public ItemOwner getItemOwner() {
        return itemOwner == null ? this : itemOwner;
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

    public NonNullList<ItemStack> getUpgrades() {
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
    public InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() && item.isEmpty()) {
            return super.onUseWithItem(stack, state, level, pos, player, hand, hit);
        }

        if (stack.isEmpty()) {
            setItem(ItemStack.EMPTY);
            count = 0;
            return InteractionResult.SUCCESS;
        }

        if (count >= size) {
            return super.onUseWithItem(stack, state, level, pos, player, hand, hit);
        }

        if (item.isEmpty()) {
            setItem(stack.copyWithCount(1));
        }

        if (ItemStack.isSameItem(stack, item)) {
            ItemStack split = stack.consumeAndReturn(size - count, player);
            count += split.getCount();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
        return VoxelShapeHelper.rotate(SHAPE, face);

    }


    @Override
    public Level level() {
        return level;
    }

    @Override
    public Vec3 position() {
        return pos.getCenter();
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return face.toYRot();
    }

    public class RemovePart extends Part {
        private static final VoxelShape SHAPE = Block.box(0, 14, -0.25, 2, 16, 0);

        protected RemovePart() {
            super(null);
        }


        @Override
        public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
            FramePanel.this.getHolder().removePart(FramePanel.this);
            return InteractionResult.SUCCESS;
        }

        @Override
        public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
            return VoxelShapeHelper.rotate(SHAPE, face);
        }
    }
}
