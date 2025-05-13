package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Part {
    public static final Endec<List<Part>> SET_ENDEC = Endec.dispatchedStruct(
      Part::getPartEndec,
      Part::getId,
      MinecraftEndecs.IDENTIFIER
    ).listOf();

    private final PartType<?> type;
    private @Nullable BlockPos pos;
    private @Nullable MultipartHolderBlockEntity holder;
    private @Nullable World world;

    protected Part(PartType<?> type) {
        this.type = type;
    }

    public void init(MultipartHolderBlockEntity holder) {
        this.holder = holder;
        this.pos = holder.getPos();
        this.world = holder.getWorld();
    }

    public PartType<?> getType() {
        return type;
    }

    public Identifier getId() {
        return type.getId();
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    public @Nullable MultipartHolderBlockEntity getHolder() {
        return holder;
    }

    public @Nullable World getWorld() {
        return world;
    }

    public @Nullable Vec3d raycast(Vec3d start, Vec3d end, ShapeContext context) {
        BlockHitResult hitResult = this.getOutlineShape(holder.getParts(), world, pos, context).raycast(start, end, this.pos);
        if (hitResult == null) return null;
        return hitResult.getPos();
    }

    public abstract VoxelShape getOutlineShape(List<Part> parts, BlockView world, BlockPos pos, ShapeContext context);

    @SuppressWarnings("unchecked")
    private static StructEndec<Part> getPartEndec(Identifier identifier) {
        return (StructEndec<Part>) ChowlRegistries.PART.get(identifier).getEndec();
    }
}
