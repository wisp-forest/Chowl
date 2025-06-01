package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.pond.MultipartHitResult;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public abstract class MultipartHolderBlockWithEntity extends BlockWithEntity {
    private final BlockEntityFactory<?> factory;
    private final @Nullable PartType<?> initialPart;

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

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity multipart)) return VoxelShapes.fullCube();

        if (multipart.getShapeCache() != null) {
            return multipart.getShapeCache();
        }

        // Copy the parts to a temp list so it doesn't ConcurrentModificationException
        ArrayList<Part> parts = new ArrayList<>(multipart.getParts());
        Optional<VoxelShape> partsShape = parts.stream().map(part -> part.getOutlineShape(parts, world, pos, context)).reduce(VoxelShapes::union);
        VoxelShape shape = partsShape.orElse(VoxelShapes.empty());
        multipart.setShapeCache(shape);

        return shape;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Chowl.LOGGER.info("Part: {} | Client: {}", ((MultipartHitResult) hit).chowl$getHitMultipart(), world.isClient());
        return super.onUse(state, world, pos, player, hit);
    }
}
