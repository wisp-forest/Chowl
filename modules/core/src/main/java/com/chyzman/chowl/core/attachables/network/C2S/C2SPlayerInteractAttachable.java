package com.chyzman.chowl.core.attachables.network.C2S;

import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record C2SPlayerInteractAttachable(InteractionHand hand, UUID attachableUuid, Vec3 pos) {
}
