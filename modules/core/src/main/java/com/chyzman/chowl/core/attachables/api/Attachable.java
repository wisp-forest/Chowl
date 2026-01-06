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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class Attachable {
    protected static final LoadingCache<Attachable, TranformedVoxelShape> SHAPE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(CacheLoader.from(Attachable::createShape));

    private final AttachableType<?> type;

    public static final StructEndec<Attachable> ENDEC = Endec.dispatchedStruct(
            attachableType -> attachableType.endec,
            attachableState -> attachableState.type,
            CodecUtils.toEndec(ChowlRegistries.ATTACHABLE_TYPE.byNameCodec())
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

    public Vec3 getClosestPointTo(Vec3 point) {
        return getShape().getClosestPointTo(point);
    }

    public abstract boolean isSupported(Level world);

    public abstract Vec3 raycast(Vec3 start, Vec3 end);

    public InteractionResult onUse(Level world, Player player, InteractionHand hand, AttachableHitResult hit) {
        return InteractionResult.PASS;
    }

    public InteractionResult onAttack(Level world, Player player, AttachableHitResult hit) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        var attachableHolder = world.getAttachedOrCreate(AttachableHolder.TYPE);
        attachableHolder.removeAttachable(hit.getContainer().getUuid());

        hit.getAttachable().onBroken(world, hit.getLocation());

        world.setAttached(AttachableHolder.TYPE, attachableHolder);

        return InteractionResult.SUCCESS;
    }

    public abstract void onBroken(Level world, Vec3 pos);

    public static boolean checkForSupport(Attachable attachable, Level world, Vec3 pos, Quaternionf rotation) {
        if (!world.isLoaded(BlockPos.containing(pos))) return true;
        var hit = world.clip(new ClipContext(
                pos,
                pos.add(new Vec3(rotation.transform(new Vector3f(0, 0.01f, 0)))),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
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
        return Block.box(
                minX / scale,
                minY / scale,
                minZ / scale,
                maxX / scale,
                maxY / scale,
                maxZ / scale
        );
    }

    public static Set<ChunkPos> getChunksBetween(Vec3 start, Vec3 end) {
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
