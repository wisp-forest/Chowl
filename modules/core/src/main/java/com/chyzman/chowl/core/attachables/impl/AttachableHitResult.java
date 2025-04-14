package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class AttachableHitResult extends HitResult {
    private final AttachableContainer container;
    private final Attachable attachable;

    public AttachableHitResult(Vec3d pos, AttachableContainer container) {
        super(pos);
        this.container = container;
        this.attachable = container.getContained();
    }

    public AttachableContainer getContainer() {
        return this.container;
    }

    public Attachable getAttachable() {
        return this.attachable;
    }

    @Override
    public Type getType() {
        return Type.MISS;
    }
}
