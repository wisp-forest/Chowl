package com.chyzman.chowl.core.attachable;

import com.chyzman.chowl.core.registry.ChowlRegistries;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.registry.Registries;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class Attachable {
    private final AttachableType<?> type;
    protected final Random random = Random.create();

    protected UUID uuid = MathHelper.randomUuid(this.random);

    public static final StructEndec<Attachable> ENDEC = Endec.dispatchedStruct(
            attachableType -> attachableType.endec,
            attachable -> attachable.type,
            CodecUtils.toEndec(ChowlRegistries.ATTACHABLE_TYPE.getCodec())
    );

    public Attachable(AttachableType<?> type) {
        this.type = type;
    }

    public AttachableType<?> getType() {
        return this.type;
    }

    public abstract Vec3d getClosestPoint(Vec3d point);

    public abstract boolean collides(Vec3d pos, double margin);

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Name", this::getNameForReport);
    }

    private String getNameForReport() {
        return ChowlRegistries.ATTACHABLE_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName();
    }
}
