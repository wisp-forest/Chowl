package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.attachables.api.Attachable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class AttachableHolder {
    public final Map<UUID, AttachableContainer> attachables = new ConcurrentHashMap<>();
    public final Multimap<ChunkPos, UUID> chunkPosToAttachables = HashMultimap.create();

    public AttachableHolder() {}

    //region ENDEC STUFF

    public static final Endec<AttachableHolder> ENDEC = StructEndecBuilder.of(
            AttachableContainer.ENDEC.listOf().fieldOf("attachables", o -> o.attachables.values().stream().toList()),
            AttachableHolder::new
    );

    private AttachableHolder(List<AttachableContainer> attachables) {
        for (AttachableContainer container : attachables) {
            this.attachables.put(container.uuid, container);
            for (ChunkPos chunkPos : container.chunksOccupied) {
                this.chunkPosToAttachables.put(chunkPos, container.uuid);
            }
        }
    }

    //endregion

    public static AttachmentType<AttachableHolder> TYPE = AttachmentRegistry.create(
            Chowl.id("attachables"),
            builder -> builder
                    .persistent(CodecUtils.toCodec(AttachableHolder.ENDEC))
                    .syncWith(CodecUtils.toPacketCodec(AttachableHolder.ENDEC), AttachmentSyncPredicate.all())
                    .initializer(AttachableHolder::new)
    );

    public AttachableContainer addAttachable(@NotNull Attachable attachable) {
        AttachableContainer container = new AttachableContainer(attachable);

        this.attachables.put(container.uuid, container);

        for (ChunkPos chunkPos : container.chunksOccupied) {
            this.chunkPosToAttachables.put(chunkPos, container.uuid);
        }

        return container;
    }

    public void removeAttachable(@NotNull UUID uuid) {
        AttachableContainer container = this.attachables.remove(uuid);

        for (ChunkPos chunkPos : container.chunksOccupied) {
            this.chunkPosToAttachables.remove(chunkPos, uuid);
        }
    }

    static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
        if (start.equals(end)) {
            return missFactory.apply(context);
        } else {
            double startX = MathHelper.lerp(-1.0E-7, end.x, start.x);
            double startY = MathHelper.lerp(-1.0E-7, end.y, start.y);
            double startZ = MathHelper.lerp(-1.0E-7, end.z, start.z);
            double endX = MathHelper.lerp(-1.0E-7, start.x, end.x);
            double endY = MathHelper.lerp(-1.0E-7, start.y, end.y);
            double endZ = MathHelper.lerp(-1.0E-7, start.z, end.z);
            int flooredX = MathHelper.floor(endX);
            int flooredY = MathHelper.floor(endY);
            int flooredZ = MathHelper.floor(endZ);
            BlockPos.Mutable targetPos = new BlockPos.Mutable(flooredX, flooredY, flooredZ);
            T firstHit = blockHitFactory.apply(context, targetPos);
            if (firstHit != null) {
                return firstHit;
            } else {
                double xDist = startX - endX;
                double yDist = startY - endY;
                double zDist = startZ - endZ;
                //"sign" = 1 if positive -1 if negative, 0 if zero
                int xSign = MathHelper.sign(xDist);
                int ySign = MathHelper.sign(yDist);
                int zSign = MathHelper.sign(zDist);
                double s = xSign == 0 ? Double.MAX_VALUE : xSign / xDist;
                double t = ySign == 0 ? Double.MAX_VALUE : ySign / yDist;
                double u = zSign == 0 ? Double.MAX_VALUE : zSign / zDist;
                double v = s * (xSign > 0 ? 1.0 - MathHelper.fractionalPart(endX) : MathHelper.fractionalPart(endX));
                double w = t * (ySign > 0 ? 1.0 - MathHelper.fractionalPart(endY) : MathHelper.fractionalPart(endY));
                double x = u * (zSign > 0 ? 1.0 - MathHelper.fractionalPart(endZ) : MathHelper.fractionalPart(endZ));

                while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
                    if (v < w) {
                        if (v < x) {
                            flooredX += xSign;
                            v += s;
                        } else {
                            flooredZ += zSign;
                            x += u;
                        }
                    } else if (w < x) {
                        flooredY += ySign;
                        w += t;
                    } else {
                        flooredZ += zSign;
                        x += u;
                    }

                    T secondHit = blockHitFactory.apply(context, targetPos.set(flooredX, flooredY, flooredZ));
                    if (secondHit != null) return secondHit;
                }

                return missFactory.apply(context);
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.attachables);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttachableHolder that)) return false;
        if (!Objects.equals(this.attachables, that.attachables)) return false;
        if (!Objects.equals(this.chunkPosToAttachables, that.chunkPosToAttachables)) return false;
        return true;
    }
}

//TODO: target MinecraftClient.doAttack to modify raycasting

//TODO: do syncing manually instead of using data attachment so we can control it
//TODO: batch rendering
//TODO: somehow map attachables to chunks or positions to avoid rendering in unloaded chunks and to make it possible to query for attachables in a given area
//TODO: string


//TODO: map from chunk to list of attachable ids then list of chunkPos ids in attachables so attachables can be queried by chunk
//TODO: in order to add an attachable you create it, then add it to all chunkposes it occupies and add all chunkposes it occupies to it

//TODO: alternatively store the chunkPos data in the chunks themselves and then have a global list of removed attachables which will then clean up the chunks when they become loaded
