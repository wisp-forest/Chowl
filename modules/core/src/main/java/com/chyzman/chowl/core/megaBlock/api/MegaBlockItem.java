package com.chyzman.chowl.core.megaBlock.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;

public class MegaBlockItem extends BlockItem {

    public MegaBlockItem(MegaBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    protected boolean place(ItemPlacementContext ctx, BlockState state) {
        if (!(state.getBlock() instanceof MegaBlock block)) return false;
        var world = ctx.getWorld();

        //TODO: actually do something here
        var origin = ctx.getBlockPos();

        var size = block.size(state);
        var box = Box.enclosing(origin, origin.add(size).add(-1, -1, -1));
        if (!world.isSpaceEmpty(box)) return false;

        for (int i = 0; i < 2; i++) {
            for (var x = 0; x < size.getX(); x++) {
                for (var y = 0; y < size.getY(); y++) {
                    for (var z = 0; z < size.getZ(); z++) {
                        var pos = origin.add(x, y, z);
                        var hit = new BlockHitResult(ctx.getHitPos().add(x, y, z), ctx.getSide(), pos, ctx.hitsInsideBlock());
                        var subCtx = new ItemPlacementContext(world, ctx.getPlayer(), ctx.getHand(), ctx.getStack(), hit);
                        if (i == 0) {
                            if (!world.getBlockState(pos).canReplace(subCtx)) return false;
                        } else {
                            if (!super.place(subCtx, block.set(state,x, y, z))) return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
