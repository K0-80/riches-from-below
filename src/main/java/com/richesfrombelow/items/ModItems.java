package com.richesfrombelow.items;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.items.custom.*;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item GATCHA_BALL_ITEM = registerItem("gatcha_ball_item", new GatchaBallItem(new Item.Settings().maxCount(16)));
    public static final Item KOBO_COIN = registerItem("kobo_coin", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item COIN_FRAGMENT = registerItem("coin_fragment", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item CROWN_OF_GREED = registerItem("crown_of_greed",
            new CrownOfGreedItem(ModArmorMaterials.CROWN_OF_GREED, ArmorItem.Type.HELMET, new Item.Settings().rarity(Rarity.EPIC).maxCount(1)
            )); //can store gold, when taking damage, drop some gold, more gold stored = become stronger. droped gold has high pickup delay

    public static final Item PACIFIST_CROWN = registerItem("pacifist_crown",
            new PacifistCrownItem(ModArmorMaterials.PACIFIST_CROWN, ArmorItem.Type.HELMET, new Item.Settings().rarity(Rarity.EPIC).maxCount(1)
            )); //gain reatance 3 and weakness 5 when worn

    public static final Item ALL_IN = registerItem("all_in", new AllInItem(new Item.Settings()
            .rarity(Rarity.UNCOMMON).maxCount(4)));

    public static final Item COLLECTOR_SUITCASE = registerItem("collector_suitcase", new CollectorSuitcaseItem(new Item.Settings()
            .rarity(Rarity.UNCOMMON).maxCount(1)));

    public static final Item DECK_OF_FATES = registerItem("deck_of_fates", new DeckOfFatesItem(new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1)));
    public static final Item DECK_OF_FATES_ANIMATION = registerItem("deck_of_fates_animation", new Item(new Item.Settings()));

    public static final Item WISHING_STAR = registerItem("wishing_star", new WishingStarItem(new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(16)));

    public static final Item FORTUNE_COOKIE = registerItem("fortune_cookie", new FortuneCookieItem(new Item.Settings()
            .food(new FoodComponent.Builder().nutrition(2).saturationModifier(2f).alwaysEdible().build())));

    public static final Item LUCKY_CLOVER = registerItem("lucky_clover", new Item(new Item.Settings()
            .rarity(Rarity.EPIC).maxCount(64)));

    public static final Item LIQUID_GOLD = registerItem("liquid_gold", new LiquidGoldItem(new Item.Settings()
            .rarity(Rarity.UNCOMMON).maxCount(16)));

    public static final Item PLUSHIE = registerItem("plushie", new PlushieItem(new Item.Settings().equipmentSlot((stack, entity) -> EquipmentSlot.HEAD)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(RichesfromBelow.MOD_ID, name), item);
    }
    public static void register() {
    }

}
