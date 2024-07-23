package com.chyzman.chowl.industries.registry;

import com.chyzman.chowl.core.ext.AttackInteractionReceiver;
import com.chyzman.chowl.core.network.AttackInteractionC2SPacket;
import com.chyzman.chowl.industries.graph.DestroyGraphPacket;
import com.chyzman.chowl.industries.graph.SyncGraphPacket;
import com.chyzman.chowl.industries.item.component.DisplayingPanelConfig;
import eu.pb4.common.protection.api.CommonProtection;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static com.chyzman.chowl.industries.Chowl.CHANNEL;

public class ServerBoundPackets {
    public static void init() {
        CHANNEL.registerClientboundDeferred(SyncGraphPacket.class);
        CHANNEL.registerClientboundDeferred(DestroyGraphPacket.class);
    }

    public static void addEndecs(ReflectiveEndecBuilder builder) {
        builder.register(DisplayingPanelConfig.ENDEC, DisplayingPanelConfig.class);
        builder.register(Endec.VAR_INT.xmap(Block.STATE_IDS::get, Block.STATE_IDS::getRawId), BlockState.class);
    }
}