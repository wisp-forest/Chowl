package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import com.chyzman.chowl.core.multipart.api.MultipartVoxelShape;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MultipartHolderBlockWithEntity extends BaseEntityBlock {
    private final BlockEntityFactory<?> factory;
    private final @Nullable PartType<?> initialPart;
    private static final VoxelShape UNSET = Util.make(() -> {
        DiscreteVoxelShape voxelSet = new BitSetDiscreteVoxelShape(1, 1, 1);
        voxelSet.fill(0, 0, 0);
        return new CubeVoxelShape(voxelSet);
    });

    protected MultipartHolderBlockWithEntity(BlockEntityFactory<?> factory, @Nullable PartType<?> initialPart, Properties settings) {
        super(settings);
        this.factory = factory;
        this.initialPart = initialPart;
//        CoreBlockEntities.MULTIPART.addSupportedBlock(this);
    }

    protected MultipartHolderBlockWithEntity(BlockEntityFactory<?> factory, Properties settings) {
        this(factory, null, settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var holder = factory.create(pos, state);
        if (initialPart != null) {
            holder.addPart(initialPart.create(holder));
        }
        return holder;
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends MultipartHolderBlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    public VoxelShape getBlockOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return UNSET;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape blockOutlineShape = getBlockOutlineShape(state, world, pos, context);
        if (!(world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity multipart)) {
            return blockOutlineShape;
        }

        if (multipart.getShapeCache() != null) {
            return multipart.getShapeCache();
        }

        // Copy the parts to a temp list so it doesn't ConcurrentModificationException
        ArrayList<Part> parts = new ArrayList<>(multipart.getParts());
        List<VoxelShape> partsShapes = parts.stream().map(part -> part.getOutlineShape(parts, world, pos, context)).collect(Collectors.toList());
        boolean hasBaseShape = blockOutlineShape != UNSET;
        if (hasBaseShape) partsShapes.addFirst(blockOutlineShape);
        VoxelShape shape = new MultipartVoxelShape(partsShapes, hasBaseShape);
        multipart.setShapeCache(shape);

        return shape;
    }

    protected InteractionResult onUseWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    protected InteractionResult onUseItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    @NotNull
    @ApiStatus.NonExtendable
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (hit instanceof MultipartHitResult hitResult && world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity holder) {
            Part part = Part.findPart(hitResult.getPart(), holder.getParts());
            if (part != null) {
                InteractionResult result = part.onUse(state, world, pos, player, hit);
                if (result.consumesAction()) {
                    return result;
                }
            }
        }

        return onUseWithoutItem(state, world, pos, player, hit);
    }

    @Override
    @NotNull
    @ApiStatus.NonExtendable
    protected InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (hit instanceof MultipartHitResult hitResult && world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity holder) {
            Part part = Part.findPart(hitResult.getPart(), holder.getParts());
            if (part != null) {
                InteractionResult result = part.onUseWithItem(stack, state, world, pos, player, hand, hit);
                if (result.consumesAction()) {
                    return result;
                }
            }
        }

        return onUseItem(stack, state, world, pos, player, hand, hit);
    }
}
