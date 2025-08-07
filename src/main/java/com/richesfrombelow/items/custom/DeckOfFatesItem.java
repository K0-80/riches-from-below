package com.richesfrombelow.items.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class DeckOfFatesItem extends Item {
    public DeckOfFatesItem(Settings settings) {
        super(settings);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.deck_of_fates.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);    }
}
