package com.richesfrombelow.datagen;


import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModModelProvider extends FabricModelProvider    {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.GATCHA_BALL_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.KOBO_COIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.COIN_FRAGMENT, Models.GENERATED);

    }
}
