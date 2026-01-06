package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    protected @Nullable Level level;
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
        this.pos = holder.getBlockPos();
        this.level = holder.getLevel();

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

    public @Nullable Level getLevel() {
        return level;
    }

    public boolean hasWorld() {
        return level != null;
    }

    public void addSubPart(Part part) {
        this.subParts.add(part);
        this.shapeCache = null;
    }

    public @NotNull List<Part> getSubParts() {
        return subParts;
    }

    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public abstract VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context);

    public VoxelShape getOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
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

    public void populateCrashReport(CrashReportCategory crashReportSection) {
        if (isInitialized()) {
            holder.fillCrashReportCategory(crashReportSection);
        }

        crashReportSection.setDetail("(Chowl) Part Name", this::getNameForReport);
    }

    private @NotNull String getNameForReport() {
        return ChowlRegistries.PART.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
    }

    @SuppressWarnings("unchecked")
    private static StructEndec<Part> getPartEndec(Identifier identifier) {
        return (StructEndec<Part>) ChowlRegistries.PART.getValue(identifier).getEndec();
    }
}
