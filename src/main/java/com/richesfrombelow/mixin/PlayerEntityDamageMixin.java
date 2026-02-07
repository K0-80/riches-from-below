
package com.richesfrombelow.mixin;

import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.items.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDamageMixin {

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float richesfrombelow$modifyDamageOnCrownWearer(float amount, DamageSource source) { //crown of greed reduce/incress damage
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);

        if (helmet.isOf(ModItems.CROWN_OF_GREED)) {
            int goldConsumed = helmet.getOrDefault(ModDataComponents.GOLD_CONSUMED, 0);
            int damageCost = (int) Math.ceil(amount);

            if (damageCost <= 0) {
                return amount;
            }

            if (goldConsumed >= damageCost) {
                helmet.set(ModDataComponents.GOLD_CONSUMED, goldConsumed - damageCost);

                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BLOCK_SNIFFER_EGG_CRACK, SoundCategory.PLAYERS,
                            0.6F,
                            1.5F + player.getRandom().nextFloat() * 0.4F
                    );
                    serverWorld.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.PLAYERS,
                            0.8F,
                            1.5F + player.getRandom().nextFloat() * 0.4F
                    );

                    int particleCount = Math.min(30, damageCost * 5);
                    serverWorld.spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.RAW_GOLD_BLOCK.getDefaultState()),
                            player.getX(),
                            player.getY() + player.getHeight() / 2.0,
                            player.getZ(),
                            particleCount,
                            player.getWidth() / 2.0f,
                            player.getHeight() / 2.0f,
                            player.getWidth() / 2.0f,
                            0.15
                    );
                }

                return amount * 0.5f;
            } else {
                return amount * 2.0f;
            }
        }
        return amount;
    }
}