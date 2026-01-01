package com.chyzman.chowl.core.megaBlock.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MegaBlock extends Block {
    public final @Nullable IntProperty X, Y, Z;
    private final Vec3i size;

    public MegaBlock(
            @Nullable IntProperty x,
            @Nullable IntProperty y,
            @Nullable IntProperty z,
            Settings settings
    ) {
        super(settings);
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.size = new Vec3i(
                x == null ? 1 : x.getValues().size(),
                y == null ? 1 : y.getValues().size(),
                z == null ? 1 : z.getValues().size()
        );
    }

    public Vec3i localPos(BlockState state) {
        return new Vec3i(xPos(state), yPos(state), zPos(state));
    }

    public BlockState set(BlockState state, int x, int y, int z) {
        return xPos(yPos(zPos(state, z), y), x);
    }

    public int xPos(BlockState state) {
        return X == null ? 0 : state.get(X);
    }

    public BlockState xPos(BlockState state, int x) {
        return X == null ? state : state.with(X, x);
    }

    public int yPos(BlockState state) {
        return Y == null ? 0 : state.get(Y);
    }

    public BlockState yPos(BlockState state, int y) {
        return Y == null ? state : state.with(Y, y);
    }

    public int zPos(BlockState state) {
        return Z == null ? 0 : state.get(Z);
    }

    public BlockState zPos(BlockState state, int z) {
        return Z == null ? state : state.with(Z, z);
    }

    public Vec3i size(BlockState state) {
        return size;
    }

    public boolean isOrigin(BlockState state) {
        return xPos(state) == 0 && yPos(state) == 0 && zPos(state) == 0;
    }

    public BlockState copyTo(BlockState from, BlockState to) {
        return from;
    }

    @Nullable
    public BlockState getExpectedNeighborState(BlockState state, Direction direction) {
        var x = xPos(state) + direction.getOffsetX();
        var y = yPos(state) + direction.getOffsetY();
        var z = zPos(state) + direction.getOffsetZ();
        var size = this.size(state);
        if (x < 0 || x >= size.getX() || y < 0 || y >= size.getY() || z < 0 || z >= size.getZ()) return null;
        return set(state, x, y, z);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        var expectedState = getExpectedNeighborState(state, direction);
        if (expectedState == null) return state;
        if (neighborState.isOf(this)) expectedState = copyTo(expectedState, neighborState);
        if (neighborState != expectedState) return Blocks.AIR.getDefaultState();
        return copyTo(state, neighborState);
    }

    @Override
    protected BlockSoundGroup getSoundGroup(BlockState state) {
        if (isOrigin(state)) return super.getSoundGroup(state);
        return BlockSoundGroup.INTENTIONALLY_EMPTY;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        for (Direction value : Direction.values()) {
            var expectedState = getExpectedNeighborState(state, value);
            if (expectedState == null) continue;
            var neighborPos = pos.offset(value);
            var neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this)) {
                world.breakBlock(pos, false);
                return;
            }
        }
    }

    public VoxelShape getFullShape(BlockView world, BlockState state, BlockPos pos, ShapeContext context) {
        var shape = VoxelShapes.empty();
        var origin = pos.subtract(localPos(state));
        for (BlockPos blockPos : BlockPos.iterate(origin, origin.add(this.size(state)))) {
            var subState = world.getBlockState(blockPos);
            if (subState.isOf(this)) shape = VoxelShapes.union(shape, subState.getOutlineShape(world, blockPos, context).offset(new Vec3d(localPos(subState))));
        }
        return shape.offset(new Vec3d(Vec3i.ZERO.subtract(localPos(state))));
    }
}
