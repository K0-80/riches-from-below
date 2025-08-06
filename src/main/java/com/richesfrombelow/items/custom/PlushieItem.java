package com.richesfrombelow.items.custom;

import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.component.PlushieVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class PlushieItem extends Item {
    public PlushieItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        PlushieVariant variant = stack.get(ModDataComponents.PLUSHIE_VARIANT);
        if (variant != null) {
            return Text.translatable(this.getTranslationKey() + ".pattern", Text.translatable(this.getTranslationKey() + "." + variant.id()));
        }
        return super.getName(stack);
    }


}