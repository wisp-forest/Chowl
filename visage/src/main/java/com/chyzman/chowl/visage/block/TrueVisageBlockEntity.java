package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.core.client.ChowlCoreClient;
import com.chyzman.chowl.core.registry.ChowlCoreComponents;
import com.chyzman.chowl.visage.registry.VisageBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TrueVisageBlockEntity extends VisageBlockEntity {
    BlockState templateModel = null;

    public TrueVisageBlockEntity(BlockPos pos, BlockState state) {
        super(VisageBlocks.Entities.TRUE_VISAGE_BLOCK, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("TemplateModel", NbtElement.COMPOUND_TYPE)) {
            templateModel = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("TemplateModel"));
        } else {
            templateModel = null;
        }

        if (world != null && world.isClient) {
            ChowlCoreClient.reloadPos(world, pos);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (templateModel != null)
            nbt.put("TemplateModel", NbtHelper.fromBlockState(templateModel));
        else
            nbt.putString("TemplateModel", "me when mojang code");
    }

    @Override
    public @Nullable Object getRenderData() {
        return new TrueVisageBlockModel.TrueVisageTemplate(templateModel, templateState());
    }

    @Override
    protected void addComponents(ComponentMap.Builder components) {
        super.addComponents(components);

        if (templateModel != null) {
            components.add(ChowlCoreComponents.TEMPLATE_MODEL_STATE, templateModel);
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);

        templateModel = components.get(ChowlCoreComponents.TEMPLATE_MODEL_STATE);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);

        nbt.remove("TemplateModel");
    }

    public BlockState templateModel() {
        return templateModel;
    }

    public void setTemplateModel(BlockState templateState) {
        BlockState old = this.templateModel;

        this.templateModel = templateState;

        if (old != templateState) {
            markDirty();

            if (world.isClient) {
                ChowlCoreClient.reloadPos(world, pos);
            } else {
                if (getCachedState().contains(LIGHT_LEVEL)) {
                    world.setBlockState(pos, getCachedState()
                            .with(LIGHT_LEVEL, templateState != null ? templateState.getLuminance() : 0));
                }
            }
        }
    }
}
