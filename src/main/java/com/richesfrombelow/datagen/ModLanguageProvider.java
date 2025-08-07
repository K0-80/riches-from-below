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
        translationBuilder.add("text.richesfrombelow.card.default", "what the hell how did u even do this??????");
        translationBuilder.add("text.richesfrombelow.card.king", "The King: Your reign begins. The realm provides for its ruler.");
        translationBuilder.add("text.richesfrombelow.card.sun", "The Sun: Bask in the light of dawn.");
        translationBuilder.add("text.richesfrombelow.card.moon", "The Moon: The night is your guide.");
        translationBuilder.add("text.richesfrombelow.card.devil", "The Devil: May our contract serve you well.");
        translationBuilder.add("text.richesfrombelow.card.tower", "The Tower: What was built on hubris shall fall to ruin.");

        translationBuilder.add(ModItems.WISHING_STAR, "Wishing Star");
        translationBuilder.add("tooltip.richesfrombelow.wishing_star.desired_level", "Desired Level: %s");
        translationBuilder.add("text.richesfrombelow.wishing_star.level_set", "Set to %s Levels");
        translationBuilder.add("text.richesfrombelow.wishing_star.success", "Your wish has been granted! You gain %s levels!");
        translationBuilder.add("text.richesfrombelow.wishing_star.failure", "The star fizzles out, draining your experience...");



        translationBuilder.add("item.richesfrombelow.plushie", "Plushie");
        translationBuilder.add(  "item.richesfrombelow.plushie.pattern", "%s Plushie");
        translationBuilder.add("item.richesfrombelow.plushie.k08", "K08_");

    }
}