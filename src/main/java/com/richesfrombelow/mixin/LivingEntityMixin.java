package com.richesfrombelow.mixin;

import com.richesfrombelow.items.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "dropLoot", at = @At("TAIL")) //collector's suitcase
    private void richesfrombelow_dropCoinFragmentOnKill(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity killedEntity = (LivingEntity) (Object) this;
        if (causedByPlayer && source.getAttacker() instanceof PlayerEntity player) {
            if (player.getInventory().contains(new ItemStack(ModItems.COLLECTOR_SUITCASE))) {
                if (killedEntity.getWorld().random.nextFloat() < 0.7f) { // 2%
                    World world = killedEntity.getWorld();
                    killedEntity.dropItem(ModItems.COIN_FRAGMENT);
                    if (!world.isClient()) {
                        killedEntity.dropItem(ModItems.COIN_FRAGMENT);

                        world.playSound(
                                null,
                                killedEntity.getBlockPos(),
                                SoundEvents.BLOCK_CHAIN_BREAK,
                                SoundCategory.PLAYERS,
                                1F,
                                2F
                        );

                        ServerWorld serverWorld = (ServerWorld) world;
                        serverWorld.spawnParticles(
                                new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(ModItems.KOBO_COIN)),
                                killedEntity.getX(),
                                killedEntity.getBodyY(0.5D),
                                killedEntity.getZ(),
                                15,
                                0.5,
                                1,
                                0.5,
                                0.1
                        );
                    }
                }
            }
        }
    }
}