package com.richesfrombelow.items.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class PacifistCrownItem extends ArmorItem {

    public PacifistCrownItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (entity instanceof PlayerEntity player) {
                if (player.getEquippedStack(Type.HELMET.getEquipmentSlot()) == stack) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 21, 2, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 21, 4, false, false, true));
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.pacifist_crown.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);}
}