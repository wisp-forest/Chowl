package com.chyzman.chowl.oddities.attachable;

import com.chyzman.chowl.core.attachable.Attachable;
import com.chyzman.chowl.core.attachable.AttachableType;
import com.chyzman.chowl.core.registry.ChowlRegistries;
import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import java.util.List;

public class StickyNoteAttachable extends Attachable {
    private Vec3d pos = Vec3d.ZERO;
    private Quaternionf rotation = new Quaternionf();
    private Text text = Text.empty();

    public StickyNoteAttachable() {
        super(OdditiesAttachables.STICKY_NOTE);
    }

    public StickyNoteAttachable(Vec3d pos, Quaternionf rotation, Text text) {
        this();
        this.pos = pos;
        this.rotation = rotation;
        this.text = text;
    }

    public static final StructEndec<StickyNoteAttachable> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.VEC3D.fieldOf("pos", s -> s.pos),
            CodecUtils.toEndec(Codecs.QUATERNION_F, PacketCodecs.QUATERNION_F)
                    .fieldOf("rotation", s -> s.rotation),
            MinecraftEndecs.TEXT.fieldOf("text", s -> s.text),
            StickyNoteAttachable::new
    );

    //region GETTERS AND SETTERS

    public Vec3d pos() {
        return this.pos;
    }

    public StickyNoteAttachable pos(Vec3d pos) {
        this.pos = pos;
        return this;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    public StickyNoteAttachable rotation(Quaternionf rotation) {
        this.rotation = rotation;
        return this;
    }

    public Text text() {
        return this.text;
    }

    public StickyNoteAttachable text(Text text) {
        this.text = text;
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
