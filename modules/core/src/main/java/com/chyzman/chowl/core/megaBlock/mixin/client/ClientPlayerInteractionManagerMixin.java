package com.chyzman.chowl.core.megaBlock.mixin.client;

import com.chyzman.chowl.core.megaBlock.api.MegaBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private BlockPos offsetAttackPos(BlockPos pos) {
        if (client.world == null) return pos;
        var state = client.world.getBlockState(pos);
        if (!(state.getBlock() instanceof MegaBlock block)) return pos;
        return pos.subtract(block.localPos(state));
    }
}
