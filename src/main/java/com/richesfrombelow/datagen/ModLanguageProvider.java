package com.richesfrombelow.datagen;


import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add("itemgroup.riches.riches_items", "Riches from Below");

        translationBuilder.add(ModItems.GATCHA_BALL_ITEM, "Gatcha Ball");
        translationBuilder.add(ModEntities.GATCHA_BALL, "Gatcha Ball");
        translationBuilder.add(ModBlocks.GACHA_MACHINE_BLOCK, "Gatcha Machine");

        translationBuilder.add(ModItems.KOBO_COIN, "K0BO Coin™");
        translationBuilder.add(ModItems.COIN_FRAGMENT, "Coin Fragment");

        translationBuilder.add(ModItems.CROWN_OF_GREED, "Crown of Greed");
        translationBuilder.add("item.richesfrombelow.crown_of_greed.tooltip.gold_consumed", "Value: %s");

        translationBuilder.add(ModItems.PACIFIST_CROWN, "Pacifist Crown");

        translationBuilder.add(ModItems.ALL_IN, "ALL IN!");
        translationBuilder.add("tooltip.richesfrombelow.all_in_item", "Consumes ALL Kobo Coins in inventory.");

        translationBuilder.add(ModItems.COLLECTOR_SUITCASE, "Collector's Suitcase");
        translationBuilder.add("item.richesfrombelow.collector_suitcase.tooltip", "Creatures killed may drop some coin fragments.");

        translationBuilder.add(ModItems.DECK_OF_FATES, "Deck of Fates");
        translationBuilder.add("item.richesfrombelow.deck_of_fates.tooltip", "Right click to draw a card");



        translationBuilder.add("item.richesfrombelow.plushie", "Plushie");
        translationBuilder.add(  "item.richesfrombelow.plushie.pattern", "%s Plushie");
        translationBuilder.add("item.richesfrombelow.plushie.k08", "K08_");

    }
}