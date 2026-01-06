package com.chyzman.chowl.core.attachables.impl;

import com.chyzman.chowl.core.attachables.api.Attachable;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AttachableHitResult extends HitResult {
    private final AttachableContainer container;
    private final Attachable attachable;

    public AttachableHitResult(Vec3 pos, AttachableContainer container) {
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
