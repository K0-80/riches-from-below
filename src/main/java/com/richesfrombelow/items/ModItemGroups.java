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

                            entries.add(ModItems.COIN_FRAGMENT);
                            entries.add(ModItems.KOBO_COIN);
                            entries.add(ModBlocks.GACHA_MACHINE_BLOCK);

                            entries.add(ModItems.CROWN_OF_GREED);
                            entries.add(ModItems.PACIFIST_CROWN);
                            entries.add(ModItems.ALL_IN);
                            entries.add(ModItems.COLLECTOR_SUITCASE);
                            entries.add(ModItems.WISHING_STAR);
                            entries.add(ModItems.FORTUNE_COOKIE);
                            entries.add(ModItems.LUCKY_CLOVER);
                            entries.add(ModItems.LIQUID_GOLD);

                            //INF PLUSH!!!!
//                            entries.add(ModItems.PLUSHIE);
//
//                            ItemStack k08Plushie = new ItemStack(ModItems.PLUSHIE);
//                            k08Plushie.set(ModDataComponents.PLUSHIE_VARIANT, new PlushieVariant("k08"));
//                            k08Plushie.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(2));
//                            entries.add(k08Plushie);
                            //INF PLUSH!!!

                        }).build());


    public static void register() {
    }
}