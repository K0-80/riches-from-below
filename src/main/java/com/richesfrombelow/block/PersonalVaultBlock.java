package com.richesfrombelow.block;

import com.mojang.serialization.MapCodec;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.block.entity.PersonalVaultBlockEntity;
import com.richesfrombelow.items.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PersonalVaultBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public PersonalVaultBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(LOCKED, true).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(PersonalVaultBlock::new);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LOCKED, FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer instanceof PlayerEntity player) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PersonalVaultBlockEntity personalVaultBlockEntity) {
                personalVaultBlockEntity.setOwner(player);
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PersonalVaultBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PersonalVaultBlockEntity) {
                ItemScatterer.spawn(world, pos, (PersonalVaultBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public float getBlastResistance() {
        return 1200.0F;
    }


    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return List.of(new ItemStack(this));
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, net.minecraft.world.BlockView world, BlockPos pos) {
        if (state.get(LOCKED)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PersonalVaultBlockEntity be) {
                if (be.getOwnerUuid() != null && !player.getUuid().equals(be.getOwnerUuid()) && !player.isCreative()) {
                    return 0.0f;
                }
            }
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PersonalVaultBlockEntity be)) {
            return ActionResult.PASS;
        }

        // Sneaking Logic: Always check time for any player
        if (player.isSneaking()) {
            world.playSound(null, pos, SoundEvents.BLOCK_VAULT_DEACTIVATE, SoundCategory.BLOCKS, 0.5f, 1.2f);
            if (state.get(LOCKED)) {
                long remainingTicks = be.getRemainingLockTime(world);
                long totalSeconds = remainingTicks / 20;
                long hours = totalSeconds / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                player.sendMessage(Text.translatable("block.richesfrombelow.personal_vault.time_remaining", hours, minutes).formatted(Formatting.GRAY), true);
            } else {
                player.sendMessage(Text.translatable("block.richesfrombelow.personal_vault.unlocked").formatted(Formatting.GRAY), true);
            }
            return ActionResult.SUCCESS;
        }

        // Normal Use Logic
        boolean isOwner = be.getOwnerUuid() != null && player.getUuid().equals(be.getOwnerUuid());
        ItemStack handStack = player.getMainHandStack();
        boolean isLocked = state.get(LOCKED);

        // Case 1: Player is the owner
        if (isOwner) {
            // Sub-case: Owner is holding a coin fragment to add time
            if (handStack.isOf(ModItems.COIN_FRAGMENT)) {
                world.setBlockState(pos, state.with(LOCKED, true), 3);
                be.addLockTime(world);
                if (!player.isCreative()) {
                    handStack.decrement(1);
                }

                world.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM, SoundCategory.BLOCKS, 1.0f, 1.2f);

                long remainingTicks = be.getRemainingLockTime(world);
                long totalSeconds = remainingTicks / 20;
                long hours = totalSeconds / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                player.sendMessage(Text.translatable("block.richesfrombelow.personal_vault.time_extended", hours, minutes).formatted(Formatting.GRAY), true);

                return ActionResult.SUCCESS;
            } else {
                // Sub-case: Owner is opening the vault
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
                player.openHandledScreen(be);
                return ActionResult.CONSUME;
            }
        }

        // Case 2: Player is not the owner
        if (isLocked) {
            world.playSound(null, pos, SoundEvents.BLOCK_VAULT_CLOSE_SHUTTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
            player.sendMessage(Text.translatable("block.richesfrombelow.personal_vault.locked_by", be.getOwnerName(world)).formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        } else {
            // Unlocked, so anyone can open it
            world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
            player.openHandledScreen(be);
            return ActionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.PERSONAL_VAULT_BLOCK_ENTITY,
                PersonalVaultBlockEntity::tick);
    }
}