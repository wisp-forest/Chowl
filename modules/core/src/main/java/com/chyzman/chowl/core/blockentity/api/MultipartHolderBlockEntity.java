package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderManager;
import com.chyzman.chowl.core.multipart.api.client.render.PartRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MultipartHolderBlockEntity extends BlockEntity {
    private @Nullable VoxelShape shapeCache = null;
    // TODO: yay we have a list of parts, now do magic stuff with it
    private final List<Part> parts = new ArrayList<>();

    public MultipartHolderBlockEntity(BlockEntityType<? extends MultipartHolderBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        parts.clear();
        clearShapeCache();
        if (view.contains("chowl:multipart")) {
            if (level != null && level.isClientSide()) {
                PartRenderManager.markForRebuild(getBlockPos());
            }

            List<? extends Part> partMap = view.get(Part.SET_ENDEC.keyed("chowl:multipart", Collections.emptyList()));
            partMap.forEach(part -> part.init(this));
            parts.addAll(partMap);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.put(Part.SET_ENDEC.keyed("chowl:multipart", Collections.emptyList()), parts);
    }

    public void markDirtyAndUpdateClients() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
        }
    }

    public void addPart(Part part) {
        parts.add(part);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (level != null && level.isClientSide()) {
            PartRenderManager.markForRebuild(getBlockPos());
        }
    }

    public void removePart(Part part) {
        parts.remove(part);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (level != null && level.isClientSide()) {
            PartRenderManager.markForRebuild(getBlockPos());
        }
    }

    public void removePart(int index) {
        parts.remove(index);
        markDirtyAndUpdateClients();
        clearShapeCache();

        if (level != null && level.isClientSide()) {
            PartRenderManager.markForRebuild(getBlockPos());
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
    public void setRemoved() {
        if (level != null && level.isClientSide()) {
            PartRenderManager.markForRebuild(getBlockPos());
        }

        super.setRemoved();
    }
}
