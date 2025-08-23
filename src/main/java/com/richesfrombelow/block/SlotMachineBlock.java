package com.richesfrombelow.block;

import com.mojang.serialization.MapCodec;
import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.entity.GachaMachineBlockEntity;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.block.entity.SlotMachineBlockEntity;
import com.richesfrombelow.items.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SlotMachineBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public static final MapCodec<SlotMachineBlock> CODEC = createCodec(SlotMachineBlock::new);

    public enum SlotResult {
        GREEN,
        PURPLE,
        YELLOW,
        RED;

        private static final SlotResult[] VALUES = values();
        private static final int SIZE = VALUES.length;

        public static SlotResult getRandomResult(Random random) {
            return VALUES[random.nextInt(SIZE)];
        }
    }

    public SlotMachineBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos basePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;

            if (world.getBlockEntity(basePos) instanceof SlotMachineBlockEntity     be && be.isIdle() && player.getStackInHand(player.getActiveHand()).isOf(ModItems.KOBO_COIN)) {
                    world.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM, SoundCategory.BLOCKS, 1.0f, 1.2f);
                    spinSlots(world, basePos, player);
                    if (!player.isCreative()) {
                        player.getStackInHand(player.getActiveHand()).decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
        }
        return ActionResult.PASS;
    }

    private void spinSlots(World world, BlockPos pos, PlayerEntity player) {
        SlotResult[] results = new SlotResult[3];
        results[0] = SlotResult.getRandomResult(world.getRandom());
        results[1] = SlotResult.getRandomResult(world.getRandom());
        results[2] = SlotResult.getRandomResult(world.getRandom());

        if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity be) {
            be.startSpin(results, player);
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SLOT_MACHINE, SlotMachineBlockEntity::tick);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (neighborState.isOf(this) && neighborState.get(HALF) != doubleBlockHalf) {
                return state.with(FACING, neighborState.get(FACING));
            }
            return Blocks.AIR.getDefaultState();
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                    .with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) != DoubleBlockHalf.UPPER) {
            return super.canPlaceAt(state, world, pos);
        }
        BlockState blockState = world.getBlockState(pos.down());
        return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!world.isClient && player.isCreative()) {
            DoubleBlockHalf half = state.get(HALF);
            BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(this) && otherState.get(HALF) != half) {
                world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.addBlockBreakParticles(otherPos, otherState);
            }
        }
        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER ? new SlotMachineBlockEntity(pos, state) : null;
    }
}