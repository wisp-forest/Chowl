package com.chyzman.chowl.item.model;

import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.client.RenderGlobals;
import com.chyzman.chowl.client.RetextureQuadTransform;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlankPanelItemModel extends ForwardingBakedModel {
    private BlankPanelItemModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        DrawerFrameBlockEntity drawerFrame = RenderGlobals.DRAWER_FRAME.get();
        if (drawerFrame == null || drawerFrame.templateState == null) {
            super.emitItemQuads(stack, randomSupplier, context);
            return;
        }

        var transform = RetextureQuadTransform.get(drawerFrame.templateState);
        try (var ignored = transform.withRotation(direction -> {
            //todo: fix this
            if (RenderGlobals.FRAME_SIDE.get() == null) return direction;

            return switch (RenderGlobals.FRAME_SIDE.get()) {
                case DOWN -> direction.rotateCounterclockwise(Direction.Axis.X);
                case UP -> direction.rotateClockwise(Direction.Axis.X);
                case NORTH -> direction.getOpposite();
                case SOUTH -> direction;
                case WEST -> direction.rotateClockwise(Direction.Axis.Y);
                case EAST -> direction.rotateCounterclockwise(Direction.Axis.Y);
            };
        })) {
            context.pushTransform(transform);
            super.emitItemQuads(stack, randomSupplier, context);
            context.popTransform();
        }
    }

    public record Unbaked(Identifier baseModel) implements UnbakedModel {
        @Override
        public Collection<Identifier> getModelDependencies() {
            return List.of(baseModel);
        }

        @Override
        public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
            // wtf does this method do
            modelLoader.apply(baseModel).setParents(modelLoader);
        }

        @Override
        public @NotNull BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
            return new BlankPanelItemModel(baker.bake(baseModel, rotationContainer));
        }
    }
}