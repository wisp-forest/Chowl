package com.chyzman.chowl.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.*;

public class CompressionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Chowl/CompressionManager");
    public static final Map<Item, Node> NODES = new HashMap<>();
    private static ThreadLocal<WeakReference<World>> world = new ThreadLocal<>();

    public static void rebuild(World w) {
        NODES.clear();
        world.set(new WeakReference<>(w));
    }

    public static String dumpDotGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph \"Compression Ladder\" {\n");

        for (var node : NODES.values()) {
            sb.append("    ");
            sb.append('"').append(Registries.ITEM.getId(node.item)).append("\";\n");

            if (node.next == null) continue;

            sb.append("    ");
            sb.append('"').append(Registries.ITEM.getId(node.item)).append('"');
            sb.append(" -> ");
            sb.append('"').append(Registries.ITEM.getId(node.next.item)).append('"');
            sb.append(" [label=").append(node.nextAmount).append("];\n");
        }

        sb.append("}");
        return sb.toString();
    }

    public static Node getOrCreateNode(Item item) {
        var node = NODES.computeIfAbsent(item, Node::new);
        node.tryFill(new HashSet<>());
        return node;
    }

    public static ScendResult followDown(Item item) {
        var node = getOrCreateNode(item);
        BigInteger totalMultiplier = BigInteger.ONE;
        int totalSteps = 0;

        while (node.previous != null) {
            totalMultiplier = totalMultiplier.multiply(BigInteger.valueOf(node.previousAmount));
            totalSteps++;
            node = node.previous;
        }

        return new ScendResult(node.item, totalMultiplier, totalSteps);
    }

    public static @Nullable CompressionManager.ScendResult downBy(Item item, int amount) {
        var node = getOrCreateNode(item);
        BigInteger totalMultiplier = BigInteger.ONE;
        int totalSteps = 0;

        for (int i = 0; i < amount; i++) {
            if (node.previous == null) return null;
            totalMultiplier = totalMultiplier.multiply(BigInteger.valueOf(node.previousAmount));
            totalSteps++;
            node = node.previous;
        }

        return new ScendResult(node.item, totalMultiplier, totalSteps);
    }

    public static ScendResult followUp(Item item) {
        var node = getOrCreateNode(item);
        BigInteger totalMultiplier = BigInteger.ONE;
        int totalSteps = 0;

        while (node.next != null) {
            totalMultiplier = totalMultiplier.multiply(BigInteger.valueOf(node.nextAmount));
            totalSteps++;
            node = node.next;
        }

        return new ScendResult(node.item, totalMultiplier, totalSteps);
    }

    public static @Nullable CompressionManager.ScendResult upBy(Item item, int amount) {
        var node = getOrCreateNode(item);
        BigInteger totalMultiplier = BigInteger.ONE;
        int totalSteps = 0;

        for (int i = 0; i < amount; i++) {
            if (node.next == null) return null;
            totalMultiplier = totalMultiplier.multiply(BigInteger.valueOf(node.nextAmount));
            totalSteps++;
            node = node.next;
        }

        return new ScendResult(node.item, totalMultiplier, totalSteps);
    }

    public record ScendResult(Item item, BigInteger totalMultiplier, int totalSteps) {}

    public static class Node {
        public final Item item;
        public Node next = null;
        public int nextAmount = -1;
        public Node previous = null;
        public int previousAmount = -1;

        private boolean initialized = false;

        public Node(Item item) {
            this.item = item;
        }

        private void tryFill(Set<Item> visited) {
            if (this.initialized) return;
            this.initialized = true;

            visited.add(item);

            ItemStack stack = item.getDefaultStack();

            ItemStack toStack = try3x3(stack);

            if (toStack != null && !visited.contains(toStack.getItem())) {
                Node toNode = NODES.get(toStack.getItem());
                if (toNode == null || toNode.previous == null) {
                    ItemStack backStack = try1x1(toStack);

                    if (backStack != null && backStack.getItem() == item && backStack.getCount() == 9) {
                        toNode = NODES.computeIfAbsent(toStack.getItem(), Node::new);

                        next = toNode;
                        nextAmount = 9;
                        toNode.previous = this;
                        toNode.previousAmount = 9;
                        LOGGER.debug("Linked {} -> {} ({})", item, toNode.item, 9);

                        toNode.tryFill(visited);
                    }
                }
            } else {
                toStack = try2x2(stack);

                if (toStack != null && !visited.contains(toStack.getItem())) {
                    Node toNode = NODES.get(toStack.getItem());
                    if (toNode == null || toNode.previous == null) {
                        ItemStack backStack = try1x1(toStack);

                        if (backStack != null && backStack.getItem() == item && backStack.getCount() == 4) {
                            toNode = NODES.computeIfAbsent(toStack.getItem(), Node::new);

                            next = toNode;
                            nextAmount = 4;
                            toNode.previous = this;
                            toNode.previousAmount = 4;
                            LOGGER.debug("Linked {} -> {} ({})", item, toNode.item, 4);

                            toNode.tryFill(visited);
                        }
                    }
                }
            }

            toStack = try1x1(stack);

            if (toStack != null && !visited.contains(toStack.getItem())) {
                Node toNode = NODES.get(toStack.getItem());
                if (toNode == null || toNode.next == null) {
                    ItemStack backStack = try3x3(toStack);
                    int amount = 9;

                    if (backStack == null) {
                        backStack = try2x2(toStack);
                        amount = 4;
                    }

                    if (backStack != null && backStack.getItem() == item && backStack.getCount() == 1) {
                        toNode = NODES.computeIfAbsent(toStack.getItem(), Node::new);

                        previous = toNode;
                        previousAmount = amount;
                        toNode.next = this;
                        toNode.nextAmount = amount;
                        LOGGER.debug("Linked {} -> {} ({})", toNode.item, item, amount);

                        toNode.tryFill(visited);
                    }
                }
            }
        }

        private static @Nullable ItemStack try3x3(ItemStack of) {
            List<ItemStack> stacks = new ArrayList<>(9);
            for (int i = 0; i < 9; i++) stacks.add(of);

            CraftingRecipeInput input = CraftingRecipeInput.create(3, 3, stacks);

            World w = world.get().get();
            var recipe = w.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, w);
            return recipe
                .map(craftingRecipe -> craftingRecipe.value().craft(input, DynamicRegistryManager.EMPTY))
                .orElse(null);
        }

        private static @Nullable ItemStack try1x1(ItemStack of) {
            CraftingRecipeInput input = CraftingRecipeInput.create(1, 1, List.of(of));

            World w = world.get().get();
            var recipe = w.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, w);
            return recipe
                .map(craftingRecipe -> craftingRecipe.value().craft(input, DynamicRegistryManager.EMPTY))
                .orElse(null);
        }

        private static @Nullable ItemStack try2x2(ItemStack of) {
            CraftingRecipeInput input = CraftingRecipeInput.create(2, 2, List.of(of, of, of, of));

            World w = world.get().get();
            var recipe = w.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, w);
            return recipe
                .map(craftingRecipe -> craftingRecipe.value().craft(input, DynamicRegistryManager.EMPTY))
                .orElse(null);
        }
    }
}