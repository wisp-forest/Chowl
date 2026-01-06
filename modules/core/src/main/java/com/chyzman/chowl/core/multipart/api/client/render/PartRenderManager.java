package com.chyzman.chowl.core.multipart.api.client.render;

import com.chyzman.chowl.core.blockentity.api.MultipartHolderBlockEntity;
import com.chyzman.chowl.core.mixin.client.access.GameRendererAccessor;
import com.chyzman.chowl.core.mixin.client.access.WorldRendererAccessor;
import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.multipart.api.client.render.state.PartRenderState;
import com.chyzman.chowl.core.multipart.pond.MinecraftClientDuck;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public final class PartRenderManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 2x2 chunks size for regions
    public static final int REGION_FROM_CHUNK_SHIFT = 1;
    public static final int REGION_SHIFT = 4 + REGION_FROM_CHUNK_SHIFT;
    public static final int MAX_XZ_IN_REGION = (16 << REGION_FROM_CHUNK_SHIFT) - 1;
    public static final int VIEW_RADIUS = 3;

    private static final Object2ReferenceMap<RenderRegionPos, RegionBuffer> regions = new Object2ReferenceOpenHashMap<>();
    private static final Set<RenderRegionPos> needsRebuild = Sets.newHashSet();
    private static final Multimap<Long, MultipartHolderBlockEntity> blockEntities = HashMultimap.create();
    private static final Multimap<RenderRegionPos, PartRenderState> bakedRenderStates = HashMultimap.create();

    private static class CachedVertexConsumerProvider implements MultiBufferSource {
        private final Reference2ReferenceMap<RenderType, ByteBufferBuilder> allocators = new Reference2ReferenceOpenHashMap<>();
        private final Reference2ReferenceMap<RenderType, BufferBuilder> builders = new Reference2ReferenceOpenHashMap<>();

        @Override
        public VertexConsumer getBuffer(RenderType layer) {
            return builders.computeIfAbsent(layer, ignored1 -> new BufferBuilder(
              allocators.computeIfAbsent(layer, ignored2 -> new ByteBufferBuilder(layer.bufferSize())),
              layer.mode(),
              layer.format())
            );
        }

        /**
         * Resets the provider so another scene can be rendered
         */
        public void reset() {
            allocators.forEach((layer, allocator) -> allocator.discard());
            builders.clear();
        }
    }

    // private static final CachedVertexConsumerProvider vcp = new CachedVertexConsumerProvider();

    private static class RegionBuffer {
        private final Map<RenderType, SectionBuffers> layerBuffers = new Reference2ReferenceOpenHashMap<>();
        private final Set<RenderType> uploadedLayers = new ObjectOpenHashSet<>();

        // FIXME: send help
        public void render(RenderType layer, PoseStack matrices) {
                /*Framebuffer framebuffer;
                if (layer instanceof RenderLayer.MultiPhase) {
                    framebuffer = ((RenderLayerMultiPhaseParametersAccessor) (Object) ((MultiPhaseRenderLayerAccessor) layer).getPhases()).getTarget().get();
                } else {
                    framebuffer = MinecraftClient.getInstance().getFramebuffer();
                }

                RenderPipeline pipeline;
                if (layer instanceof RenderLayer.MultiPhase) {
                    pipeline = ((MultiPhaseRenderLayerAccessor) layer).getPipeline();
                } else {
                    pipeline = RenderPipelines.SOLID;
                }

                layer.startDrawing();
                GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms()
                  .write(
                    matrices.peek().getPositionMatrix(),
                    new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                    RenderSystem.getModelOffset(),
                    RenderSystem.getTextureMatrix(),
                    RenderSystem.getShaderLineWidth()
                  );

                try (RenderPass renderPass = RenderSystem.getDevice()
                  .createCommandEncoder()
                  .createRenderPass(
                    () -> "Glowcase baked BER section layers",
                    framebuffer.getColorAttachmentView(),
                    OptionalInt.empty(),
                    framebuffer.getDepthAttachmentView(),
                    OptionalDouble.empty()
                  )) {
                    Buffers buffers = layerBuffers.get(layer);

                    GpuBuffer indexBuffer;
                    VertexFormat.IndexType indexType;
                    if (buffers.getIndexBuffer() == null) {
                        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(layer.getDrawMode());
                        indexBuffer = shapeIndexBuffer.getIndexBuffer(buffers.getIndexCount());
                        indexType = shapeIndexBuffer.getIndexType();
                    } else {
                        indexBuffer = buffers.getIndexBuffer();
                        indexType = buffers.getIndexType();
                    }

                    ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
                    if (scissorState.method_72091()) {
                        renderPass.enableScissor(scissorState.method_72092(), scissorState.method_72093(), scissorState.method_72094(), scissorState.method_72095());
                    }

                    for (int j = 0; j < 12; j++) {
                        GpuTextureView gpuTextureView3 = RenderSystem.getShaderTexture(j);
                        if (gpuTextureView3 != null) {
                            renderPass.bindSampler("Sampler" + j, gpuTextureView3);
                        }
                    }

                    renderPass.setPipeline(pipeline);
                    renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
                    renderPass.setVertexBuffer(0, buffers.getVertexBuffer());
                    renderPass.setIndexBuffer(indexBuffer, indexType);
                    RenderSystem.bindDefaultUniforms(renderPass);

                    renderPass.drawIndexed(0, 0, buffers.getIndexCount(), 1);
                }
                layer.endDrawing();

                //VertexBuffer buf = layerBuffers.get(layer);
                //buf.bind();
                //layer.startDrawing();
                //buf.draw(matrices.peek().getPositionMatrix(), projectionMatrix, RenderSystem.getShader());
                //layer.endDrawing();
                //VertexBuffer.unbind();*/
        }

        public void upload(RenderType layer, BufferBuilder newBuf) {
            try (MeshData buffer = newBuf.build()) {
                    /*if (buffer == null) return;

                    CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
                    Buffers oldBuffers = this.layerBuffers.get(layer);
                    if (oldBuffers != null) {
                        if (oldBuffers.getVertexBuffer().size() < buffer.getBuffer().remaining()) {
                            oldBuffers.getVertexBuffer().close();
                            oldBuffers.setVertexBuffer(
                              RenderSystem.getDevice()
                                .createBuffer(
                                  () -> "Glowcase Region vertex buffer - layer: " + layer.getName(),
                                  GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                                  buffer.getBuffer()
                                )
                            );
                        } else if (!oldBuffers.getVertexBuffer().isClosed()) {
                            commandEncoder.writeToBuffer(oldBuffers.getVertexBuffer().slice(), buffer.getBuffer());
                        }

                        ByteBuffer byteBuffer = buffer.getSortedBuffer();
                        if (byteBuffer != null) {
                            if (oldBuffers.getIndexBuffer() != null && oldBuffers.getIndexBuffer().size() >= byteBuffer.remaining()) {
                                if (!oldBuffers.getIndexBuffer().isClosed()) {
                                    commandEncoder.writeToBuffer(oldBuffers.getIndexBuffer().slice(), byteBuffer);
                                }
                            } else {
                                if (oldBuffers.getIndexBuffer() != null) {
                                    oldBuffers.getIndexBuffer().close();
                                }

                                oldBuffers.setIndexBuffer(
                                  RenderSystem.getDevice()
                                    .createBuffer(
                                      () -> "Glowcase Region index buffer - layer: " + layer.getName(),
                                      GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST,
                                      byteBuffer
                                    )
                                );
                            }
                        } else if (oldBuffers.getIndexBuffer() != null) {
                            oldBuffers.getIndexBuffer().close();
                            oldBuffers.setIndexBuffer(null);
                        }

                        oldBuffers.setIndexCount(buffer.getDrawParameters().indexCount());
                        oldBuffers.setIndexType(buffer.getDrawParameters().indexType());
                    } else {
                        GpuBuffer vertexBuffer = RenderSystem.getDevice()
                          .createBuffer(
                            () -> "Glowcase Region vertex buffer - layer: " + layer.getName(),
                            GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                            buffer.getBuffer()
                          );
                        ByteBuffer sortedBuffer = buffer.getSortedBuffer();
                        GpuBuffer indexBuffer = sortedBuffer != null
                          ? RenderSystem.getDevice()
                          .createBuffer(
                            () -> "Glowcase Region index buffer - layer: " + layer.getName(),
                            GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST,
                            sortedBuffer
                          )
                          : null;
                        this.layerBuffers.put(layer, new Buffers(vertexBuffer, indexBuffer, buffer.getDrawParameters().indexCount(), buffer.getDrawParameters().indexType()));
                    }

                    this.uploadedLayers.add(layer);*/
            }
        }

        public void reset() {
            layerBuffers.values().forEach(SectionBuffers::close);
            layerBuffers.clear();
            uploadedLayers.clear();
        }
    }

    public static void scheduleBlockEntity(MultipartHolderBlockEntity blockEntity) {
        blockEntities.put(ChunkPos.asLong(blockEntity.getBlockPos()), blockEntity);
    }

    /**
     * Causes the render region containing this Part to be rebuilt -
     * do not call this too frequently as it will affect performance.
     * An invalidation will not immediately cause the next frame to contain an updated view (and call to renderBaked)
     * as all render region rebuilds must call every BER that is to be rendered, otherwise they will be missing from the
     * vertex buffer.
     */
    public static void markForRebuild(BlockPos pos) {
        needsRebuild.add(new RenderRegionPos(pos));
    }

    private static boolean isVisiblePos(RenderRegionPos rrp, Vec3 cam) {
        return Math.abs(rrp.x - ((int) cam.x() >> REGION_SHIFT)) <= VIEW_RADIUS && Math.abs(rrp.z - ((int) cam.z() >> REGION_SHIFT)) <= VIEW_RADIUS;
    }

    public static void extract(WorldExtractionContext context) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("chowl:multipart");

        float tickProgress = context.tickCounter().getGameTimeDeltaPartialTick(false);

        PartRenderDispatcher dispatcher = ((MinecraftClientDuck) Minecraft.getInstance()).chowl$getPartRenderDispatcher();

        Vec3 cameraPos = context.gameRenderer().getMainCamera().position();

        if (!needsRebuild.isEmpty()) {
            profiler.push("rebuild");

            List<Part> parts = new ArrayList<>();
            for (RenderRegionPos renderRegionPos : needsRebuild) {
                if (!isVisiblePos(renderRegionPos, cameraPos)) continue;

                // For the current region, rebuild each render layer using the buffer builders
                // Find all block entities in this region
                for (int chunkX = renderRegionPos.x << REGION_FROM_CHUNK_SHIFT; chunkX < (renderRegionPos.x + 1) << REGION_FROM_CHUNK_SHIFT; chunkX++) {
                    for (int chunkZ = renderRegionPos.z << REGION_FROM_CHUNK_SHIFT; chunkZ < (renderRegionPos.z + 1) << REGION_FROM_CHUNK_SHIFT; chunkZ++) {
                        for (var blockEntity : blockEntities.get(ChunkPos.asLong(chunkX, chunkZ))) {
                            parts.addAll(blockEntity.getParts());
                        }
                    }
                }

                if (parts.isEmpty()) {
                    bakedRenderStates.put(renderRegionPos, null);
                    continue;
                }

                for (Part part : parts) {
                    var renderer = dispatcher.get(part);
                    if (renderer == null) continue;

                    if (renderer.shouldBake(part, tickProgress, cameraPos)) {
                        bakedRenderStates.put(renderRegionPos, dispatcher.getBakedRenderState(part));
                    }
                }

                parts.clear();
            }

            // We've processed all pending rebuilds now
            needsRebuild.clear();

            profiler.pop();
        }

        profiler.pop();
    }

    public static void render(WorldRenderContext wrc) {
        try {
            renderInternal(wrc);
        } catch (Exception e) {
            CrashReport crashReport = CrashReport.forThrowable(e, "Baked Multipart Rendering");
            CrashReportCategory crashReportSection = crashReport.addCategory("Multipart render details");
            crashReportSection.setDetail(
              "Needs Rebuild",
              needsRebuild.size() + " | " +
              Arrays.toString(needsRebuild.stream().map(pos -> "(" + pos.x + "," + pos.z + ")").toList().toArray())
            );
            crashReportSection.setDetail(
              "Regions",
              regions.size() + " | " +
              Arrays.toString(regions.keySet().stream().map(pos -> "(" + pos.x + "," + pos.z + ")").toList().toArray())
            );

            throw new ReportedException(crashReport);
        }
    }

    @SuppressWarnings("unchecked")
    private static void renderInternal(WorldRenderContext context) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("chowl:multipart");

        ClientLevel world = ((WorldRendererAccessor) context.worldRenderer()).getLevel();
        Vec3 cameraPos = context.gameRenderer().getMainCamera().position();

        if (!bakedRenderStates.isEmpty()) {
            profiler.push("rebuild");

            PartRenderDispatcher dispatcher = ((MinecraftClientDuck) Minecraft.getInstance()).chowl$getPartRenderDispatcher();

            // Make builders for regions that are marked for rebuild, render and upload to RegionBuffers
            Set<RenderRegionPos> removing = Sets.newHashSet();
            PoseStack bakeMatrices = new PoseStack();

            for (var entry : bakedRenderStates.asMap().entrySet()) {
                RenderRegionPos renderRegionPos = entry.getKey();
                var renderStates = entry.getValue();

                if (renderStates.isEmpty() || renderStates.contains(null)) {
                    removing.add(renderRegionPos);
                    continue;
                }

                boolean bakedAnything = false;

                for (var renderState : renderStates) {
                    var renderer = dispatcher.getByRenderState(renderState);
                    if (renderer == null) continue;

                    BlockPos pos = renderState.pos;

                    bakeMatrices.pushPose();
                    bakeMatrices.translate(pos.getX() & MAX_XZ_IN_REGION, pos.getY(), pos.getZ() & MAX_XZ_IN_REGION);
                    try {
                        renderer.renderBaked(renderState, bakeMatrices, context.commandQueue());
                        bakedAnything = true;
                    } catch (Throwable t) {
                        LOGGER.error("Block entity renderer threw exception during baking : ", t);
                    }
                    bakeMatrices.popPose();
                }

                if (!bakedAnything) {
                    removing.add(renderRegionPos);
                    continue;
                }

                RegionBuffer buf = regions.computeIfAbsent(renderRegionPos, k -> new RegionBuffer());
                buf.reset();
                // vcp.builders.forEach(buf::upload);
                // vcp.reset();
            }

            // We've processed all pending rebuilds now
            needsRebuild.clear();
            // These regions no longer contain anything
            removing.forEach(rrp -> {
                RegionBuffer buf = regions.get(rrp);
                if (buf != null) {
                    buf.reset();
                    regions.remove(rrp, buf);
                }
            });

            profiler.pop();
        }

        if (!regions.isEmpty()) {
            profiler.push("render");

            /*
             * Set the fog end to an extremely high value, this is a total hack but.
             * It's needed to make fog not bleed into text blocks
             */
            GpuBufferSlice originalFog = RenderSystem.getShaderFog();
            RenderSystem.setShaderFog(((GameRendererAccessor) context.gameRenderer()).getFogRenderer().getBuffer(FogRenderer.FogMode.NONE));
            // Iterate over all RegionBuffers, render visible and remove non-visible RegionBuffers
            PoseStack matrices = context.matrices();
            matrices.pushPose();
            // matrices.multiplyPositionMatrix(context.positionMatrix());
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            var iter = regions.object2ReferenceEntrySet().iterator();
            while (iter.hasNext()) {
                var entry = iter.next();
                RenderRegionPos rrp = entry.getKey();
                RegionBuffer regionBuffer = entry.getValue();
                if (isVisiblePos(entry.getKey(), cameraPos)) {
                    // Iterate over used render layers in the region, render them
                    matrices.pushPose();
                    matrices.translate(rrp.origin.getX(), rrp.origin.getY(), rrp.origin.getZ());
                    for (RenderType l : regionBuffer.uploadedLayers) {
                        regionBuffer.render(l, matrices);
                    }
                    matrices.popPose();
                } else {
                    regionBuffer.reset();
                    iter.remove();
                }
            }
            RenderSystem.setShaderFog(originalFog);
            matrices.popPose();

            profiler.pop();
        }

        // RenderSystem.setShaderColor(1, 1, 1, 1);

        blockEntities.clear();
        profiler.pop();
    }

    public static void activateRegion(BlockPos pos) {
        RenderRegionPos rrp = new RenderRegionPos(pos);
        if (!regions.containsKey(rrp)) {
            markForRebuild(pos);
        }
    }

    public static void reset() {
        regions.values().forEach(RegionBuffer::reset);
        regions.clear();
        needsRebuild.clear();
    }

    private record RenderRegionPos(int x, int z, @NotNull BlockPos origin) {
        public RenderRegionPos(int x, int z) {
            this(x, z, new BlockPos(x << REGION_SHIFT, 0, z << REGION_SHIFT));
        }

        public RenderRegionPos(BlockPos pos) {
            this(pos.getX() >> REGION_SHIFT, pos.getZ() >> REGION_SHIFT);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RenderRegionPos that = (RenderRegionPos) o;
            return x == that.x &&
                   z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }
}
