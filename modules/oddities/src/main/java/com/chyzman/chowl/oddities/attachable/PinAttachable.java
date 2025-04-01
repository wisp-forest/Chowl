package com.chyzman.chowl.oddities.attachable;

import com.chyzman.chowl.core.attachable.Attachable;
import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class PinAttachable extends Attachable {
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
    public Vec3d getClosestPoint(Vec3d point) {
        return this.pos;
    }

    @Override
    public boolean collides(Vec3d pos, double margin) {
        return this.pos.distanceTo(pos) <= margin;
    }
}
