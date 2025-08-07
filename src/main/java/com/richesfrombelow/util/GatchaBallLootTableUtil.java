package com.richesfrombelow.util;

import com.richesfrombelow.items.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;


import java.util.List;
import java.util.function.Supplier;

public class GatchaBallLootTableUtil {

    public enum GatchaTier {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY,
        EXOTIC
    }

    public record GatchaResult(GatchaTier tier, List<ItemStack> items) {
    }

    public static GatchaResult generateLoot(Random random) {
        GatchaTier selectedTier = selectTier(random);
        List<ItemStack> lootStacks = getLootForTier(selectedTier, random);
        return new GatchaResult(selectedTier, lootStacks);
    }

    public static int getAnimationTicks(GatchaTier tier) {
        return switch (tier) {
            case COMMON -> 2 * 20;
            case UNCOMMON -> 3 * 20;
            case RARE -> 5 * 20;
            case EPIC -> 7 * 20;
            case LEGENDARY -> 10 * 20;
            case EXOTIC -> 15 * 20;
        };
    }

    private static GatchaTier selectTier(Random random) {
        int totalWeight = 100;
        int roll = random.nextInt(totalWeight);

        if (roll < 40) return GatchaTier.COMMON; //40
        if (roll < 70) return GatchaTier.UNCOMMON; //30
        if (roll < 85) return GatchaTier.RARE; //15
        if (roll < 95) return GatchaTier.EPIC; //10
        if (roll < 99) return GatchaTier.LEGENDARY; //4
        return GatchaTier.EXOTIC; //1
    }

    private static List<ItemStack> getLootForTier(GatchaTier tier, Random random) {
        return switch (tier) {
            case COMMON -> getCommonLoot(random);
            case UNCOMMON -> getUncommonLoot(random);
            case RARE -> getRareLoot(random);
            case EPIC -> getEpicLoot(random);
            case LEGENDARY -> getLegendaryLoot(random);
            case EXOTIC -> getExoticLoot(random);
        };
    }

    private static List<ItemStack> getCommonLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                () -> new ItemStack(ModItems.COIN_FRAGMENT, random.nextInt(3) + 1), // 1-3
                () -> new ItemStack(ModItems.FORTUNE_COOKIE, random.nextInt(3) + 1),
                () -> random.nextBoolean() ? new ItemStack(Items.COAL, random.nextInt(9) + 8) : new ItemStack(Items.RAW_IRON, random.nextInt(9) + 8), // 8-16
                () -> random.nextBoolean() ? new ItemStack(Items.DIRT, random.nextInt(17) + 16) : new ItemStack(Items.COBBLESTONE, random.nextInt(17) + 16), // 16-32
                () -> new ItemStack(Items.ARROW, random.nextInt(17) + 16), // 16-32
                () -> new ItemStack(Items.LEATHER, random.nextInt(5) + 4), // 4-8
                () -> new ItemStack(Items.GLASS_BOTTLE, random.nextInt(5) + 4), // 4-8
                () -> new ItemStack(Items.STICK, random.nextInt(17) + 16), // 16-32
                () -> new ItemStack(Items.STRING, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.WHEAT_SEEDS, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.OAK_SAPLING, random.nextInt(3) + 1), // 1-3
                () -> random.nextBoolean() ? new ItemStack(Items.CARROT, random.nextInt(6) + 5) : new ItemStack(Items.POTATO, random.nextInt(6) + 5) // 5-10
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

    private static List<ItemStack> getUncommonLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                //                () -> createRandomEnchantedBook(random, 1)
                () -> new ItemStack(ModItems.COIN_FRAGMENT, random.nextInt(2) + 2),
                () -> new ItemStack(ModItems.FORTUNE_COOKIE, random.nextInt(6) + 5), // 5-10
                () -> new ItemStack(Items.GOLD_INGOT, random.nextInt(4) + 3), // 3-6
                () -> new ItemStack(Items.IRON_INGOT, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.BREAD, random.nextInt(6) + 5), // 5-10
                () -> new ItemStack(Items.COOKED_CHICKEN, random.nextInt(6) + 5), // 5-10
                () -> new ItemStack(Items.REDSTONE, random.nextInt(17) + 16), // 16-32
                () -> new ItemStack(Items.LAPIS_LAZULI, random.nextInt(17) + 16), // 16-32
                () -> new ItemStack(Items.GUNPOWDER, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.SLIME_BALL, random.nextInt(5) + 4), // 4-8
                () -> new ItemStack(Items.ENDER_PEARL, random.nextInt(3) + 1), // 1-3
                () -> new ItemStack(Items.EXPERIENCE_BOTTLE, random.nextInt(9) + 8) // 8-16
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

