package com.hamusuke.numguesser.command;

import com.hamusuke.numguesser.command.commands.StopCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class Commands {
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        StopCommand.register(dispatcher);
    }

    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static RequiredArgumentBuilder<CommandSource, ?> argument(String name, ArgumentType<?> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
