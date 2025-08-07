package com.richesfrombelow.items.custom;

import com.richesfrombelow.util.TaskScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class LiquidGoldItem extends Item {
    private static final int MAX_RADIUS = 3;
    private static final int RING_DELAY_TICKS = 10;

    private static Optional<Block> getGravelConversion() {
        double r = Math.random();
        if (r < 0.10) return Optional.of(Blocks.IRON_ORE);    // 10%
        if (r < 0.35) return Optional.of(Blocks.COAL_ORE);    // 25%
        return Optional.empty(); // 65% chance to do nothing
    }

    private static Optional<Block> getStoneConversion() {
        double r = Math.random();
        if (r < 0.05) return Optional.of(Blocks.GOLD_ORE);    // 5%
        if (r < 0.20) return Optional.of(Blocks.IRON_ORE);    // 15%
        if (r < 0.40) return Optional.of(Blocks.COPPER_ORE);  // 20%
        if (r < 0.70) return Optional.of(Blocks.COAL_ORE);    // 30%
        return Optional.empty(); // 30% chance to do nothing
    }

    private static Optional<Block> getDeepslateConversion() {
        double r = Math.random();
        if (r < 0.02) return Optional.of(Blocks.DEEPSLATE_DIAMOND_ORE); // 2%
        if (r < 0.05) return Optional.of(Blocks.DEEPSLATE_EMERALD_ORE); // 3%
        if (r < 0.12) return Optional.of(Blocks.DEEPSLATE_GOLD_ORE);   // 7%
        if (r < 0.25) return Optional.of(Blocks.DEEPSLATE_IRON_ORE);   // 13%
        if (r < 0.45) return Optional.of(Blocks.DEEPSLATE_COPPER_ORE); // 20%
        if (r < 0.75) return Optional.of(Blocks.DEEPSLATE_COAL_ORE);   // 30%
        return Optional.empty(); // 25% chance to do nothing
    }

    private static Optional<Block> getNetherrackConversion() {
        double r = Math.random();
        if (r < 0.02) return Optional.of(Blocks.ANCIENT_DEBRIS);     // 2%
        if (r < 0.25) return Optional.of(Blocks.NETHER_GOLD_ORE);    // 23%
        if (r < 0.60) return Optional.of(Blocks.NETHER_QUARTZ_ORE);  // 35%
        return Optional.empty(); // 40% chance to do nothing
    }

    private static final Map<Block, Supplier<Optional<Block>>> CONVERSION_MAP = Map.ofEntries(
            // --- OVERWORLD SOIL & STONE ---
            Map.entry(Blocks.DIRT, () -> Optional.of(Blocks.GRASS_BLOCK)),
            Map.entry(Blocks.COARSE_DIRT, () -> Optional.of(Blocks.DIRT)),
            Map.entry(Blocks.ROOTED_DIRT, () -> Optional.of(Blocks.MOSS_BLOCK)),
            Map.entry(Blocks.GRAVEL, LiquidGoldItem::getGravelConversion),
            Map.entry(Blocks.SAND, () -> Optional.of(Blocks.GLASS)),
            Map.entry(Blocks.RED_SAND, () -> Optional.of(Blocks.GLASS)),
            Map.entry(Blocks.CLAY, () -> Optional.of(Blocks.TERRACOTTA)),
            Map.entry(Blocks.STONE, LiquidGoldItem::getStoneConversion),
            Map.entry(Blocks.COBBLESTONE, LiquidGoldItem::getStoneConversion),
            Map.entry(Blocks.ANDESITE, LiquidGoldItem::getStoneConversion),
            Map.entry(Blocks.DIORITE, LiquidGoldItem::getStoneConversion),
            Map.entry(Blocks.GRANITE, LiquidGoldItem::getStoneConversion),
            Map.entry(Blocks.DEEPSLATE, LiquidGoldItem::getDeepslateConversion),
            Map.entry(Blocks.COBBLED_DEEPSLATE, LiquidGoldItem::getDeepslateConversion),
            Map.entry(Blocks.TUFF, LiquidGoldItem::getDeepslateConversion),

            // --- NETHER ---
            Map.entry(Blocks.NETHERRACK, LiquidGoldItem::getNetherrackConversion),
            Map.entry(Blocks.SOUL_SAND, () -> Optional.of(Blocks.SOUL_SOIL)),
            Map.entry(Blocks.SOUL_SOIL, () -> Optional.of(Blocks.NETHER_WART_BLOCK)),
            Map.entry(Blocks.BASALT, () -> Optional.of(Blocks.SMOOTH_BASALT)),
            Map.entry(Blocks.SMOOTH_BASALT, () -> Optional.of(Blocks.MAGMA_BLOCK)),
            Map.entry(Blocks.BLACKSTONE, () -> Optional.of(Blocks.GILDED_BLACKSTONE)),

            // --- THE END ---
            Map.entry(Blocks.END_STONE, () -> Optional.of(Blocks.END_STONE_BRICKS)),
            Map.entry(Blocks.END_STONE_BRICKS, () -> Optional.of(Blocks.PURPUR_BLOCK)),

            // --- ICE & SNOW ---
            Map.entry(Blocks.SNOW_BLOCK, () -> Optional.of(Blocks.ICE)),
            Map.entry(Blocks.ICE, () -> Optional.of(Blocks.PACKED_ICE)),
            Map.entry(Blocks.PACKED_ICE, () -> Optional.of(Blocks.BLUE_ICE)),

            // --- ORE UPGRADES ---
            Map.entry(Blocks.COAL_ORE, () -> Optional.of(Blocks.IRON_ORE)),
            Map.entry(Blocks.DEEPSLATE_COAL_ORE, () -> Optional.of(Blocks.DEEPSLATE_IRON_ORE)),
            Map.entry(Blocks.COPPER_ORE, () -> Optional.of(Blocks.GOLD_ORE)),
            Map.entry(Blocks.DEEPSLATE_COPPER_ORE, () -> Optional.of(Blocks.DEEPSLATE_GOLD_ORE)),
            Map.entry(Blocks.IRON_ORE, () -> Optional.of(Blocks.DIAMOND_ORE)),
            Map.entry(Blocks.DEEPSLATE_IRON_ORE, () -> Optional.of(Blocks.DEEPSLATE_DIAMOND_ORE)),
            Map.entry(Blocks.LAPIS_ORE, () -> Optional.of(Blocks.DIAMOND_ORE)),
            Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, () -> Optional.of(Blocks.DEEPSLATE_DIAMOND_ORE)),
            Map.entry(Blocks.REDSTONE_ORE, () -> Optional.of(Blocks.EMERALD_ORE)),
            Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, () -> Optional.of(Blocks.DEEPSLATE_EMERALD_ORE)),
            Map.entry(Blocks.GOLD_ORE, () -> Optional.of(Blocks.EMERALD_ORE)),
            Map.entry(Blocks.DEEPSLATE_GOLD_ORE, () -> Optional.of(Blocks.DEEPSLATE_EMERALD_ORE)),
            Map.entry(Blocks.DIAMOND_ORE, () -> Optional.of(Blocks.ANCIENT_DEBRIS)),
            Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, () -> Optional.of(Blocks.ANCIENT_DEBRIS)),
            Map.entry(Blocks.NETHER_QUARTZ_ORE, () -> Optional.of(Blocks.NETHER_GOLD_ORE)),

            // --- VEGETATION ---
            Map.entry(Blocks.OAK_LOG, () -> Optional.of(Blocks.DARK_OAK_LOG)),
            Map.entry(Blocks.BIRCH_LOG, () -> Optional.of(Blocks.MANGROVE_LOG)),
            Map.entry(Blocks.SPRUCE_LOG, () -> Optional.of(Blocks.CHERRY_LOG)),
            Map.entry(Blocks.JUNGLE_LOG, () -> Optional.of(Blocks.ACACIA_LOG)),
            Map.entry(Blocks.ACACIA_LOG, () -> Optional.of(Blocks.JUNGLE_LOG)),
            Map.entry(Blocks.DARK_OAK_LOG, () -> Optional.of(Blocks.OAK_LOG)),
            Map.entry(Blocks.MANGROVE_LOG, () -> Optional.of(Blocks.BIRCH_LOG)),
            Map.entry(Blocks.CHERRY_LOG, () -> Optional.of(Blocks.SPRUCE_LOG)),
            Map.entry(Blocks.MOSS_BLOCK, () -> Optional.of(Blocks.SCULK))
    );

    public LiquidGoldItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        PlayerEntity player = context.getPlayer();
        if (player != null && !player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }

        BlockPos center = context.getBlockPos();
        ServerWorld serverWorld = (ServerWorld) world;

        world.playSound(null, center, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.2f);

        for (int r = 0; r <= MAX_RADIUS; r++) {
            final int currentRadius = r;
            TaskScheduler.schedule(r * RING_DELAY_TICKS, () -> convertRing(serverWorld, center, currentRadius));
        }

        return ActionResult.CONSUME;
    }

    private void convertRing(ServerWorld world, BlockPos center, int radius) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) != radius && Math.abs(y) != radius && Math.abs(z) != radius) {
                        continue;
                    }

                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState originalState = world.getBlockState(mutable);

                    if (CONVERSION_MAP.containsKey(originalState.getBlock())) {
                        CONVERSION_MAP.get(originalState.getBlock()).get().ifPresent(newBlock -> {
                            world.setBlockState(mutable, newBlock.getDefaultState(), 3);
                            world.playSound(null, mutable, originalState.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, originalState.getSoundGroup().getVolume(), originalState.getSoundGroup().getPitch());
                            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, originalState), mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
                        });
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.richesfrombelow.liquid_gold.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);}
}