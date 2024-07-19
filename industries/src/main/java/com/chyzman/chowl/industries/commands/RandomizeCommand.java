package com.chyzman.chowl.industries.commands;

import com.chyzman.chowl.industries.block.DrawerFrameBlockEntity;
import com.chyzman.chowl.industries.item.component.StoragePanelItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class RandomizeCommand {
    private RandomizeCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("chowl")
            .then(literal("randomize")
                .requires(x -> x.hasPermissionLevel(3))
                .then(argument("from", BlockPosArgumentType.blockPos())
                    .then(argument("to", BlockPosArgumentType.blockPos())
                        .executes(RandomizeCommand::randomize)))));
    }



    private static int randomize(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var from = BlockPosArgumentType.getLoadedBlockPos(ctx, "from");
        var to = BlockPosArgumentType.getLoadedBlockPos(ctx, "to");
        var world = ctx.getSource().getWorld();

        for (var pos : BlockPos.iterate(from, to)) {
            if (!(world.getBlockEntity(pos) instanceof DrawerFrameBlockEntity frame)) continue;

            for (int sideId = 0; sideId < 6; sideId++) {
                var stack = frame.stacks.get(sideId).stack();

                if (!(stack.getItem() instanceof StoragePanelItem storage)) continue;

                var capacity = storage.fullCapacity(stack);
                BigInteger count;

                do {
                    count = new BigInteger(capacity.bitLength(), ThreadLocalRandom.current());
                } while (count.compareTo(capacity) > 0);

                storage.setCount(stack, count);
                frame.stacks.set(sideId, frame.stacks.get(sideId).withStack(stack));
                frame.markDirty();
            }
        }

        return 0;
    }
}
