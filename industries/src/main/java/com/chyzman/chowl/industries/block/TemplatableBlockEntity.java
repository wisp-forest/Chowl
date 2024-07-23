package com.chyzman.chowl.industries.block;

import com.chyzman.chowl.industries.client.ChowlClient;
import com.chyzman.chowl.industries.registry.ChowlComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

public abstract class TemplatableBlockEntity extends BlockEntity {
    private BlockState templateState = null;

    public TemplatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("TemplateState", NbtElement.COMPOUND_TYPE)) {
            templateState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("TemplateState"));
        } else {
            templateState = null;
        }

        if (world != null && world.isClient) {
            ChowlClient.reloadPos(world, pos);
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (templateState != null)
            nbt.put("TemplateState", NbtHelper.fromBlockState(templateState));
        else
            nbt.putString("TemplateState", "me when mojang code");
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
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        nbt.remove("TemplateState");
    }

    public BlockState templateState() {
        return templateState;
    }

    public void setTemplateState(BlockState templateState) {
        BlockState old = this.templateState;

        this.templateState = templateState;

        if (old != templateState) {
            markDirty();

            if (world.isClient) {
                ChowlClient.reloadPos(world, pos);
            } else {
                if (getCachedState().contains(DrawerFrameBlock.LIGHT_LEVEL)) {
                    world.setBlockState(pos, getCachedState()
                        .with(DrawerFrameBlock.LIGHT_LEVEL, templateState != null ? templateState.getLuminance() : 0));
                }
            }
        }
    }
}
