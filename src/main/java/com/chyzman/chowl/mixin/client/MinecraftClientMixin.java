package com.chyzman.chowl.mixin.client;

import com.chyzman.chowl.Chowl;
import com.chyzman.chowl.block.DrawerFrameBlock;
import com.chyzman.chowl.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.classes.AttackInteractionReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chyzman.chowl.block.DrawerFrameBlockEntity.writePanelsToNbt;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    public GameOptions options;

    @Inject(method = "doAttack", at = @At(value = "HEAD"), cancellable = true)
    private void stopBreakingDrawersSmh(CallbackInfoReturnable<Boolean> cir) {
        if (world == null || !(crosshairTarget instanceof BlockHitResult blockHit)) return;

        var pos = blockHit.getBlockPos();
        var state = this.world.getBlockState(pos);
        if (!(state.getBlock() instanceof AttackInteractionReceiver receiver)) return;

        var result = receiver.onAttack(this.world, state, blockHit, player);
        if (!result.isAccepted()) return;

        this.player.swingHand(Hand.MAIN_HAND);
        Chowl.CHANNEL.clientHandle().send(new AttackInteractionReceiver.InteractionPacket(blockHit));

        options.attackKey.setPressed(false);
        cir.setReturnValue(true);
    }

    @Inject(method = "addBlockEntityNbt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/BlockItem;setBlockEntityNbt(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/nbt/NbtCompound;)V",
            shift = At.Shift.AFTER), cancellable = true)
    private void removeTheNbtLore(ItemStack stack, BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof DrawerFrameBlockEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "doItemPick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;addBlockEntityNbt(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/entity/BlockEntity;)V",
            shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void pickDrawerFrame(CallbackInfo ci, boolean bl, BlockEntity blockEntity, ItemStack itemStack, HitResult.Type type, PlayerInventory playerInventory) {
        if (blockEntity instanceof DrawerFrameBlockEntity frame) {
            var sides = Direction.getEntityFacingOrder(player);
            var newPanels = new ArrayList<>(frame.stacks);
            if (sides[0].getAxis().isHorizontal()) {
                for (int i = 2; i < 6; i++) {
                    newPanels.set(i, frame.stacks.get(Direction.fromRotation(sides[0].asRotation()).getId()));
                }
                var nbt = itemStack.getNbt();
                var subNbt = itemStack.getOrCreateSubNbt("BlockEntityTag");
                writePanelsToNbt(newPanels, subNbt);
                nbt.put("BlockEntityTag", subNbt);
                itemStack.writeNbt(nbt);
            }
        }
    }
}