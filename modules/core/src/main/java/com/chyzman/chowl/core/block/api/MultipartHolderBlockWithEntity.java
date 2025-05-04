package com.chyzman.chowl.core.block.api;

import com.chyzman.chowl.core.block.FrameBlock;
import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.pond.ShapeContextExtended;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class MultipartHolderBlockWithEntity extends BlockWithEntity {
    private final BlockEntityFactory<?> factory;

    protected MultipartHolderBlockWithEntity(BlockEntityFactory<?> factory, Settings settings) {
        super(settings);
        this.factory = factory;
        CoreBlockEntities.MULTIPART.addSupportedBlock(this);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return factory.create(pos, state);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends MultipartHolderBlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartHolderBlockEntity multipart)) return VoxelShapes.fullCube();


        Optional<VoxelShape> partsShape = multipart.getParts().stream().map(part -> part.getOutlineShape(multipart.getParts(), world, pos, context)).reduce(VoxelShapes::union);

        return VoxelShapes.union(FrameBlock.OUTLINE, partsShape.orElse(VoxelShapes.empty()));

        /*// Return early so it doesn't turn the block item into air
        if (world == EmptyBlockView.INSTANCE) return VoxelShapes.fullCube();
        // Return full cube on normal context to know when a holder is being hit

        if (context.isHolding(asItem())) return VoxelShapes.fullCube();
        if (
          ((ShapeContextExtended) context).chowl$isHolding(stack -> stack.getItem() instanceof Multipart<?>)
          && !context.isDescending()
        ) return VoxelShapes.fullCube();

        return VoxelShapes.fullCube();//multipart.outlineShape;*/
    }
}
