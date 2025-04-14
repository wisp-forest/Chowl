package com.chyzman.chowl.oddities.attachable;

import com.chyzman.chowl.core.attachables.api.Attachable;
import com.chyzman.chowl.core.attachables.impl.TranformedVoxelShape;
import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import com.chyzman.chowl.oddities.registry.OdditiesItems;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PinAttachable extends Attachable {
    private static final VoxelShape SHAPE = VoxelShapes.union(
                    Attachable.createAttachableCuboid(
                            -1.5, -11, -1.5,
                            1.5, -6, 1.5,
                            6
                    ),
                    Attachable.createAttachableCuboid(
                            -2.5, -6, -2.5,
                            2.5, -5, 2.5,
                            6
                    ),
                    Attachable.createAttachableCuboid(
                            -0.5, -5, -0.5,
                            0.5, 2, 0.5,
                            6
                    )
            )
            .offset(0, -1 / 15f / 16f, 0);

    private Vec3d pos = Vec3d.ZERO;
    private Quaternionf rotation = new Quaternionf();

    public PinAttachable() {
        super(OdditiesAttachables.PIN);
    }

    public PinAttachable(Vec3d pos, Quaternionf rotation) {
        this();
        this.pos = pos;
        this.rotation = rotation;
    }

    public static final StructEndec<PinAttachable> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.VEC3D.fieldOf("pos", s -> s.pos),
            CodecUtils.toEndec(Codecs.QUATERNION_F, PacketCodecs.QUATERNION_F)
                    .fieldOf("rotation", s -> s.rotation),
            PinAttachable::new
    );

    //region GETTERS AND SETTERS

    public Vec3d pos() {
        return this.pos;
    }

    public PinAttachable pos(Vec3d pos) {
        this.pos = pos;
        return this;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    public PinAttachable rotation(Quaternionf rotation) {
        this.rotation = rotation;
        return this;
    }

    //endregion

    @Override
    public Set<ChunkPos> getChunksOccupied() {
        var offsetPos = pos.add(new Vec3d(rotation.transform(new Vector3f(0, 0.01f, 0))));
        return new HashSet<>(List.of(
                new ChunkPos((int) (Math.round(this.pos.x) >> 4), (int) Math.round(this.pos.z) >> 4),
                new ChunkPos((int) (Math.round(offsetPos.x) >> 4), (int) Math.round(offsetPos.z) >> 4)
        ));
    }

    @Override
    protected TranformedVoxelShape createShape() {
        return new TranformedVoxelShape(SHAPE, new Quaternionf(this.rotation));
    }

    @Override
    public boolean isSupported(World world) {
        return Attachable.checkForSupport(this, world, this.pos, this.rotation);
    }

    @Override
    public @Nullable Vec3d raycast(Vec3d start, Vec3d end) {
        return this.getShape().raycast(start, end, this.pos);
    }

    @Override
    public void onBroken(World world, Vec3d pos) {
        ItemScatterer.spawn(
                world,
                pos.x,
                pos.y,
                pos.z,
                OdditiesItems.PIN.getDefaultStack()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pos, this.rotation);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PinAttachable that)) return false;
        if (!Objects.equals(this.pos, that.pos)) return false;
        if (!Objects.equals(this.rotation, that.rotation)) return false;
        return super.equals(obj);
    }
}
