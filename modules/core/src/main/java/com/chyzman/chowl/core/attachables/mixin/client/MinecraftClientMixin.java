package com.chyzman.chowl.core.attachables.mixin.client;

import com.chyzman.chowl.core.attachables.impl.AttachableHitResult;
import com.chyzman.chowl.core.attachables.api.client.AttachableRenderDispatcher;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerAttackAttachable;
import com.chyzman.chowl.core.attachables.network.C2S.C2SPlayerInteractAttachable;
import com.chyzman.chowl.core.mixin.client.access.InteractionManagerAccessor;
import com.chyzman.chowl.core.attachables.pond.HitResultDuck;
import com.chyzman.chowl.core.attachables.pond.MinecraftClientDuck;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
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

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientDuck {
    @Unique public AttachableRenderDispatcher attachableRenderDispatcher;

    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Final private ItemModelManager itemModelManager;

    @Shadow @Final private BlockRenderManager blockRenderManager;

    @Shadow @Final private BlockEntityRenderManager blockEntityRenderManager;

    @Shadow @Final private EntityRenderManager entityRenderManager;

    @Shadow @Final public TextRenderer textRenderer;

    @Shadow @Final private ReloadableResourceManagerImpl resourceManager;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Nullable public ClientWorld world;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow
    protected abstract void render(boolean tick);

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Final public GameRenderer gameRenderer;

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderManager;<init>(Lnet/minecraft/client/font/TextRenderer;Ljava/util/function/Supplier;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/client/item/ItemModelManager;Lnet/minecraft/client/render/item/ItemRenderer;Lnet/minecraft/client/render/entity/EntityRenderManager;Lnet/minecraft/client/texture/SpriteHolder;Lnet/minecraft/client/texture/PlayerSkinCache;)V"
        )
    )
    private void createAttachableRenderDispatcher(
        RunArgs args,
        CallbackInfo ci
    ) {
        this.attachableRenderDispatcher = new AttachableRenderDispatcher(
            itemRenderer,
            itemModelManager,
            blockRenderManager,
            blockEntityRenderManager,
            entityRenderManager,
            textRenderer
        );
        resourceManager.registerReloader(this.attachableRenderDispatcher);
    }

    @Override
    public AttachableRenderDispatcher chowl$getAttachableRenderDispatcher() {
        return this.attachableRenderDispatcher;
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    private void interactWithAttachables(
        CallbackInfo ci,
        @Local Hand hand,
        @Local ItemStack itemStack
    ) {
        if (crosshairTarget.getType() != HitResult.Type.MISS) return;
        var targetAttachable = ((HitResultDuck) crosshairTarget).chowl$getHitAttachable();
        if (targetAttachable == null) return;
        var count = itemStack.getCount();

        ((InteractionManagerAccessor) interactionManager).chowl$syncSelectedSlot();
        CHANNEL.clientHandle().send(new C2SPlayerInteractAttachable(
            hand,
            targetAttachable.getUuid(),
            crosshairTarget.getPos()
        ));
        var result = targetAttachable.getContained().onUse(
            world,
            player,
            hand,
            new AttachableHitResult(crosshairTarget.getPos(), targetAttachable)
        );
        if (result instanceof ActionResult.Success success) {
            if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                this.player.swingHand(hand);
                if (!itemStack.isEmpty() && (itemStack.getCount() != count || this.player.isInCreativeMode())) {
                    gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                }
            }
            ci.cancel();
        }
        if (result instanceof ActionResult.Fail) ci.cancel();
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    private void attackAttachables(CallbackInfoReturnable<Boolean> cir) {
        if (crosshairTarget.getType() != HitResult.Type.MISS) return;
        var targetAttachable = ((HitResultDuck) crosshairTarget).chowl$getHitAttachable();
        if (targetAttachable == null) return;
        var stack = player.getStackInHand(Hand.MAIN_HAND);
        var count = stack.getCount();

        CHANNEL.clientHandle().send(new C2SPlayerAttackAttachable(
            targetAttachable.getUuid(),
            crosshairTarget.getPos()
        ));
        var result = targetAttachable.getContained().onAttack(
            world,
            player,
            new AttachableHitResult(crosshairTarget.getPos(), targetAttachable)
        );
        if (result instanceof ActionResult.Success success) {
            if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                this.player.swingHand(Hand.MAIN_HAND);
                if (!stack.isEmpty() && (stack.getCount() != count || this.player.isInCreativeMode())) {
                    gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.MAIN_HAND);
                }
            }
            cir.setReturnValue(true);
            return;
        }
        if (result instanceof ActionResult.Fail) {
            cir.setReturnValue(true);
            return;
        }
    }
}
