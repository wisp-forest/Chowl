package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Optional;

public class FrameBlock extends MultipartHolderBlockWithEntity /*implements MultipartBlock<EmptyPart>*/ {
    public static final MapCodec<FrameBlock> CODEC = createCodec(FrameBlock::new);
    public static final VoxelShape OUTLINE = VoxelShapes.combine(VoxelShapes.fullCube(), VoxelShapes.union(
      createCuboidShape(2, 0, 2, 14, 16, 14),
      createCuboidShape(0, 2, 2, 16, 14, 14),
      createCuboidShape(2, 2, 0, 14, 14, 16)
    ), (a, b) -> a && !b);

    public FrameBlock(Settings settings) {
        super(MultipartBlockEntity::new, settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        Optional<MultipartBlockEntity> perhapsBE = world.getBlockEntity(pos, CoreBlockEntities.MULTIPART);
        if (perhapsBE.isEmpty()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        MultipartBlockEntity blockEntity = perhapsBE.get();

        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof Multipart<?> multipart) {
                blockEntity.addPart(multipart.getPart().create(blockEntity));
                return ActionResult.CONSUME;
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    /*@Override
    public PartType<EmptyPart> getPart() {
        return CoreParts.EMPTY;
    }*/
}
