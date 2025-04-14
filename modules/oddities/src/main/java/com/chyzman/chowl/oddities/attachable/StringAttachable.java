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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Set;

public class StringAttachable extends Attachable {

    private Vec3d startPos = Vec3d.ZERO;
    private Quaternionf startPosRotation = new Quaternionf();

    private Vec3d endPos = Vec3d.ZERO;
    private Quaternionf endPosRotation = new Quaternionf();

    public StringAttachable() {
        super(OdditiesAttachables.STRING);
    }

    public StringAttachable(
            Vec3d startPos,
            Quaternionf startPosRotation,
            Vec3d endPos,
            Quaternionf endPosRotation
    ) {
        this();
        this.startPos = startPos;
        this.startPosRotation = startPosRotation;
        this.endPos = endPos;
        this.endPosRotation = endPosRotation;
    }

    public static final StructEndec<StringAttachable> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.VEC3D.fieldOf("pos", s -> s.startPos),
            CodecUtils.toEndec(Codecs.QUATERNION_F, PacketCodecs.QUATERNION_F)
                    .fieldOf("rotation", s -> s.startPosRotation),
            MinecraftEndecs.VEC3D.fieldOf("endPos", s -> s.endPos),
            CodecUtils.toEndec(Codecs.QUATERNION_F, PacketCodecs.QUATERNION_F)
                    .fieldOf("endRotation", s -> s.endPosRotation),
            StringAttachable::new
    );

    //region GETTERS AND SETTERS

    public Vec3d pos() {
        return this.startPos;
    }

    public StringAttachable pos(Vec3d pos) {
        this.startPos = pos;
        return this;
    }

    public Quaternionf rotation() {
        return this.startPosRotation;
    }

    public StringAttachable rotation(Quaternionf rotation) {
        this.startPosRotation = rotation;
        return this;
    }

    public Vec3d endPos() {
        return this.endPos;
    }

    public StringAttachable endPos(Vec3d endPos) {
        this.endPos = endPos;
        return this;
    }

    public Quaternionf endRotation() {
        return this.endPosRotation;
    }

    public StringAttachable endRotation(Quaternionf endRotation) {
        this.endPosRotation = endRotation;
        return this;
    }

    //endregion

    @Override
    public Set<ChunkPos> getChunksOccupied() {
        var offsetPos = startPos.add(new Vec3d(startPosRotation.transform(new Vector3f(0, 0.01f, 0))));
        var offsetEndPos = endPos.add(new Vec3d(endPosRotation.transform(new Vector3f(0, 0.01f, 0))));
        var chunks = getChunksBetween(startPos, endPos);
        chunks.add(new ChunkPos((int) (Math.round(offsetPos.x) >> 4), (int) Math.round(offsetPos.z) >> 4));
        chunks.add(new ChunkPos((int) (Math.round(offsetEndPos.x) >> 4), (int) Math.round(offsetEndPos.z) >> 4));
        return chunks;
    }

    @Override
    protected TranformedVoxelShape createShape() {
        return new TranformedVoxelShape(
                Attachable.createAttachableCuboid(
                        -0.25, 0, -0.25,
                        0.25, startPos.distanceTo(endPos) * 16f, 0.25,
                        1
                ),
                getRotation(this.startPos, this.endPos)
        );
    }

    @Override
    public boolean isSupported(World world) {
        return Attachable.checkForSupport(this, world, this.startPos, this.startPosRotation) &&
               Attachable.checkForSupport(this, world, this.endPos, this.endPosRotation);
    }

    @Override
    public @Nullable Vec3d raycast(Vec3d start, Vec3d end) {
        return this.getShape().raycast(start, end, this.startPos);
    }

    @Override
    public void onBroken(World world, Vec3d pos) {
        ItemScatterer.spawn(
                world,
                pos.x,
                pos.y,
                pos.z,
                OdditiesItems.STRING.getDefaultStack()
        );
    }

    public static Quaternionf getRotation(Vec3d start, Vec3d end) {
        Vec3d direction = end.subtract(start).normalize();
        Vector3f forward = new Vector3f(0, 0, 1);

        Quaternionf quaternion = new Quaternionf();
        quaternion.rotationTo(forward, direction.toVector3f());
        quaternion.rotateX((float) Math.toRadians(90));

        return quaternion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.startPos,
                this.startPosRotation,
                this.endPos,
                this.endPosRotation
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringAttachable that)) return false;
        if (!Objects.equals(this.startPos, that.startPos)) return false;
        if (!Objects.equals(this.startPosRotation, that.startPosRotation)) return false;
        if (!Objects.equals(this.endPos, that.endPos)) return false;
        if (!Objects.equals(this.endPosRotation, that.endPosRotation)) return false;
        return super.equals(obj);
    }
}
