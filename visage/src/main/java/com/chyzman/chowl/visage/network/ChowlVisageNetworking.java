package com.chyzman.chowl.visage.network;

import com.chyzman.chowl.visage.block.VisageBlockEntity;
import com.chyzman.chowl.visage.block.VisageBlockTemplate;
import com.chyzman.chowl.visage.item.VisageBlockItem;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;

import static com.chyzman.chowl.core.network.ChowlCoreNetworking.CHANNEL;
import static com.chyzman.chowl.core.registry.ChowlCoreComponents.TEMPLATE_STATE;

public class ChowlVisageNetworking {

    public static void init() {
        CHANNEL.addEndecs(reflectiveEndecBuilder -> {
            reflectiveEndecBuilder.register(CodecUtils.toEndec(BlockState.CODEC), BlockState.class);
        });

        CHANNEL.registerServerbound(PickBlockWithVisageC2SPacket.class, (message, access) -> {
            var player = access.player();
            var world = player.getServerWorld();
            var state = world.getBlockState(message.pos());
            var stack = player.getStackInHand(player.getActiveHand());

            if (stack.getItem() instanceof VisageBlockItem && state != null) {
                if (state.isAir()) return;
                if (state.getBlock() instanceof VisageBlockTemplate) {
                    if (world.getBlockEntity(message.pos()) instanceof VisageBlockEntity visageBlockEntity) {
                        var templateState = visageBlockEntity.templateState();
                        if (templateState != null) {
                            state = templateState;
                        } else {
                            stack.remove(TEMPLATE_STATE);
                            return;
                        }
                    } else {
                        return;
                    }
                }
                stack.set(TEMPLATE_STATE, state);
            }
        });
    }
}
