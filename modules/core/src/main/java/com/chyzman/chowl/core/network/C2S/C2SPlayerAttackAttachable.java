package com.chyzman.chowl.core.network.C2S;

import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record C2SPlayerAttackAttachable(UUID attachableUuid, Vec3d pos) {
}
