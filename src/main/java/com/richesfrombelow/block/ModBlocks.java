package com.richesfrombelow.block;

import com.richesfrombelow.RichesfromBelow;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {


    public static final Block GACHA_MACHINE_BLOCK = registerBlock("gacha_machine_block",
            new GachaMachineBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()
//                    .suffocates((state, world, pos) -> false)
//                    .blockVision((state, world, pos) -> false)
            ));


    // Helper for blocks with a standard item
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block, new Item.Settings());
        return Registry.register(Registries.BLOCK, Identifier.of(RichesfromBelow.MOD_ID, name), block);
    }

    // Helper for blocks without auto item registiotn
    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(RichesfromBelow.MOD_ID, name), block);
    }

    // Helper to register a BlockItem with specific settings
    private static Item registerBlockItem(String name, Block block, Item.Settings settings) {
        return Registry.register(Registries.ITEM, Identifier.of(RichesfromBelow.MOD_ID, name),
                new BlockItem(block, settings));
    }

    public static void register() {
    }
}
