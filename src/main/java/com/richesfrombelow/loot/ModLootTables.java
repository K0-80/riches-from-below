package com.richesfrombelow.loot;

import com.richesfrombelow.RichesfromBelow;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModLootTables {
    public static final RegistryKey<LootTable> FORTUNE_COOKIE_TREASURE_CHEST_KEY =
            RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(RichesfromBelow.MOD_ID, "chests/fortune_cookie_treasure"));

}