package com.richesfrombelow.datagen;


import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.GACHA_MACHINE_BLOCK, 1)
                .pattern("GG ")
                .pattern("GR ")
                .pattern("II ")
                .input('G', Items.GLASS)
                .input('I', Items.IRON_BLOCK)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.GACHA_MACHINE_BLOCK));

        //slot machine
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.SLOT_MACHINE, 1)
                .pattern("IG ")
                .pattern("IL ")
                .pattern("II ")
                .input('I', Items.IRON_BLOCK)
                .input('G', Items.GOLD_BLOCK)
                .input('L', Items.LEVER)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .criterion(hasItem(Items.GOLD_BLOCK), conditionsFromItem(Items.GOLD_BLOCK))
                .offerTo(recipeExporter, RecipeProvider.getRecipeName(ModBlocks.SLOT_MACHINE));
    }
}