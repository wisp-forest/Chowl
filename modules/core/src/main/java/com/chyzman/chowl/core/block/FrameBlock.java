package com.chyzman.chowl.core.block;

import com.chyzman.chowl.core.blockentity.FrameBlockEntity;
import com.chyzman.chowl.core.graph.NetworkRegistry;
import com.chyzman.chowl.core.multipart.api.Multipart;
import com.chyzman.chowl.core.block.api.MultipartHolderBlockWithEntity;
import com.chyzman.chowl.core.blockentity.MultipartBlockEntity;
import com.chyzman.chowl.core.pond.ExtendedShapeContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FrameBlock extends MultipartHolderBlockWithEntity {
    public static final MapCodec<FrameBlock> CODEC = simpleCodec(FrameBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape OUTLINE = Shapes.joinUnoptimized(Shapes.block(), Shapes.or(
      Block.box(2, 0, 2, 14, 16, 14),
      Block.box(0, 2, 2, 16, 14, 14),
      Block.box(2, 2, 0, 14, 14, 16)
    ), (a, b) -> a && !b);

    public FrameBlock(Properties settings) {
        super(FrameBlockEntity::new, settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);

        if (world instanceof ServerLevel sw) NetworkRegistry.UNIVERSE.getGraphWorld(sw).updateNodes(pos);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, world, pos, moved);

        NetworkRegistry.UNIVERSE.getGraphWorld(world).updateNodes(pos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getBlockOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return OUTLINE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (((ExtendedShapeContext) context).chowl$isHolding(stack -> stack.getItem() instanceof Multipart<?>)) {
            return Shapes.block();
        }

        return super.getShape(state, world, pos, context);
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(blockPos);

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
