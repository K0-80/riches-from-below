package com.richesfrombelow.util;

import com.richesfrombelow.RichesfromBelow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class GatchaBallLootTableUtil {

    public enum GatchaTier {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY,
        EXOTIC
    }

    public record GatchaResult(GatchaTier tier, List<ItemStack> items) {}

    public static GatchaResult generateLoot(Random random) {
        GatchaTier selectedTier = selectTier(random);
        List<ItemStack> lootStacks = getLootForTier(selectedTier, random);
        return new GatchaResult(selectedTier, lootStacks);
    }

    public static int getAnimationTicks(GatchaTier tier) {
        RichesfromBelow.LOGGER.info("tier" + tier);
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

        if (roll < 40) return GatchaTier.COMMON;
        if (roll < 70) return GatchaTier.UNCOMMON;
        if (roll < 85) return GatchaTier.RARE;
        if (roll < 95) return GatchaTier.EPIC;
        if (roll < 99) return GatchaTier.LEGENDARY;
        return GatchaTier.EXOTIC;
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
        List<ItemStack> pool = List.of(
                new ItemStack(Items.COAL)
        );
        // this is how to give more then 1 item from the pool
        int amountToDrop = random.nextInt(2) + 1;
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < amountToDrop; i++) {
            result.add(pool.get(random.nextInt(pool.size())).copy());
        }
        return result;
    }

    private static List<ItemStack> getUncommonLoot(Random random) {
        List<ItemStack> pool = List.of(
                new ItemStack(Items.IRON_INGOT, 2 )  //can be more then 1 item
        );
        return List.of(pool.get(random.nextInt(pool.size())).copy());
    }

    private static List<ItemStack> getRareLoot(Random random) {
        List<ItemStack> pool = List.of(
                new ItemStack(Items.DIAMOND)
        );
        return List.of(pool.get(random.nextInt(pool.size())).copy());
    }

    private static List<ItemStack> getEpicLoot(Random random) {
        List<ItemStack> pool = List.of(
                new ItemStack(Items.NETHERITE_INGOT)
        );
        return List.of(pool.get(random.nextInt(pool.size())).copy());
    }

    private static List<ItemStack> getLegendaryLoot(Random random) {
        List<ItemStack> pool = List.of(
                new ItemStack(Items.NETHER_STAR)
        );
        return List.of(pool.get(random.nextInt(pool.size())).copy());
    }

    private static List<ItemStack> getExoticLoot(Random random) {
        List<ItemStack> pool = List.of(
                new ItemStack(Items.ELYTRA)
        );
        return List.of(pool.get(random.nextInt(pool.size())).copy());
    }
}
