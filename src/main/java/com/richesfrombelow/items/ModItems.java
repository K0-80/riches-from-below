package com.richesfrombelow.items;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.custom.GatchaBallItem;
import com.richesfrombelow.items.custom.PlushieItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item GATCHA_BALL_ITEM = registerItem("gatcha_ball_item", new GatchaBallItem(new Item.Settings().maxCount(16)));
    public static final Item KOBO_COIN = registerItem("kobo_coin", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item PLUSHIE = registerItem("plushie", new PlushieItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(RichesfromBelow.MOD_ID, name), item);
    }
    public static void register() {
    }

}
