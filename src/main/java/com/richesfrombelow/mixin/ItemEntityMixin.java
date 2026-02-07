package com.richesfrombelow.mixin;

import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.items.ModItems;
import com.richesfrombelow.items.custom.CrownOfGreedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;



@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }









































    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void richesfrombelow$onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        ItemStack itemStack = this.getStack();
        Item item = itemStack.getItem();

        if (CrownOfGreedItem.GOLD_VALUES.containsKey(item)) {
            ItemStack helmetStack = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);

            if (helmetStack.isOf(ModItems.CROWN_OF_GREED)) {
                int goldValue = CrownOfGreedItem.GOLD_VALUES.get(item);
                int valueToAdd = itemStack.getCount() * goldValue;
                int currentConsumedGold = helmetStack.getOrDefault(ModDataComponents.GOLD_CONSUMED, 0);

                helmetStack.set(ModDataComponents.GOLD_CONSUMED, currentConsumedGold + valueToAdd);

                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.PLAYERS,
                        0.2F,
                        ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_ARMOR_EQUIP_GOLD,
                        SoundCategory.PLAYERS,
                        0.6F, 2f
                );

                self.discard();
                ci.cancel();
            }
        }
    }
}