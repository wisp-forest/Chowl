package com.chyzman.chowl.industries.util;

import com.chyzman.chowl.industries.classes.AABBConstructingVertexConsumerProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.TimeUnit;

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
                ModelTransformationMode.FIXED,
                false,
                matrices,
                provider,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
                OverlayTexture.DEFAULT_UV,
                client.getItemRenderer().getModels().getModel(stack)
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

}