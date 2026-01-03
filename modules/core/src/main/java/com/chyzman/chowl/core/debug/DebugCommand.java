package com.chyzman.chowl.core.debug;

import com.chyzman.chowl.core.format.number.NumberFormatter;
import com.chyzman.chowl.core.format.number.api.NumberFormatterTypes;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.wispforest.owo.command.EnumArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DebugCommand {
    public static final EnumArgumentType<NumberFormatterTypes> NUMBER_FORMATTER_ARGUMENT_TYPE = EnumArgumentType.create(
        NumberFormatterTypes.class,
        "'{}' is not a valid number formatter"
    );

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                literal("chowlDebug")
                    .then(literal("formatNumber")
                        .then(argument("number", StringArgumentType.string())
                            .executes(context -> formatNumber(context, NumberFormatterTypes.LETTER))
                            .then(argument("formatter", NUMBER_FORMATTER_ARGUMENT_TYPE)
                                .executes(context -> formatNumber(context, NUMBER_FORMATTER_ARGUMENT_TYPE.get(context, "formatter"))))

                        )
                    )
            );
        });
    }

    public static int formatNumber(CommandContext<ServerCommandSource> context, NumberFormatterTypes type) {
        var number = StringArgumentType.getString(context, "number");
        context.getSource().sendFeedback(() -> NumberFormatter.format(number, type), false);
        return 1;
    }
}
