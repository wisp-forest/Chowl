package com.chyzman.chowl.oddities.item;

import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class StringItem extends Item {
    public StringItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getWorld().isClient()) return ActionResult.SUCCESS;

        ctx.getStack().decrementUnlessCreative(1, ctx.getPlayer());

        final var rotation = new Quaternionf();

        var player = ctx.getPlayer();
        if (player != null) {
            var pitch = Math.toRadians(player.getPitch() + 90);
            var yaw = Math.toRadians(-player.getHeadYaw());

            var pitchQuat = new Quaternionf().rotateX((float) pitch);
            var yawQuat = new Quaternionf().rotateY((float) yaw);

            rotation.set(yawQuat).mul(pitchQuat);
        }

        OdditiesAttachables.STRING.create(
                ctx.getWorld(),
                pin -> pin
                        .pos(ctx.getHitPos())
                        .rotation(rotation)
                        .endPos(ctx.getHitPos().multiply(-1, 1, -1))
                        .endRotation(new Quaternionf(rotation).rotateY((float) Math.toRadians(180)))
        );

        return ActionResult.CONSUME;
    }
}
