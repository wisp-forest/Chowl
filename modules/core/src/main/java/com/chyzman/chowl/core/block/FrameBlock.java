package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.impl.EmptyPart;
import com.chyzman.chowl.core.multipart.impl.FramePart;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.chyzman.chowl.core.registry.CoreParts;
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
import net.minecraft.world.World;

import java.util.Optional;

public class FrameBlock extends MultipartHolderBlockWithEntity implements Multipart<FramePart> {
    public static final MapCodec<FrameBlock> CODEC = createCodec(FrameBlock::new);

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

    @Override
    public PartType<FramePart> getPart() {
        return CoreParts.FRAME;
    }
}
