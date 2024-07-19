package com.chyzman.chowl.industries.commands;

import com.chyzman.chowl.industries.graph.ServerGraphStore;
import com.chyzman.chowl.industries.util.CompressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class DebugCommands {
    private DebugCommands() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            literal("chowl")
                .then(literal("graph")
                    .then(literal("dump")
                        .executes(DebugCommands::dumpGraphs))
                    .then(literal("clear")
                        .executes(DebugCommands::clearGraphs)))
                .then(literal("compression")
                    .then(literal("dump")
                        .executes(DebugCommands::dumpCompression))
                    .then(literal("build_all")
                        .executes(DebugCommands::buildAllCompression))
                    .then(literal("clear")
                        .executes(DebugCommands::clearCompression))
                    .then(literal("build")
                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                            .executes(DebugCommands::buildItemCompression))))
        );
    }

    private static int dumpGraphs(CommandContext<ServerCommandSource> ctx) {
        StringBuilder sb = new StringBuilder();
        ServerGraphStore state = ServerGraphStore.get(ctx.getSource().getWorld());

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

    private static int clearGraphs(CommandContext<ServerCommandSource> ctx) {
        ServerGraphStore state = ServerGraphStore.get(ctx.getSource().getWorld());

        state.clear();

        ctx.getSource().sendFeedback(() -> Text.literal("Cleared all graph data."), false);
        return 1;
    }

    private static int dumpCompression(CommandContext<ServerCommandSource> ctx) {
        String dotGraph = CompressionManager.dumpDotGraph();
        Path graphPath = FabricLoader.getInstance().getGameDir().resolve("chowl_compression_graph.dot").toAbsolutePath();

        try {
            Files.writeString(graphPath, dotGraph);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ctx.getSource().sendFeedback(() -> Text.literal("Dumped compression graph to " + graphPath), false);
        return 1;
    }

    private static int clearCompression(CommandContext<ServerCommandSource> ctx) {
        CompressionManager.NODES.clear();

        ctx.getSource().sendFeedback(() -> Text.literal("Cleared compression graph."), false);
        return 1;
    }

    private static int buildItemCompression(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Item item = ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem();

        long startNanos = System.nanoTime();
        CompressionManager.getOrCreateNode(item);
        long durationNanos = System.nanoTime() - startNanos;
        double duration = (double) durationNanos / 1000000000;

        ctx.getSource().sendFeedback(() -> Text.literal("Built compression graph for " + Registries.ITEM.getId(item) + " in " + duration + "s"), false);
        return 1;
    }

    private static int buildAllCompression(CommandContext<ServerCommandSource> ctx) {
        CompressionManager.NODES.clear();

        long startNanos = System.nanoTime();

        for (Item item : Registries.ITEM) {
            CompressionManager.getOrCreateNode(item);
        }

        long durationNanos = System.nanoTime() - startNanos;
        double duration = (double) durationNanos / 1000000000;

        ctx.getSource().sendFeedback(() -> Text.literal("Rebuilt all compression graph nodes in " + duration + "s"), false);
        return 1;
    }
}
