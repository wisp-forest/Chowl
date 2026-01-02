package com.chyzman.chowl.core.attachables.api;

import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import com.chyzman.chowl.core.attachables.impl.TranformedVoxelShape;
import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class Attachable {
    protected static final LoadingCache<Attachable, TranformedVoxelShape> SHAPE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(CacheLoader.from(Attachable::createShape));

    private final AttachableType<?> type;

    public static final StructEndec<Attachable> ENDEC = Endec.dispatchedStruct(
            attachableType -> attachableType.endec,
            attachableState -> attachableState.type,
            CodecUtils.toEndec(ChowlRegistries.ATTACHABLE_TYPE.getCodec())
    );

    public Attachable(AttachableType<?> type) {
        this.type = type;
    }

    public AttachableType<?> getType() {
        return this.type;
    }

    public abstract Set<ChunkPos> getChunksOccupied();

    public TranformedVoxelShape getShape() {
        return SHAPE_CACHE.getUnchecked(this);
    }

    protected abstract TranformedVoxelShape createShape();

    public Vec3d getClosestPointTo(Vec3d point) {
        return getShape().getClosestPointTo(point);
    }

    public abstract boolean isSupported(World world);

    public abstract Vec3d raycast(Vec3d start, Vec3d end);

    public ActionResult onUse(World world, PlayerEntity player, Hand hand, AttachableHitResult hit) {
        return ActionResult.PASS;
    }

    public ActionResult onAttack(World world, PlayerEntity player, AttachableHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);
        attachableHolder.removeAttachable(hit.getContainer().getUuid());

        hit.getAttachable().onBroken(world, hit.getPos());

        world.setAttached(AttachableHolder.TYPE, attachableHolder);

        return ActionResult.SUCCESS;
    }

    public abstract void onBroken(World world, Vec3d pos);

    public static boolean checkForSupport(Attachable attachable, World world, Vec3d pos, Quaternionf rotation) {
        if (!world.isPosLoaded(BlockPos.ofFloored(pos))) return true;
        var hit = world.raycast(new RaycastContext(
                pos,
                pos.add(new Vec3d(rotation.transform(new Vector3f(0, 0.01f, 0)))),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
        ));
        return hit.getType().equals(HitResult.Type.BLOCK) ||
               (((HitResultDuck)hit).chowl$getHitAttachable() != null &&
                !((HitResultDuck)hit).chowl$getHitAttachable().getContained().equals(attachable));
    }

    public static VoxelShape createAttachableCuboid(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ,
            double scale
    ) {
        return Block.createCuboidShape(
                minX / scale,
                minY / scale,
                minZ / scale,
                maxX / scale,
                maxY / scale,
                maxZ / scale
        );
    }

    public static Set<ChunkPos> getChunksBetween(Vec3d start, Vec3d end) {
        var chunks = new HashSet<ChunkPos>();

        var xLength = end.x - start.x;
        var zLength = end.z - start.z;
        var length = Math.sqrt((xLength * xLength) + (zLength * zLength));

        var slope = Math.abs(xLength / zLength);

        for (var i = 0d; i < Math.abs(length); i += slope * 16d) {
            double x = start.x + (xLength * (i / length));
            double z = start.z + (zLength * (i / length));

            int chunkX = (int) (Math.round(x) >> 4);
            int chunkZ = (int) (Math.round(z) >> 4);

            chunks.add(new ChunkPos(chunkX, chunkZ));
        }
        return chunks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Attachable that)) return false;
        if (!Objects.equals(this.type, that.type)) return false;
        return true;
    }
}
