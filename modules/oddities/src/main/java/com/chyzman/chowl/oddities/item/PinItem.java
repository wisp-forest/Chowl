package com.chyzman.chowl.oddities.item;

import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class PinItem extends Item {
    public PinItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getWorld().isClient()) return ActionResult.SUCCESS;

        ctx.getStack().decrementUnlessCreative(1, ctx.getPlayer());

        final var rotation = new Quaternionf();

        var player = ctx.getPlayer();
        if (player != null) {
            var pitch = Math.toRadians(player.getPitch());
            var yaw = Math.toRadians(-player.getHeadYaw());

            var pitchQuat = new Quaternionf().rotateX((float) pitch);
            var yawQuat = new Quaternionf().rotateY((float) yaw);

            rotation.set(yawQuat).mul(pitchQuat);
        }

        OdditiesAttachables.PIN.create(
                ctx.getWorld(),
                pin -> pin
                        .pos(ctx.getHitPos())
                        .rotation(rotation)
        );

        return ActionResult.CONSUME;
    }
}
