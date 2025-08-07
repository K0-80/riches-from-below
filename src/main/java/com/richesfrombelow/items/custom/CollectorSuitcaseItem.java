package com.richesfrombelow.items.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CollectorSuitcaseItem extends Item  {
    public CollectorSuitcaseItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.collector_suitcase.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);    }
}
