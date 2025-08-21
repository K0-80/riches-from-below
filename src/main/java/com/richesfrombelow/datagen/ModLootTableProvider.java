package com.richesfrombelow.datagen;


import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.loot.ModLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
       addDrop(ModBlocks.GACHA_MACHINE_BLOCK);
       addDrop(ModBlocks.PERSONAL_VAULT);
    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(ModLootTables.FORTUNE_COOKIE_TREASURE_CHEST_KEY,
                LootTable.builder()

                        .pool(LootPool.builder()
                                .rolls(UniformLootNumberProvider.create(5,8))
                                .with(ItemEntry.builder(ModItems.COIN_FRAGMENT)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2,5))))
                        )
                        .pool(LootPool.builder()
                                .rolls(UniformLootNumberProvider.create(1,3))
                                .with(ItemEntry.builder(ModItems.KOBO_COIN)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
                        )
                        .pool(LootPool.builder()
                                .rolls(UniformLootNumberProvider.create(5,10))
                                .with(ItemEntry.builder(Items.DIAMOND).weight(10)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                                .with(ItemEntry.builder(Items.EMERALD).weight(10)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                                .with(ItemEntry.builder(Items.GOLD_BLOCK).weight(8))
                                .with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
                                .with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(1))
                        )
        );
    }
}


