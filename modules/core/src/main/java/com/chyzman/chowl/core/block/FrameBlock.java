package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.multipart.api.PartType;
import com.chyzman.chowl.core.multipart.impl.FramePart;
import com.chyzman.chowl.core.pond.ShapeContextExtended;
import com.chyzman.chowl.core.registry.CoreBlockEntities;
import com.chyzman.chowl.core.registry.CoreParts;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FrameBlock extends MultipartHolderBlockWithEntity implements Multipart<FramePart> {
    public static final MapCodec<FrameBlock> CODEC = createCodec(FrameBlock::new);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public FrameBlock(Settings settings) {
        super(MultipartBlockEntity::new, CoreParts.FRAME, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getBlockOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getBlockOutlineShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (((ShapeContextExtended) context).chowl$isHolding(stack -> stack.getItem() instanceof Multipart<?>)) {
            return VoxelShapes.fullCube();
        }

        return super.getOutlineShape(state, world, pos, context);
    }

    /*@Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<MultipartBlockEntity> perhapsBE = world.getBlockEntity(pos, CoreBlockEntities.MULTIPART);
        if (perhapsBE.isEmpty()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        MultipartBlockEntity blockEntity = perhapsBE.get();

        Multipart<?> multipart = switch (stack.getItem()) {
            case BlockItem blockItem when blockItem.getBlock() instanceof Multipart<?> mp -> mp;
            case Multipart<?> mp -> mp;
            case null, default -> null;
        };

        if (multipart != null) {
            if (!world.isClient()) {
                player.sendMessage(Text.literal("test"), false);
                //blockEntity.addPart(multipart.getPart().create(blockEntity));
            }
            //return ActionResult.CONSUME;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }*/

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public PartType<FramePart> getPart() {
        return CoreParts.FRAME;
    }
}
