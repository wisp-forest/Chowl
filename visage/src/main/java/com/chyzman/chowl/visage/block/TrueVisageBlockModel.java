// Mostly yoinked from https://github.com/quat1024/templates-mod/blob/master/src/main/java/io/github/cottonmc/templates/model/RetexturingBakedModel.java,
// which is under MIT (https://github.com/quat1024/templates-mod/blob/master/LICENSE)

package com.chyzman.chowl.visage.block;

import com.chyzman.chowl.core.client.RetextureInfo;
import com.chyzman.chowl.core.registry.ChowlCoreComponents;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TrueVisageBlockModel extends ForwardingBakedModel {

    private TrueVisageBlockModel(BakedModel base) {
        this.wrapped = base;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(
            BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context
    ) {
        var template = (TrueVisageTemplate) blockView.getBlockEntityRenderData(pos);

        if (template != null && (template.model() != null || template.texture() != null)) {
            var model = template.model() != null ? MinecraftClient.getInstance().getBlockRenderManager().getModel(template.model()) : wrapped;

            var info = RetextureInfo.get(template.texture() != null ? template.texture() : state);
            context.pushTransform(new RetextureTransform(info, blockView, pos));
            try {
                model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            } catch (Exception ignored) {
            } finally {
                context.popTransform();
            }
        } else {
            super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        BlockState templateModel = stack.get(ChowlCoreComponents.TEMPLATE_MODEL_STATE);
        BlockState templateTexture = stack.get(ChowlCoreComponents.TEMPLATE_STATE);

        if (templateModel != null || templateTexture != null) {
            var model = templateModel != null ? MinecraftClient.getInstance().getBlockRenderManager().getModel(templateModel) : wrapped;

            if (templateTexture != null) {
                var info = RetextureInfo.get(templateTexture);
                context.pushTransform(new RetextureTransform(info, null, null));
            }
            model.emitItemQuads(stack, randomSupplier, context);
            if (templateTexture != null) context.popTransform();
        } else {
            super.emitItemQuads(stack, randomSupplier, context);
        }
    }

    private record RetextureTransform(RetextureInfo info, @Nullable BlockRenderView world, @Nullable BlockPos pos) implements RenderContext.QuadTransform {
        @Override
        public boolean transform(MutableQuadView quad) {
            Direction face = quad.nominalFace();
            if (face == null) return true;

            if (!info.changeSprite(quad, face)) return false;

            if (world != null && pos != null) info.changeColor(quad, face, world, pos);

            return true;
        }
    }

    public record Unbaked(Identifier baseModel) implements UnbakedModel {
        @Override
        public Collection<Identifier> getModelDependencies() {
            return new ArrayList<>(List.of(baseModel));
        }

        @Override
        public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
            // wtf does this method do
        }

        @Override
        public @NotNull BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
            return new TrueVisageBlockModel(baker.bake(baseModel, rotationContainer));
        }
    }

    public record TrueVisageTemplate(@Nullable BlockState model, @Nullable BlockState texture) {}

}
