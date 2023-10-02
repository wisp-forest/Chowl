// Mostly yoinked from https://github.com/quat1024/templates-mod/blob/master/src/main/java/io/github/cottonmc/templates/model/RetexturingBakedModel.java,
// which is under MIT (https://github.com/quat1024/templates-mod/blob/master/LICENSE)

package com.chyzman.chowl.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class DrawerFrameBlockModel extends ForwardingBakedModel {
    public DrawerFrameBlockModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        var template = (BlockState)((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);

        if (template != null) context.pushTransform(RetextureTransform.CACHE.getUnchecked(template));
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        if (template != null) context.popTransform();
    }

    public record Unbaked(Identifier baseModel) implements UnbakedModel {
        @Override
        public Collection<Identifier> getModelDependencies() {
            return List.of(baseModel);
        }

        @Override
        public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
            // wtf does this method do
        }

        @Override
        public @NotNull BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
            return new DrawerFrameBlockModel(baker.bake(baseModel, rotationContainer));
        }
    }

    private static class RetextureTransform implements RenderContext.QuadTransform {
        private static final LoadingCache<BlockState, RetextureTransform> CACHE = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build(CacheLoader.from(RetextureTransform::new));

        static {
            // TODO: reload cache on resource pack reload
        }

        private final DirectionInfo[] directions = new DirectionInfo[6];

        private RetextureTransform(BlockState template) {
            BakedModel templateModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(template);

            for (int dirId = 0; dirId < 6; dirId++) {
                Direction dir = Direction.byId(dirId);

                var quads = templateModel.getQuads(template, dir, Random.create());
                if (quads.size() == 0) continue;

                var quad = quads.get(0);

                directions[dirId] = new DirectionInfo(quad.getSprite(), quad.hasColor(), quad.getColorIndex());
            }
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            Direction face = quad.nominalFace();
            if (face == null) return true;
            if (directions[face.getId()] == null) return true;

            quad.spriteBake(directions[face.getId()].sprite, MutableQuadView.BAKE_LOCK_UV);

            // todo: handle color providers.

            return true;
        }

        record DirectionInfo(Sprite sprite, boolean hasColor, int colorIdx) {}
    }
}
