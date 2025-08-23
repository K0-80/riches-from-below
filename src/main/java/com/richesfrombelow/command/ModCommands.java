package com.richesfrombelow.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(TestLootCommand::register);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> TestSlotCommand.register(dispatcher));

    }
}