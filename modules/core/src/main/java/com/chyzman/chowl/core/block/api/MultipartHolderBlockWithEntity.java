package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.MultipartHitResult;
import com.chyzman.chowl.core.multipart.api.MultipartVoxelShape;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MultipartHolderBlockWithEntity extends BlockWithEntity {
    private final BlockEntityFactory<?> factory;
    private final @Nullable PartType<?> initialPart;
    private static final VoxelShape UNSET = Util.make(() -> {
        VoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
        voxelSet.set(0, 0, 0);
        return new SimpleVoxelShape(voxelSet);
    });

    protected MultipartHolderBlockWithEntity(BlockEntityFactory<?> factory, @Nullable PartType<?> initialPart, Settings settings) {
        super(settings);
        this.factory = factory;
        this.initialPart = initialPart;
        CoreBlockEntities.MULTIPART.addSupportedBlock(this);
    }

    protected MultipartHolderBlockWithEntity(BlockEntityFactory<?> factory, Settings settings) {
        this(factory, null, settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
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

    protected VoxelShape getBlockOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return UNSET;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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

    protected ActionResult onNonPartUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return super.onUse(state, world, pos, player, hit);
    }

    protected ActionResult onNonPartUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    @ApiStatus.NonExtendable
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (hit instanceof MultipartHitResult hitResult && world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity holder) {
            Part part = Part.findPart(hitResult.getPart(), holder.getParts());
            if (part != null) {
                ActionResult result = part.onUse(state, world, pos, player, hit);
                if (result.isAccepted()) {
                    return result;
                }
            }
        }

        return onNonPartUse(state, world, pos, player, hit);
    }

    @Override
    @ApiStatus.NonExtendable
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hit instanceof MultipartHitResult hitResult && world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity holder) {
            Part part = Part.findPart(hitResult.getPart(), holder.getParts());
            if (part != null) {
                ActionResult result = part.onUseWithItem(stack, state, world, pos, player, hand, hit);
                if (result.isAccepted()) {
                    return result;
                }
            }
        }

        return onNonPartUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
