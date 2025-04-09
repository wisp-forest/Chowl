package com.chyzman.chowl.oddities.item;

import com.chyzman.chowl.oddities.registry.OdditiesAttachables;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

public class StickyNoteItem extends Item {
    public StickyNoteItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getWorld().isClient()) return ActionResult.SUCCESS;

        ctx.getStack().decrementUnlessCreative(1, ctx.getPlayer());

        var side = ctx.getSide();

        final var rotation = side.getOpposite().getRotationQuaternion();

        if (side.getAxis() == Direction.Axis.Y) {
            var rot = (float) Math.toRadians(ctx.getPlayerYaw());
            rotation.rotateY(rot * (side == Direction.UP ? 1 : -1));
        }

        OdditiesAttachables.STICKY_NOTE.create(
                ctx.getWorld(),
                stickyNote -> stickyNote
                        .pos(ctx.getHitPos())
                        .rotation(rotation)
        );

        return ActionResult.CONSUME;
    }
}
