package com.richesfrombelow.items;

import com.richesfrombelow.RichesfromBelow;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> CROWN_OF_GREED = register(
            "crown_of_greed",
            Map.of(ArmorItem.Type.HELMET, 2),
            40,
            SoundEvents.ITEM_ARMOR_EQUIP_GOLD,
            () -> Ingredient.ofItems(Items.GOLD_INGOT),
            0.0f,
            0.0f
    );

    public static final RegistryEntry<ArmorMaterial> PACIFIST_CROWN = register(
            "pacifist_crown",
            Map.of(ArmorItem.Type.HELMET, 2),
            40,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(Items.LEATHER),
            0.0f,
            0.0f
    );

    private static RegistryEntry<ArmorMaterial> register(String name, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResistance) {
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(Identifier.of(RichesfromBelow.MOD_ID, name)));
        var material = new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance);
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(RichesfromBelow.MOD_ID, name), material);
    }

    public static void registerModArmorMaterials() {
    }
}
