package com.richesfrombelow.items.custom;

import com.richesfrombelow.component.ModDataComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        super(settings.component(ModDataComponents.WISHING_LEVEL, 0));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            if (!world.isClient) {
                final int increment = 5;
                int currentLevel = stack.getOrDefault(ModDataComponents.WISHING_LEVEL, 0);
                int nextLevel = (currentLevel - 1 + increment) % MAX_LEVEL + 1;

                stack.set(ModDataComponents.WISHING_LEVEL, nextLevel);
                user.sendMessage(Text.translatable("text.richesfrombelow.wishing_star.level_set", nextLevel).formatted(Formatting.GRAY), true);
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 0.5f);
            }
            return TypedActionResult.success(stack, world.isClient());
        }


        if (!world.isClient) {
            if (user instanceof ServerPlayerEntity serverPlayer) {
                ServerWorld serverWorld = (ServerWorld) world;
                int desiredLevel = stack.getOrDefault(ModDataComponents.WISHING_LEVEL, 1);
                double successChance = 1.0 - (desiredLevel / 35.0);

                if (serverWorld.getRandom().nextFloat() < successChance) {
                    serverPlayer.addExperienceLevels(desiredLevel);
                    serverPlayer.sendMessage(Text.translatable("text.richesfrombelow.wishing_star.success", desiredLevel).formatted(Formatting.GOLD), true);
                    serverWorld.playSound(null, serverPlayer.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                } else {
                    serverPlayer.setExperienceLevel(0);
                    serverPlayer.setExperiencePoints(0);
                    serverPlayer.sendMessage(Text.translatable("text.richesfrombelow.wishing_star.failure").formatted(Formatting.RED), true);
                    serverWorld.playSound(null, serverPlayer.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    serverWorld.spawnParticles(ParticleTypes.SMOKE, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                }

                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }




    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int level = stack.getOrDefault(ModDataComponents.WISHING_LEVEL, 0);
        tooltip.add(Text.translatable("tooltip.richesfrombelow.wishing_star.desired_level", level).formatted(Formatting.GRAY));
    }
}