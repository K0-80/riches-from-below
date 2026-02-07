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
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
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

        Identifier FRAG_TO_COIN = Identifier.of(RichesfromBelow.MOD_ID, "kobo_coin_from_fragments");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.KOBO_COIN, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.COIN_FRAGMENT)
                .criterion("has_coin_fragment", conditionsFromItem(ModItems.COIN_FRAGMENT))
                .offerTo(recipeExporter, FRAG_TO_COIN);
        Identifier COIN_TO_FRAG = Identifier.of(RichesfromBelow.MOD_ID, "coin_fragments_from_kobo_coin");

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COIN_FRAGMENT, 9)
                .input(ModItems.KOBO_COIN)
                .criterion("has_kobo_coin", conditionsFromItem(ModItems.KOBO_COIN))
                .offerTo(recipeExporter, COIN_TO_FRAG);

        Identifier SLOT_RECIPE_ID = Identifier.of(RichesfromBelow.MOD_ID, "slot_machine");
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.GACHA_MACHINE_BLOCK, 1)
                .pattern("GG ")
                .pattern("GR ")
                .pattern("II ")
                .input('G', Items.GLASS)
                .input('I', Items.IRON_BLOCK)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(recipeExporter, SLOT_RECIPE_ID);

        Identifier GACHA_RECIPE_ID = Identifier.of(RichesfromBelow.MOD_ID, "gacha_machine_block");
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.SLOT_MACHINE, 1)
                .pattern("IG ")
                .pattern("IL ")
                .pattern("II ")
                .input('I', Items.IRON_BLOCK)
                .input('G', Items.GOLD_BLOCK)
                .input('L', Items.LEVER)
                .criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(recipeExporter, GACHA_RECIPE_ID);
    }
}