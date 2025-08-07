package com.richesfrombelow.items.custom;

import com.richesfrombelow.component.ModDataComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

public class WishingStarItem extends Item {
    public static final int MAX_LEVEL = 30;

    public WishingStarItem(Settings settings) {
        super(settings.component(ModDataComponents.WISHING_LEVEL, 1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            final int increment = user.isSneaking() ? 5 : 1;
            int currentLevel = stack.getOrDefault(ModDataComponents.WISHING_LEVEL, 1);

            int nextLevel = (currentLevel - 1 + increment) % MAX_LEVEL + 1;

            stack.set(ModDataComponents.WISHING_LEVEL, nextLevel);
            user.sendMessage(Text.translatable("text.richesfrombelow.wishing_star.level_set", nextLevel), true);

            if (increment > 1) {
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 0.5f);
            } else {
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.4f, 1f);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            stack.set(ModDataComponents.WISHING_STAR_OWNER, player.getUuid());
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int level = stack.getOrDefault(ModDataComponents.WISHING_LEVEL, 1);
        tooltip.add(Text.translatable("tooltip.richesfrombelow.wishing_star.desired_level", level).formatted(Formatting.GRAY));
    }
}