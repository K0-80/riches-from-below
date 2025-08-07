package com.richesfrombelow.items.custom;

import com.richesfrombelow.items.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class AllInItem extends Item {
    public AllInItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (!world.isClient()) {
            PlayerInventory inventory = user.getInventory();
            int coinCount = 0;

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isOf(ModItems.KOBO_COIN)) {
                    coinCount += stack.getCount();
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }

            if (coinCount > 0) {
                int duration = coinCount * 20;

                // Amplifier scales with coin count
                int strengthAmp = Math.min(4, coinCount / 16); // max Strength V (amp 4)
                int speedAmp = Math.min(4, coinCount / 16); // max Speed V (amp 4)
                int resistanceAmp = Math.min(3, coinCount / 32); // max Resistance IV (amp 3)
                int regenerationAmp = Math.min(3, coinCount / 32); // max Regeneration IV (amp 3)

                user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, strengthAmp, false, true, true));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, speedAmp, false, true, true));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, resistanceAmp, false, true, true));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, duration, regenerationAmp, false, true, true));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, duration, 0, false, true, true));

                if (!user.getAbilities().creativeMode) {heldStack.decrement(1);}

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);

                return TypedActionResult.success(heldStack);
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.richesfrombelow.all_in_item").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}