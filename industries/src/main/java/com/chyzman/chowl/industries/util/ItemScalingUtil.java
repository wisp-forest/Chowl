package com.chyzman.chowl.industries.util;

import com.chyzman.chowl.industries.classes.AABBConstructingVertexConsumerProvider;
import com.chyzman.chowl.industries.client.RenderGlobals;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.chyzman.chowl.industries.Chowl.CHOWL_CONFIG;

@Environment(EnvType.CLIENT)
public final class ItemScalingUtil {
    private static final ThreadLocal<MatrixStack> MATRICES = ThreadLocal.withInitial(MatrixStack::new);
    private static final LoadingCache<ItemStack, ItemModelProperties> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(CacheLoader.from(stack -> {
                MatrixStack matrices = MATRICES.get();
                MinecraftClient client = MinecraftClient.getInstance();

                matrices.push();
                matrices.loadIdentity();

                var provider = new AABBConstructingVertexConsumerProvider();

                client.getItemRenderer().renderItem(
                        stack,
                        CHOWL_CONFIG.flatten_contents() ? ModelTransformationMode.GUI : ModelTransformationMode.FIXED,
                        false,
                        matrices,
                        provider,
                        LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        OverlayTexture.DEFAULT_UV,
                        client.getItemRenderer().getModel(stack, client.world, null, 0)
                );

                matrices.pop();

                return new ItemModelProperties(
                        new Vec3d(
                                provider.minX,
                                provider.minY,
                                provider.minZ
                        ),
                        new Vec3d(
                                provider.maxX,
                                provider.maxY,
                                provider.maxY
                        )
                );
            }));


    public static ItemModelProperties getItemModelProperties(ItemStack stack) {
        return CACHE.getUnchecked(stack);
    }

    public record ItemModelProperties(Vec3d size, Vec3d offset, Vec3d min, Vec3d max) {
        public ItemModelProperties(Vec3d min, Vec3d max) {
            this(
                    new Vec3d(
                            max.x - min.x,
                            max.y - min.y,
                            max.z - min.z
                    ),
                    new Vec3d(
                            (max.x + min.x) / 2,
                            (max.y + min.y) / 2,
                            (max.z + min.z) / 2
                    ),
                    min,
                    max
            );
        }
    }

    public static void renderScaledItem(
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            Consumer<MatrixStack> modifications
    ) {
        var client = MinecraftClient.getInstance();
        var renderer = client.getItemRenderer();
        var properties = getItemModelProperties(stack);
        var flatten = CHOWL_CONFIG.flatten_contents();
        var model = renderer.getModel(stack, client.world, null, 0);
        float scale = (float) Math.min(2, (1 / (Math.max(properties.size().x, Math.max(properties.size().y, properties.size().z)))));
        matrices.push();
        matrices.scale(scale, scale, scale);
        scale = (12 / 16f);
        matrices.scale(scale, scale, scale);
        modifications.accept(matrices);
        if (flatten) matrices.scale(4/3f, 4/3f, 4/3f);
        matrices.translate(-properties.offset().x, -properties.offset().y, Math.abs(properties.offset().z) > 0.5 ? -properties.offset().z : 0);
        if (flatten) {
            matrices.scale(1, 1, 1 / 512f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
//            if (!model.isSideLit()) DiffuseLighting.disableGuiDepthLighting();
        }
        renderer.renderItem(
                stack,
                flatten ? ModelTransformationMode.GUI : ModelTransformationMode.FIXED,
                false,
                matrices,
                vertexConsumers,
                light,
                overlay,
                model
        );
//        if (flatten && !model.isSideLit()) DiffuseLighting.enableGuiDepthLighting();
        matrices.pop();
    }

    public static void renderScaledItem(
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        renderScaledItem(stack, matrices, vertexConsumers, light, overlay, m -> {});
    }
}
