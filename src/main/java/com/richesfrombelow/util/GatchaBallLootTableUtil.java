package com.richesfrombelow.util;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
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
                new ItemStack(Items.IRON_INGOT, 2)  //can be more then 1 item

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