    private static List<ItemStack> getRareLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                //                () -> createRandomEnchantedBook(random, 2)
                () -> new ItemStack(ModItems.COIN_FRAGMENT, random.nextInt(2) + 5),
                () -> new ItemStack(ModItems.COLLECTOR_SUITCASE, 1),
                () -> new ItemStack(ModItems.WISHING_STAR, random.nextInt(2) + 2),
                () -> new ItemStack(ModItems.LIQUID_GOLD, random.nextInt(2) + 2),
                () -> new ItemStack(Items.DIAMOND, random.nextInt(3) + 3), // 3-5
                () -> new ItemStack(Items.GOLDEN_APPLE, random.nextInt(2) + 1), // 1-2
                () -> new ItemStack(Items.EMERALD, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.SADDLE, 1),
                () -> new ItemStack(Items.NAME_TAG, 1),
                () -> new ItemStack(Items.AMETHYST_SHARD, random.nextInt(9) + 8), // 8-16
                () -> new ItemStack(Items.BLAZE_ROD, random.nextInt(5) + 4), // 4-8
                () -> new ItemStack(Items.DIAMOND_HORSE_ARMOR, 1)
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

    private static List<ItemStack> getEpicLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                //                () -> createRandomEnchantedBook(random, 3)
                () -> new ItemStack(ModItems.KOBO_COIN, random.nextInt(2)), // 2-3
                () -> new ItemStack(ModItems.ALL_IN, 1),
                () -> new ItemStack(ModItems.DECK_OF_FATES, 1),
                () -> new ItemStack(Items.NETHERITE_SCRAP, random.nextInt(2) + 1), // 1-2
                () -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1),
                () -> new ItemStack(Items.TOTEM_OF_UNDYING, 1),
                () -> new ItemStack(Items.MUSIC_DISC_CREATOR, 1),
                () -> new ItemStack(Items.HEART_OF_THE_SEA, 1),
                () -> new ItemStack(Items.DIAMOND_BLOCK, random.nextInt(2) + 1), // 1-2
                () -> new ItemStack(Items.SHULKER_SHELL, random.nextInt(3) + 2), // 2-4
                () -> new ItemStack(Items.TRIDENT, 1),
                () -> new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1)
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

    private static List<ItemStack> getLegendaryLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                //                () -> createRandomEnchantedBook(random, 4)
                () -> new ItemStack(ModItems.KOBO_COIN, random.nextInt(2) + 2), // 2-3
                () -> new ItemStack(ModItems.CROWN_OF_GREED, 1),
                () -> new ItemStack(ModItems.PACIFIST_CROWN, 1),
                () -> new ItemStack(Items.NETHERITE_INGOT, random.nextInt(2) + 1), // 1-2
                () -> new ItemStack(Items.NETHERITE_BLOCK, 1),
                () -> new ItemStack(Items.NETHER_STAR, 1),
                () -> new ItemStack(Items.ELYTRA, 1),
                () -> new ItemStack(Items.WITHER_SKELETON_SKULL, random.nextInt(2) + 1) // 1-2
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

    private static List<ItemStack> getExoticLoot(Random random) {
        List<Supplier<ItemStack>> pool = List.of(
                () -> new ItemStack(ModItems.KOBO_COIN, 32),
//                () -> new ItemStack(ModItems.GUILDED_CONTRACT, 1),
                () -> new ItemStack(Items.DRAGON_EGG, 1)
        );
        return List.of(pool.get(random.nextInt(pool.size())).get());
    }

//    private record EnchantmentData(RegistryKey<Enchantment> enchantmentKey, int level) {
//    }
//
//    private static ItemStack createRandomEnchantedBook(Random random, int tier) {
//        List<EnchantmentData> possibleEnchantments = switch (tier) {
//            // Uncommon
//            case 1 -> List.of(
//                    new EnchantmentData(Enchantments.PROTECTION, 1),
//                    new EnchantmentData(Enchantments.SHARPNESS, 1),
//                    new EnchantmentData(Enchantments.EFFICIENCY, 1)
//            );
//            // Rare
//            case 2 -> List.of(
//                    new EnchantmentData(Enchantments.PROTECTION, 2),
//                    new EnchantmentData(Enchantments.SHARPNESS, 3),
//                    new EnchantmentData(Enchantments.EFFICIENCY, 2)
//            );
//            // Epic
//            case 3 -> List.of(
//                    new EnchantmentData(Enchantments.PROTECTION, 4),
//                    new EnchantmentData(Enchantments.SHARPNESS, 5),
//                    new EnchantmentData(Enchantments.EFFICIENCY, 5)
//            );
//            // Legendary
//            case 4 -> List.of(
//                    new EnchantmentData(Enchantments.PROTECTION, 5),
//                    new EnchantmentData(Enchantments.SHARPNESS, 6),
//                    new EnchantmentData(Enchantments.EFFICIENCY, 6),
//                    new EnchantmentData(Enchantments.MENDING, 1)
//            );
//            default -> List.of();
//        };
//
//        if (possibleEnchantments.isEmpty()) {
//            return new ItemStack(Items.ENCHANTED_BOOK);
//        }
//
//        EnchantmentData selected = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
//        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
//
//        Enchantment enchantment = Registries.ENCHANTMENT.get(selected.enchantmentKey());
//        if (enchantment == null) {
//            return new ItemStack(Items.ENCHANTED_BOOK); // Fallback if registry lookup fails
//        }
//
//        StoredEnchantmentsComponent enchantments = new StoredEnchantmentsComponent.Builder()
//                .add(enchantment, selected.level())
//                .build();
//
//        book.set(DataComponentTypes.STORED_ENCHANTMENTS, enchantments);
//        return book;
//    }
}