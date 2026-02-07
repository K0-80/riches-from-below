package com.richesfrombelow.items;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup RICHES_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(RichesfromBelow.MOD_ID, "riches_items"),
                    FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.KOBO_COIN))
                            .displayName(Text.translatable("itemgroup.riches.riches_items"))
                        .entries((displayContext, entries) -> {

                            entries.add(ModItems.KOBO_COIN);
                            entries.add(ModItems.COIN_FRAGMENT);
                            
                            entries.add(ModBlocks.GACHA_MACHINE_BLOCK);
                            entries.add(ModBlocks.SLOT_MACHINE);

                            entries.add(ModItems.CROWN_OF_GREED);
                            entries.add(ModItems.PACIFIST_CROWN);
                            entries.add(ModItems.ALL_IN);
                            entries.add(ModItems.COLLECTOR_SUITCASE);
                            entries.add(ModItems.WISHING_STAR);
                            entries.add(ModItems.FORTUNE_COOKIE);
                            entries.add(ModItems.LUCKY_CLOVER);
                            entries.add(ModItems.LIQUID_GOLD);










                        }).build());


    public static void register() {
    }
}