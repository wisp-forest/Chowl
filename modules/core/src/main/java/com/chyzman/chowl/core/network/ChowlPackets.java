package com.chyzman.chowl.core.network;

import com.chyzman.chowl.core.Chowl;
import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.impl.AttachableHolder;
import com.chyzman.chowl.core.attachables.network.AttachablesPackets;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerAttackAttachable;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerInteractAttachable;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ChowlPackets {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Chowl.id("main"));

    public static void registerCommon() {
        CHANNEL.addEndecs(builder -> {

        });

        AttachablesPackets.registerCommon();
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        AttachablesPackets.registerClient();
    }
}
