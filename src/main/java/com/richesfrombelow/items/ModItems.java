package com.richesfrombelow.items;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.custom.GatchaBallItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item GATCHA_BALL_ITEM = registerItem("gatcha_ball_item", new GatchaBallItem(new Item.Settings().maxCount(16)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(RichesfromBelow.MOD_ID, name), item);
    }
    public static void register() {//
    }
}
