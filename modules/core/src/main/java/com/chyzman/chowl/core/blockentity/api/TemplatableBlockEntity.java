package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.client.ChowlClient;
import com.chyzman.chowl.core.registry.ChowlComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

public abstract class TemplatableBlockEntity extends BlockEntity {
    protected static final IntProperty LIGHT_LEVEL = Properties.LEVEL_15;

    private BlockState templateState = null;

    public TemplatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        templateState = view.read("TemplateState", BlockState.CODEC).orElse(null);

        if (world != null && world.isClient()) ChowlClient.reloadPos(world, pos);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        if (templateState != null) view.put("TemplateState", BlockState.CODEC, templateState);
    }

    @Override
    public @Nullable Object getRenderData() {
        return templateState;
    }

    @MustBeInvokedByOverriders
    @Override
    protected void addComponents(ComponentMap.Builder components) {
        if (templateState != null) {
            components.add(ChowlComponents.TEMPLATE_STATE, templateState);
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void readComponents(ComponentsAccess components) {
        templateState = components.get(ChowlComponents.TEMPLATE_STATE);
    }

    @MustBeInvokedByOverriders
    @SuppressWarnings("deprecation")
    @Override
    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("TemplateState");
    }

    public BlockState templateState() {
        return templateState;
    }

    public void setTemplateState(BlockState templateState) {
        BlockState old = this.templateState;

        this.templateState = templateState;

        if (old != templateState) {
            markDirty();

            if (world.isClient()) {
                ChowlClient.reloadPos(world, pos);
            } else {
                if (getCachedState().contains(LIGHT_LEVEL)) {
                    world.setBlockState(pos, getCachedState()
                        .with(LIGHT_LEVEL, templateState != null ? templateState.getLuminance() : 0));
                }
            }
        }
    }
}
