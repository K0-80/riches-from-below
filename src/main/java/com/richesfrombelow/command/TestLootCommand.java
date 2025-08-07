package com.richesfrombelow.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.util.GatchaBallLootTableUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.EnumMap;
import java.util.Map;

public class TestLootCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("gatchaballloottest")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 1000000))
                        .executes(TestLootCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final int count = IntegerArgumentType.getInteger(context, "count");
        final ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        final Random random = player.getWorld().getRandom();

        Map<GatchaBallLootTableUtil.GatchaTier, Integer> tierCounts = new EnumMap<>(GatchaBallLootTableUtil.GatchaTier.class);
        for (GatchaBallLootTableUtil.GatchaTier tier : GatchaBallLootTableUtil.GatchaTier.values()) {
            tierCounts.put(tier, 0);
        }

        long koboCoins = 0;
        long coinFragments = 0;

        for (int i = 0; i < count; i++) {
            GatchaBallLootTableUtil.GatchaResult result = GatchaBallLootTableUtil.generateLoot(random);
            tierCounts.compute(result.tier(), (tier, currentCount) -> currentCount + 1);

            for (ItemStack stack : result.items()) {
                if (stack.isOf(ModItems.KOBO_COIN)) {
                    koboCoins += stack.getCount();
                } else if (stack.isOf(ModItems.COIN_FRAGMENT)) {
                    coinFragments += stack.getCount();
                }
                player.dropItem(stack.copy(), false, true);
            }
        }

        long totalKoboCoins = koboCoins + (coinFragments / 9);
        long remainingFragments = coinFragments % 9;

        context.getSource().sendFeedback(() -> Text.literal(""), false);
        context.getSource().sendFeedback(() -> Text.literal(String.format("Total K0KO Coins gained: %d (and %d fragments)", totalKoboCoins, remainingFragments)), false);


        return count;
    }
}