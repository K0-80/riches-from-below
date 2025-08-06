package com.richesfrombelow.datagen;


import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {

        //K0BO coin
        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.MISC, ModItems.COIN_FRAGMENT, RecipeCategory.MISC, ModItems.KOBO_COIN);


        //gatcha machine
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.GACHA_MACHINE_BLOCK, 1)
                .pattern("GGG")
                .pattern("IGR")
                .pattern("IBI")
                .input('G', Items.GLASS)
                .input('B', Items.IRON_BLOCK)
                .input('B', Items.IRON_INGOT)
                .input('R', Items.REDSTONE)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.GACHA_MACHINE_BLOCK));
    }
}