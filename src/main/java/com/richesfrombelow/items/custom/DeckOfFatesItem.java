package com.richesfrombelow.items.custom;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.networking.packet.DrawCardS2CPacket;
import com.richesfrombelow.util.TaskScheduler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckOfFatesItem extends Item {

    private enum Card {
        MOON, SUN, KING, TOWER, DEVIL
    }

    public DeckOfFatesItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        user.getItemCooldownManager().set(this, 20 * 60 * 5);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 2F, 2F);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 2F, 2F);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 2F, 2F);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 2F, 2F);


        if (!world.isClient) {
            itemStack.damage(1, user, PlayerEntity.getSlotForHand(hand));

            ServerWorld serverWorld = (ServerWorld) world;
            Card[] cards = Card.values();
            Card drawnCard = cards[world.getRandom().nextInt(cards.length)];

            RichesfromBelow.LOGGER.info("{} drew the '{}' card", user.getName().getString(), drawnCard.name());
            applyCardEffect(serverWorld, user, drawnCard);

            ServerPlayNetworking.send((ServerPlayerEntity) user, new DrawCardS2CPacket(drawnCard.ordinal()));
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    private void applyCardEffect(ServerWorld world, PlayerEntity user, Card card) {
        Text message = Text.translatable("text.richesfrombelow.card.default").formatted(Formatting.GRAY);
        switch (card) {
            case KING -> {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_VINDICATOR_CELEBRATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 12000, 4)); // 10 mins
                for (int i = 0; i < 3; i++) {
                    ItemEntity coin = new ItemEntity(world, user.getX(), user.getY() + 0.5, user.getZ(), new ItemStack(ModItems.KOBO_COIN));
                    world.spawnEntity(coin);
                }
                message = Text.translatable("text.richesfrombelow.card.king").formatted(Formatting.GOLD);

            }
            case SUN -> {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.8f, 1.2f);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 3600, 1)); // 3 mins
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600, 0)); // 3 mins
                message = Text.translatable("text.richesfrombelow.card.sun").formatted(Formatting.GOLD);
            }
            case MOON -> {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.PLAYERS, 1.0f, 1.2f);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 6000, 0)); // 5 mins
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 6000, 0)); // 5 mins
                message = Text.translatable("text.richesfrombelow.card.moon").formatted(Formatting.GRAY);
            }
            case DEVIL -> {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 4)); // 1 min
                user.setHealth(user.getHealth() / 10.0f);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 0.7f);
                message = Text.translatable("text.richesfrombelow.card.devil").formatted(Formatting.RED);
            }
            case TOWER -> {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.2f, 1.0f);
                handleTowerEffect(world, user);
                message = Text.translatable("text.richesfrombelow.card.tower").formatted(Formatting.RED);
            }
        }
        user.sendMessage(message, true); // Send the message to the action bar
    }

    private void handleTowerEffect(ServerWorld world, PlayerEntity user) {
        Inventory inventory = user.getInventory();
        List<Integer> validSlots = new ArrayList<>();
        for (int i = 9; i < 36; i++) { // Main inventory, no hotbar
            if (!inventory.getStack(i).isEmpty()) {
                validSlots.add(i);
            }
        }

        if (!validSlots.isEmpty()) {
            Collections.shuffle(validSlots);
            int slotToDrop = validSlots.get(0);
            ItemStack stackToDrop = inventory.getStack(slotToDrop).copy();
            inventory.setStack(slotToDrop, ItemStack.EMPTY);

            ItemEntity itemEntity = new ItemEntity(world, user.getX(), user.getY(), user.getZ(), stackToDrop);
            itemEntity.setPickupDelay(200); // 10 seconds
            world.spawnEntity(itemEntity);

            Vec3d dropPosition = itemEntity.getPos();
            TaskScheduler.schedule(100, () -> { // 5 seconds
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(dropPosition);
                world.spawnEntity(lightning);
            });
        }
    }

    @Environment(EnvType.CLIENT)
    public static void drawCardAnimation(int cardId) {
        ItemStack cardStack = new ItemStack(ModItems.DECK_OF_FATES_ANIMATION);
        cardStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(cardId));

        MinecraftClient.getInstance().gameRenderer.showFloatingItem(cardStack);

        String cardName = getCardNameById(cardId);
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            RichesfromBelow.LOGGER.info("Animating '{}' card (ID: {}) for {}", cardName, cardId, player.getName().getString());
        }
    }

    private static String getCardNameById(int id) {
        return switch (id) {
            case 0 -> "Moon";
            case 1 -> "Sun";
            case 2 -> "King";
            case 3 -> "Tower";
            case 4 -> "Devil";
            default -> "Unknown";
        };
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.deck_of_fates.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}