package com.richesfrombelow.loot;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class ModChestLootInjector {


    private record Rates(float fragChance, float coinChance, float fragMin, float fragMax, float coinMin, float coinMax) {}

    private static final Map<Identifier, Rates> CHESTS = Map.ofEntries(

            Map.entry(id("minecraft","chests/simple_dungeon"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/abandoned_mineshaft"), new Rates(0.02f, 0.004f, 1, 4, 1, 1)),
            Map.entry(id("minecraft","chests/shipwreck_supply"), new Rates(0.01f, 0.002f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/shipwreck_map"), new Rates(0.01f, 0.002f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/shipwreck_treasure"), new Rates(0.02f, 0.004f, 2, 5, 1, 1)),
            Map.entry(id("minecraft","chests/pillager_outpost"), new Rates(0.02f, 0.004f, 1, 4, 1, 1)),

            Map.entry(id("minecraft","chests/desert_pyramid"), new Rates(0.03f, 0.007f, 2, 5, 1, 1)),
            Map.entry(id("minecraft","chests/jungle_temple"), new Rates(0.03f, 0.007f, 2, 5, 1, 1)),
            Map.entry(id("minecraft","chests/igloo_chest"), new Rates(0.02f, 0.004f, 1, 4, 1, 1)),

            Map.entry(id("minecraft","chests/stronghold_corridor"), new Rates(0.03f, 0.008f, 2, 5, 1, 1)),
            Map.entry(id("minecraft","chests/stronghold_crossing"), new Rates(0.03f, 0.008f, 2, 5, 1, 1)),
            Map.entry(id("minecraft","chests/stronghold_library"), new Rates(0.03f, 0.01f, 2, 6, 1, 2)),

            Map.entry(id("minecraft","chests/underwater_ruin_small"), new Rates(0.02f, 0.004f, 1, 4, 1, 1)),
            Map.entry(id("minecraft","chests/underwater_ruin_big"), new Rates(0.02f, 0.004f, 1, 4, 1, 1)),

            Map.entry(id("minecraft","chests/village/village_armorer"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_toolsmith"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_weaponsmith"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_cartographer"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_mason"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_temple"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_shepherd"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_fisher"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_butcher"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_fletcher"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_tannery"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_desert_house"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_plains_house"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_savanna_house"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_taiga_house"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),
            Map.entry(id("minecraft","chests/village/village_snowy_house"), new Rates(0.02f, 0.004f, 1, 3, 1, 1)),

            Map.entry(id("minecraft","chests/ruined_portal"), new Rates(0.04f, 0.01f, 2, 6, 1, 2)),
            Map.entry(id("minecraft","chests/nether_bridge"), new Rates(0.04f, 0.01f, 2, 6, 1, 2)),
            Map.entry(id("minecraft","chests/trial_chambers/reward"), new Rates(0.05f, 0.015f, 3, 7, 1, 2)),
            Map.entry(id("minecraft","chests/trail_ruins_rare"), new Rates(0.05f, 0.015f, 3, 7, 1, 2)),
            Map.entry(id("minecraft","chests/trail_ruins_common"), new Rates(0.03f, 0.007f, 2, 5, 1, 1)),

            Map.entry(id("minecraft","chests/buried_treasure"), new Rates(0.05f, 0.015f, 2, 8, 1, 2)),
            Map.entry(id("minecraft","chests/bastion_bridge"), new Rates(0.06f, 0.02f, 3, 8, 1, 3)),
            Map.entry(id("minecraft","chests/bastion_hoglin_stable"), new Rates(0.06f, 0.02f, 3, 8, 1, 3)),
            Map.entry(id("minecraft","chests/bastion_other"), new Rates(0.06f, 0.02f, 3, 8, 1, 3)),
            Map.entry(id("minecraft","chests/bastion_treasure"), new Rates(0.08f, 0.03f, 4, 10, 1, 4)),
            Map.entry(id("minecraft","chests/end_city_treasure"), new Rates(0.10f, 0.04f, 4, 10, 2, 5)),
            Map.entry(id("minecraft","chests/ancient_city"), new Rates(0.12f, 0.05f, 5, 12, 2, 6)),
            Map.entry(id("minecraft","chests/woodland_mansion"), new Rates(0.07f, 0.02f, 3, 9, 1, 3))
    );

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier tableId = key.getValue();
            Rates r = CHESTS.get(tableId);
            if (r == null) return;

            tableBuilder.pool(LootPool.builder()
                    .conditionally(RandomChanceLootCondition.builder(r.fragChance))
                    .with(ItemEntry.builder(ModItems.COIN_FRAGMENT)
                            .apply(SetCountLootFunction.builder(
                                    UniformLootNumberProvider.create(r.fragMin, r.fragMax)
                            )))
            );

            tableBuilder.pool(LootPool.builder()
                    .conditionally(RandomChanceLootCondition.builder(r.coinChance))
                    .with(ItemEntry.builder(ModItems.KOBO_COIN)
                            .apply(SetCountLootFunction.builder(
                                    UniformLootNumberProvider.create(r.coinMin, r.coinMax)
                            )))
            );

            RichesfromBelow.LOGGER.info("Injected currency into {}", tableId);
        });
    }

    private static Identifier id(String ns, String path) {
        return Identifier.of(ns, path);
    }

    private ModChestLootInjector() {}
}
