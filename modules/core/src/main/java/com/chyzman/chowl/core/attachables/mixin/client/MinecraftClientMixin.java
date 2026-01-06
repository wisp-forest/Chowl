package com.chyzman.chowl.core.attachables.mixin.client;

import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.api.client.AttachableRenderDispatcher;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerAttackAttachable;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerInteractAttachable;
import com.chyzman.chowl.core.mixin.client.access.InteractionManagerAccessor;
import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.chyzman.chowl.core.network.ChowlPackets.CHANNEL;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique public AttachableRenderDispatcher attachableRenderDispatcher;

    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Final private ItemModelResolver itemModelResolver;

    @Shadow @Final private BlockRenderDispatcher blockRenderer;

    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow @Final public Font font;

    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Nullable public ClientLevel level;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow
    protected abstract void runTick(boolean tick);

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Shadow @Final public GameRenderer gameRenderer;

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;<init>(Lnet/minecraft/client/gui/Font;Ljava/util/function/Supplier;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/client/renderer/entity/ItemRenderer;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;)V"
        )
    )
    private void createAttachableRenderDispatcher(
        GameConfig args,
        CallbackInfo ci
    ) {
        this.attachableRenderDispatcher = new AttachableRenderDispatcher(
            itemRenderer,
            itemModelResolver,
            blockRenderer,
            blockEntityRenderDispatcher,
            entityRenderDispatcher,
            font
        );
        resourceManager.registerReloadListener(this.attachableRenderDispatcher);
    }

    @Override
    public AttachableRenderDispatcher chowl$getAttachableRenderDispatcher() {
        return this.attachableRenderDispatcher;
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void interactWithAttachables(
        CallbackInfo ci,
        @Local InteractionHand hand,
        @Local ItemStack itemStack
    ) {
        if (hitResult.getType() != HitResult.Type.MISS) return;
        var targetAttachable = ((HitResultDuck) hitResult).chowl$getHitAttachable();
        if (targetAttachable == null) return;
        var count = itemStack.getCount();

        ((InteractionManagerAccessor) gameMode).chowl$syncSelectedSlot();
        CHANNEL.clientHandle().send(new C2SPlayerInteractAttachable(
            hand,
            targetAttachable.getUuid(),
            hitResult.getLocation()
        ));
        var result = targetAttachable.getContained().onUse(
            level,
            player,
            hand,
            new AttachableHitResult(hitResult.getLocation(), targetAttachable)
        );
        if (result instanceof InteractionResult.Success success) {
            if (success.swingSource() == InteractionResult.SwingSource.CLIENT) {
                this.player.swing(hand);
                if (!itemStack.isEmpty() && (itemStack.getCount() != count || this.player.hasInfiniteMaterials())) {
                    gameRenderer.itemInHandRenderer.itemUsed(hand);
                }
            }
            ci.cancel();
        }
        if (result instanceof InteractionResult.Fail) ci.cancel();
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void attackAttachables(CallbackInfoReturnable<Boolean> cir) {
        if (hitResult.getType() != HitResult.Type.MISS) return;
        var targetAttachable = ((HitResultDuck) hitResult).chowl$getHitAttachable();
        if (targetAttachable == null) return;
        var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        var count = stack.getCount();

        CHANNEL.clientHandle().send(new C2SPlayerAttackAttachable(
            targetAttachable.getUuid(),
            hitResult.getLocation()
        ));
        var result = targetAttachable.getContained().onAttack(
            level,
            player,
            new AttachableHitResult(hitResult.getLocation(), targetAttachable)
        );
        if (result instanceof InteractionResult.Success success) {
            if (success.swingSource() == InteractionResult.SwingSource.CLIENT) {
                this.player.swing(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty() && (stack.getCount() != count || this.player.hasInfiniteMaterials())) {
                    gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
                }
            }
            cir.setReturnValue(true);
            return;
        }
        if (result instanceof InteractionResult.Fail) {
            cir.setReturnValue(true);
            return;
        }
    }
}
