package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.PartRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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
    protected void readData(ReadView view) {
        super.readData(view);

        parts.clear();
        clearShapeCache();
        if (view.contains("chowl:multipart")) {
            if (world != null && world.isClient()) {
                PartRenderer.Manager.markForRebuild(getPos());
            }

            List<? extends Part> partMap = view.get(Part.SET_ENDEC.keyed("chowl:multipart", Collections.emptyList()));
            partMap.forEach(part -> part.init(this));
            parts.addAll(partMap);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.put(Part.SET_ENDEC.keyed("chowl:multipart", Collections.emptyList()), parts);
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

        if (world != null && world.isClient()) {
            PartRenderer.Manager.markForRebuild(getPos());
        }
    }

    public void removePart(Part part) {
        parts.remove(part);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (world != null && world.isClient()) {
            PartRenderer.Manager.markForRebuild(getPos());
        }
    }

    public void removePart(int index) {
        parts.remove(index);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (world != null && world.isClient()) {
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
        if (world != null && world.isClient()) {
            PartRenderer.Manager.markForRebuild(getPos());
        }

        super.markRemoved();
    }
}
