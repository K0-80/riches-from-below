package com.richesfrombelow.items.custom;

import com.richesfrombelow.loot.ModLootTables;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FortuneCookieItem extends Item {
    private static final List<String> FORTUNES = List.of(
            "text.richesfrombelow.fortune.1",
            "text.richesfrombelow.fortune.2",
            "text.richesfrombelow.fortune.3",
            "text.richesfrombelow.fortune.4",
            "text.richesfrombelow.fortune.5",
            "text.richesfrombelow.fortune.6",
            "text.richesfrombelow.fortune.7",
            "text.richesfrombelow.fortune.8",
            "text.richesfrombelow.fortune.9",
            "text.richesfrombelow.fortune.10",
            "text.richesfrombelow.fortune.11",
            "text.richesfrombelow.fortune.12"
    );

    public FortuneCookieItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (!world.isClient) {
                // This ensures the main hand item is consumed, even if it stacks
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                double chance = world.getRandom().nextDouble();

                if (chance < 0.01) { // 1%
                    if (!findAndPlaceTreasure(player, (ServerWorld) world)) {
                        grantRandomBuff(player);
                    }
                } else if (chance < 0.10) { // 9%
                    grantRandomBuff(player);
                } else { // 90%
                    grantRandomFortune(player);
                }
            }
        }
        return stack;
    }

    private void grantRandomFortune(PlayerEntity player) {
        String fortuneKey = FORTUNES.get(player.getWorld().getRandom().nextInt(FORTUNES.size()));
        player.sendMessage(Text.translatable(fortuneKey).formatted(Formatting.YELLOW));
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.5f);
    }

    private void grantRandomBuff(PlayerEntity player) {
        World world = player.getWorld();
        StatusEffectInstance effect;
        Text message;

        if (world.getRandom().nextBoolean()) {
            effect = new StatusEffectInstance(StatusEffects.LUCK, 600, 0); // 30 seconds
            message = Text.translatable("text.richesfrombelow.fortune.buff.luck").formatted(Formatting.GREEN);
        } else {
            effect = new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 2); // 30 seconds
            message = Text.translatable("text.richesfrombelow.fortune.buff.absorption").formatted(Formatting.GOLD);
        }

        player.addStatusEffect(effect);
        player.sendMessage(message);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.2f);
    }

    private boolean findAndPlaceTreasure(PlayerEntity player, ServerWorld world) {
        final int radius = 50;
        final int searchAttempts = 200; // Try 200 times to find a stone block

        for (int i = 0; i < searchAttempts; i++) {
            int x = player.getBlockX() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
            int y = player.getBlockY() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
            int z = player.getBlockZ() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);

            BlockPos targetPos = new BlockPos(x, y, z);

            if (world.getBlockState(targetPos).isOf(Blocks.STONE)) {
                world.setBlockState(targetPos, Blocks.CHEST.getDefaultState());
                if (world.getBlockEntity(targetPos) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(ModLootTables.FORTUNE_COOKIE_TREASURE_CHEST_KEY);
                }

                player.sendMessage(Text.translatable("text.richesfrombelow.fortune.treasure", x, y, z).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return true; // Success
            }
        }
        return false; // Failed to find a suitable spot
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.fortune_cookie.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}