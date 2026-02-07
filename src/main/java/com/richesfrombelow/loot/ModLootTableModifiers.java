package com.richesfrombelow.loot;

import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {
    private static final Identifier GRASS_ID = Identifier.of("minecraft", "blocks/short_grass");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (GRASS_ID.equals(key.getValue()) && source.isBuiltin()) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.HOES))) // Must use a hoe
                        .conditionally(RandomChanceLootCondition.builder(0.01f))  //1% chance = 0.01
                        .with(ItemEntry.builder(ModItems.LUCKY_CLOVER));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}