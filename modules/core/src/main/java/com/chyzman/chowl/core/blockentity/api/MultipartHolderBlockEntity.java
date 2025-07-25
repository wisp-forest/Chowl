package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MultipartHolderBlockEntity extends BlockEntity {
    private @Nullable VoxelShape shapeCache = null;
    // TODO: yay we have a list of parts, now do magic stuff with it
    private final List<Part> parts = new ArrayList<>();

    public MultipartHolderBlockEntity(BlockEntityType<? extends MultipartHolderBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        parts.clear();
        clearShapeCache();
        if (nbt.contains("chowl:multipart")) {
            if (world != null && world.isClient) {
                PartRenderer.Manager.markForRebuild(getPos());
            }

            List<? extends Part> partMap = Part.SET_ENDEC.decode(SerializationContext.empty(), NbtDeserializer.of(nbt.get("chowl:multipart")));
            partMap.forEach(part -> part.init(this));
            parts.addAll(partMap);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        NbtSerializer serializer = NbtSerializer.of();
        Part.SET_ENDEC.encode(SerializationContext.empty(), serializer, parts);
        nbt.put("chowl:multipart", serializer.result());
    }

    public void markDirtyAndUpdateClients() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }

    public void addPart(Part part) {
        parts.add(part);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (world != null && world.isClient) {
            PartRenderer.Manager.markForRebuild(getPos());
        }
    }

    public void removePart(Part part) {
        parts.remove(part);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (world != null && world.isClient) {
            PartRenderer.Manager.markForRebuild(getPos());
        }
    }

    public void removePart(int index) {
        parts.remove(index);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (world != null && world.isClient) {
            PartRenderer.Manager.markForRebuild(getPos());
        }
    }

    public List<Part> getParts() {
        return parts;
    }

    public @Nullable VoxelShape getShapeCache() {
        return shapeCache;
    }

    public void setShapeCache(@Nullable VoxelShape shapeCache) {
        this.shapeCache = shapeCache;
    }

    public void clearShapeCache() {
        shapeCache = null;
    }

    @Override
    public void markRemoved() {
        if (world != null && world.isClient) {
            PartRenderer.Manager.markForRebuild(getPos());
        }

        super.markRemoved();
    }
}
