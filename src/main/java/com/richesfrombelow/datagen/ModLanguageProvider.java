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

        translationBuilder.add("item.richesfrombelow.plushie", "Plushie");
        translationBuilder.add(  "item.richesfrombelow.plushie.pattern", "%s Plushie");
        translationBuilder.add("item.richesfrombelow.plushie.k08", "K08_");

    }
}