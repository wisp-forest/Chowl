package com.chyzman.chowl.core.blockentity.api;

import com.chyzman.chowl.core.client.ChowlClient;
import com.chyzman.chowl.core.registry.ChowlComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

public abstract class TemplatableBlockEntity extends BlockEntity {
    protected static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;

    private BlockState templateState = null;

    public TemplatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        templateState = view.read("TemplateState", BlockState.CODEC).orElse(null);

        if (level != null && level.isClientSide()) ChowlClient.reloadPos(level, worldPosition);
    }

    @MustBeInvokedByOverriders
    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        if (templateState != null) view.store("TemplateState", BlockState.CODEC, templateState);
    }

    @Override
    public @Nullable Object getRenderData() {
        return templateState;
    }

    @MustBeInvokedByOverriders
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        if (templateState != null) {
            components.set(ChowlComponents.TEMPLATE_STATE, templateState);
        }
    }

    @MustBeInvokedByOverriders
    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        templateState = components.get(ChowlComponents.TEMPLATE_STATE);
    }

    @MustBeInvokedByOverriders
    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput view) {
        super.removeComponentsFromTag(view);
        view.discard("TemplateState");
    }

    public BlockState templateState() {
        return templateState;
    }

    public void setTemplateState(BlockState templateState) {
        BlockState old = this.templateState;

        this.templateState = templateState;

        if (old != templateState) {
            setChanged();

            if (level.isClientSide()) {
                ChowlClient.reloadPos(level, worldPosition);
            } else {
                if (getBlockState().hasProperty(LIGHT_LEVEL)) {
                    level.setBlockAndUpdate(worldPosition, getBlockState()
                        .setValue(LIGHT_LEVEL, templateState != null ? templateState.getLightEmission() : 0));
                }
            }
        }
    }
}
