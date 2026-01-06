package com.chyzman.chowl.core.attachables.network.C2S;

import java.util.UUID;
import net.minecraft.world.phys.Vec3;

public record C2SPlayerAttackAttachable(UUID attachableUuid, Vec3 pos) {
}
