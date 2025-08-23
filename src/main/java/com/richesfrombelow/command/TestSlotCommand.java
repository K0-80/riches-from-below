package com.richesfrombelow.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.richesfrombelow.block.SlotMachineBlock;
import com.richesfrombelow.block.entity.SlotMachineBlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TestSlotCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("testslot")
                .requires(source -> source.hasPermissionLevel(2)) // Operator-only command
                .then(CommandManager.argument("result1", StringArgumentType.string())
                        .suggests(TestSlotCommand::getSlotResultSuggestions)
                        .then(CommandManager.argument("result2", StringArgumentType.string())
                                .suggests(TestSlotCommand::getSlotResultSuggestions)
                                .then(CommandManager.argument("result3", StringArgumentType.string())
                                        .suggests(TestSlotCommand::getSlotResultSuggestions)
                                        .executes(TestSlotCommand::run)))));
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        HitResult hit = player.raycast(5.0, 0.0f, false);

        if (hit.getType() != HitResult.Type.BLOCK) {
            context.getSource().sendError(Text.literal("You are not looking at a block."));
            return 0;
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        if (!(player.getWorld().getBlockEntity(pos) instanceof SlotMachineBlockEntity be)) {
            context.getSource().sendError(Text.literal("The block you are looking at is not a Slot Machine."));
            return 0;
        }

        try {
            SlotMachineBlock.SlotResult r1 = SlotMachineBlock.SlotResult.valueOf(StringArgumentType.getString(context, "result1").toUpperCase());
            SlotMachineBlock.SlotResult r2 = SlotMachineBlock.SlotResult.valueOf(StringArgumentType.getString(context, "result2").toUpperCase());
            SlotMachineBlock.SlotResult r3 = SlotMachineBlock.SlotResult.valueOf(StringArgumentType.getString(context, "result3").toUpperCase());

            SlotMachineBlock.SlotResult[] results = {r1, r2, r3};

            be.startSpin(results, player);

            context.getSource().sendFeedback(() -> Text.literal("Forcing slot machine result: " + Arrays.toString(results)), true);
            return 1;

        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Invalid slot result name. Use YELLOW, GREEN, RED, or PURPLE."));
            return 0;
        }
    }

    private static CompletableFuture<Suggestions> getSlotResultSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                Stream.of(SlotMachineBlock.SlotResult.values()).map(Enum::name),
                builder
        );
    }
}