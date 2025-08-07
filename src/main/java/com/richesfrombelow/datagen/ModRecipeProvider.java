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


        // gacha machine
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.GACHA_MACHINE_BLOCK, 1)
                .pattern("GG ")
                .pattern("GR ")
                .pattern("II ")
                .input('G', Items.GLASS)
                .input('I', Items.IRON_BLOCK)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.GACHA_MACHINE_BLOCK));
    }
}