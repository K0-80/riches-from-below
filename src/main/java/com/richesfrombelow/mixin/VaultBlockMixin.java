package com.richesfrombelow.mixin;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.PersonalVaultBlock;
import com.richesfrombelow.block.entity.PersonalVaultBlockEntity;
import com.richesfrombelow.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VaultBlock.class)
public class VaultBlockMixin {

    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.KOBO_COIN)) {
            Direction originalFacing = state.get(VaultBlock.FACING);
            Direction newFacing;
            if (originalFacing.getAxis().isVertical()) {
                newFacing = player.getHorizontalFacing().getOpposite();
            } else {
                newFacing = originalFacing;
            }

            world.setBlockState(pos, ModBlocks.PERSONAL_VAULT.getDefaultState().with(PersonalVaultBlock.FACING, newFacing), 3);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof PersonalVaultBlockEntity personalVaultBlockEntity) {
                personalVaultBlockEntity.setOwner(player);
            }

            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(Hand.MAIN_HAND).decrement(1);
            }

            world.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM_FAIL, SoundCategory.BLOCKS, 0.8f, 0.8f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GOLD_BLOCK.getDefaultState()),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.0);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}