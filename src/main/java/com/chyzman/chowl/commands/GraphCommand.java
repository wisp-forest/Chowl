package com.chyzman.chowl.commands;

import com.chyzman.chowl.graph.CrudeGraphState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public final class GraphCommand {
    private GraphCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("chowl")
                .then(literal("graph")
                    .then(literal("dump")
                        .executes(GraphCommand::dump))
                    .then(literal("clear")
                        .executes(GraphCommand::clear)))
        );
    }

    private static int dump(CommandContext<ServerCommandSource> ctx) {
        StringBuilder sb = new StringBuilder();
        CrudeGraphState state = CrudeGraphState.getFor(ctx.getSource().getWorld());

        sb.append("Dumping all graph data");
        for (var entry : state.graphs().values()) {
            sb.append("\nGraph ").append(entry.graphId).append(", ").append(entry.nodes.size()).append(" items:");
            for (var node : entry.nodes.values()) {
                sb.append("\n  ").append(node.state()).append(" at ").append(node.pos()).append(", linked to ");

                for (var link : node.links()) {
                    sb.append(BlockPos.fromLong(link)).append(" ");
                }
            }
        }

        ctx.getSource().sendFeedback(() -> Text.literal(sb.toString()), false);
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> ctx) {
        CrudeGraphState state = CrudeGraphState.getFor(ctx.getSource().getWorld());

        state.clear();

        ctx.getSource().sendFeedback(() -> Text.literal("Cleared all graph data."), false);
        return 1;
    }
}
