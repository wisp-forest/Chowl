package com.chyzman.chowl.core.attachable;

import com.chyzman.chowl.core.Chowl;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class AttachableHolder {
    public final Map<UUID,Attachable> attachables = new HashMap<>();

    public AttachableHolder() {}

    public AttachableHolder(Map<UUID, Attachable> attachables) {
        this.attachables.putAll(attachables);
    }

    public static final Endec<AttachableHolder> ENDEC = StructEndecBuilder.of(
            Endec.map(BuiltInEndecs.UUID, Attachable.ENDEC).fieldOf("attachables", o -> o.attachables),
            AttachableHolder::new
    );

    public static AttachmentType<AttachableHolder> TYPE = AttachmentRegistry.create(
            Chowl.id("attachables"),
            builder -> builder
                    .persistent(CodecUtils.toCodec(AttachableHolder.ENDEC))
                    .syncWith(CodecUtils.toPacketCodec(AttachableHolder.ENDEC), AttachmentSyncPredicate.all())
                    .initializer(AttachableHolder::new)
    );



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
            BlockPos.Mutable mutable = new BlockPos.Mutable(flooredX, flooredY, flooredZ);
            T hitResult = blockHitFactory.apply(context, mutable);
            if (hitResult != null) {
                return hitResult;
            } else {
                double xDist = startX - endX;
                double yDist = startY - endY;
                double zDist = startZ - endZ;
                //sign = 1 if positive -1 if negative, 0 if zero
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

                    T object2 = (T)blockHitFactory.apply(context, mutable.set(flooredX, flooredY, flooredZ));
                    if (object2 != null) {
                        return object2;
                    }
                }

                return (T)missFactory.apply(context);
            }
        }
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
