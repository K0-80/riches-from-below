package com.richesfrombelow;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.items.ModItemGroups;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.api.ModInitializer;

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



	}
}