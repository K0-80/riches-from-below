package com.richesfrombelow.items.custom;

import com.google.common.collect.ImmutableMap;
import com.richesfrombelow.component.ModDataComponents;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

public class CrownOfGreedItem extends ArmorItem {
    public CrownOfGreedItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    public static final Map<Item, Integer> GOLD_VALUES;

    static {
        GOLD_VALUES = new ImmutableMap.Builder<Item, Integer>()

                .put(Items.GOLD_NUGGET, 1)
                .put(Items.RAW_GOLD, 7)
                .put(Items.GOLD_INGOT, 9)
                .put(Items.RAW_GOLD_BLOCK, 63)
                .put(Items.GOLD_BLOCK, 81)

                .put(Items.GOLDEN_SWORD, 18)
                .put(Items.GOLDEN_PICKAXE, 27)
                .put(Items.GOLDEN_AXE, 27)
                .put(Items.GOLDEN_SHOVEL, 9)
                .put(Items.GOLDEN_HOE, 18)

                .put(Items.GOLDEN_HELMET, 45)
                .put(Items.GOLDEN_CHESTPLATE, 72)
                .put(Items.GOLDEN_LEGGINGS, 63)
                .put(Items.GOLDEN_BOOTS, 36)
                .put(Items.GOLDEN_HORSE_ARMOR, 54)

                .build();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int goldConsumed = stack.getOrDefault(ModDataComponents.GOLD_CONSUMED, 0);
        tooltip.add(Text.translatable("item.richesfrombelow.crown_of_greed.tooltip.gold_consumed", goldConsumed).formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);    }
}
