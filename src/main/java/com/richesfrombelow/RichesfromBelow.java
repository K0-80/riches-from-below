package com.richesfrombelow;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.command.ModCommands;
import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.items.ModItemGroups;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.util.ModLootTableModifiers;
import com.richesfrombelow.util.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.potion.Potions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichesfromBelow implements ModInitializer {
	public static final String MOD_ID = "richesfrombelow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModEntities.register();

		ModBlocks.register();
		ModBlockEntities.register();

		ModItems.register();
		ModItemGroups.register();

		ModDataComponents.register();

		ModLootTableModifiers.modifyLootTables();


		ModCommands.registerCommands();
		TaskScheduler.initialize();

		FabricBrewingRecipeRegistryBuilder.BUILD.register((builder -> { //potions
			builder.registerPotionRecipe(Potions.AWKWARD, ModItems.LUCKY_CLOVER, Potions.LUCK);
		}));
	}
}