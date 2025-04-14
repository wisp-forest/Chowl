package com.chyzman.chowl.core.attachables.network.C2S;

import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record C2SPlayerInteractAttachable(Hand hand, UUID attachableUuid, Vec3d pos) {
}
