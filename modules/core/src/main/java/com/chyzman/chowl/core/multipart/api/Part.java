package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class Part {
    public static final Endec<List<Part>> SET_ENDEC = Endec.dispatchedStruct(
      Part::getPartEndec,
      Part::getId,
      MinecraftEndecs.IDENTIFIER
    ).listOf();

    public static @Nullable Part findPart(byte[] index, List<Part> base) {
        Part part = null;
        for (byte i : index) {
            if (i >= base.size()) return null;

            part = base.get(i);
            base = part.getSubParts();
        }

        return part;
    }

    protected @Nullable final PartType<?> type;
    protected final List<Part> subParts;
    protected @Nullable BlockPos pos;
    protected @Nullable MultipartHolderBlockEntity holder;
    protected @Nullable World world;
    protected @Nullable VoxelShape shapeCache = null;

    /**
     * The base constructor for a Multipart part
     * @param type The {@link PartType} for this part. If the part is a
     *             sub part that isn't registered this may be null.
     */
    protected Part(@Nullable PartType<?> type) {
        this.type = type;
        this.subParts = new ArrayList<>();
    }

    public boolean isInitialized() {
        return holder != null;
    }

    public Part init(@NotNull MultipartHolderBlockEntity holder) {
        if (isInitialized()) {
            throw new IllegalArgumentException("Can not initialize an initialized part!");
        }

        this.holder = holder;
        this.pos = holder.getPos();
        this.world = holder.getWorld();

        return this;
    }

    public @Nullable PartType<?> getType() {
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

    public boolean hasWorld() {
        return world != null;
    }

    public void addSubPart(Part part) {
        this.subParts.add(part);
        this.shapeCache = null;
    }

    public @NotNull List<Part> getSubParts() {
        return subParts;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    public abstract VoxelShape getPartOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context);

    public VoxelShape getOutlineShape(List<Part> otherParts, BlockView world, BlockPos pos, ShapeContext context) {
        if (shapeCache != null) return shapeCache;

        List<VoxelShape> partsShapes = subParts.stream().map(part -> part.getOutlineShape(subParts, world, pos, context)).collect(Collectors.toList());
        partsShapes.addFirst(getPartOutlineShape(otherParts, world, pos, context));

        VoxelShape shape = new MultipartVoxelShape(partsShapes, true);
        setShapeCache(shape);

        return shape;
    }

    protected void setShapeCache(VoxelShape shape) {
        this.shapeCache = shape;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        if (isInitialized()) {
            holder.populateCrashReport(crashReportSection);
        }

        crashReportSection.add("(Chowl) Part Name", this::getNameForReport);
    }

    private @NotNull String getNameForReport() {
        return ChowlRegistries.PART.getId(this.getType()) + " // " + this.getClass().getCanonicalName();
    }

    @SuppressWarnings("unchecked")
    private static StructEndec<Part> getPartEndec(Identifier identifier) {
        return (StructEndec<Part>) ChowlRegistries.PART.get(identifier).getEndec();
    }
}
