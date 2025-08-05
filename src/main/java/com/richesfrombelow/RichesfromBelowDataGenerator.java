package com.richesfrombelow;

import com.richesfrombelow.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RichesfromBelowDataGenerator implements DataGeneratorEntrypoint {
	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen	//i hate daagen hate data gen
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModRegistryDataGenerator::new);
		pack.addProvider(ModLanguageProvider::new);

	}
}
