# RichesfromBelow Dev README

This is a focused, engineer-facing overview of the project as it currently exists.
It is intentionally terse and biased toward "where to change behavior".

## Build + Target
- Minecraft: 1.21 (Fabric)
- Java: 21
- Loader: 0.16.14
- Fabric API: 0.102.0+1.21
- Build system: Gradle + fabric-loom

Key files:
- build.gradle
- gradle.properties
- fabric.mod.json

## Entrypoints
- Main init: src/main/java/com/richesfrombelow/RichesfromBelow.java
  - Registers entities, blocks, block entities, items, item group, components, loot modifiers, commands, scheduler
  - Registers brewing recipe: Awkward + Lucky Clover -> Luck potion
- Client init: src/main/java/com/richesfrombelow/RichesfromBelowClient.java
  - Registers S2C packet receiver for Deck of Fates animation
  - Registers entity renderers and model layers
  - Registers block entity renderers and cutout layers
  - HUD renderer for card animation
- Datagen: src/main/java/com/richesfrombelow/RichesfromBelowDataGenerator.java
  - Hooks all providers

## Core Systems

### Currency
- K0BO Coin: ModItems.KOBO_COIN
- Coin Fragment: ModItems.COIN_FRAGMENT
- Recipe: reversible compacting via datagen (fragments <-> coin)

### Blocks
- Gacha Machine: ModBlocks.GACHA_MACHINE_BLOCK
  - Block: src/main/java/com/richesfrombelow/block/GachaMachineBlock.java
  - BE: src/main/java/com/richesfrombelow/block/entity/GachaMachineBlockEntity.java
  - Behavior: insert K0BO coin -> roll tiered loot -> animate -> spawn GatchaBallEntity

- Slot Machine: ModBlocks.SLOT_MACHINE
  - Block: src/main/java/com/richesfrombelow/block/SlotMachineBlock.java
  - BE: src/main/java/com/richesfrombelow/block/entity/SlotMachineBlockEntity.java
  - Behavior: insert K0BO coin -> spin 3 wheels -> payout by match tier

- Personal Vault: ModBlocks.PERSONAL_VAULT
  - Block: src/main/java/com/richesfrombelow/block/PersonalVaultBlock.java
  - BE: src/main/java/com/richesfrombelow/block/entity/PersonalVaultBlockEntity.java
  - Behavior:
    - Owner set on placement
    - Locked state with timer (1 hour per coin fragment)
    - Sneak-use shows remaining lock time
    - Non-owner cannot open when locked

### Entities
- Gatcha Ball entity: ModEntities.GATCHA_BALL
  - Entity: src/main/java/com/richesfrombelow/entities/custom/GatchaBallEntity.java
  - Renderer + model: src/main/java/com/richesfrombelow/entities/client/*
  - Drops loot on player damage; tier-specific particle effects + textures

### Items (custom behavior)
- All In: consumes all K0BO coins, buffs scale with count
  - src/main/java/com/richesfrombelow/items/custom/AllInItem.java
- Collector Suitcase: mob drops coin fragments via mixin
  - src/main/java/com/richesfrombelow/items/custom/CollectorSuitcaseItem.java
  - src/main/java/com/richesfrombelow/mixin/LivingEntityMixin.java
- Crown of Greed: stores gold value, alters damage taken
  - src/main/java/com/richesfrombelow/items/custom/CrownOfGreedItem.java
  - src/main/java/com/richesfrombelow/mixin/ItemEntityMixin.java
  - src/main/java/com/richesfrombelow/mixin/PlayerEntityDamageMixin.java
- Pacifist Crown: permanent Resistance III + Weakness V while worn
  - src/main/java/com/richesfrombelow/items/custom/PacifistCrownItem.java
- Deck of Fates: random card effect + animation
  - src/main/java/com/richesfrombelow/items/custom/DeckOfFatesItem.java
  - Animation: src/main/java/com/richesfrombelow/client/DeckOfFatesAnimationRenderer.java
  - Networking: src/main/java/com/richesfrombelow/networking/*
- Fortune Cookie: fortune text / buffs / rare treasure placement
  - src/main/java/com/richesfrombelow/items/custom/FortuneCookieItem.java
- Wishing Star: gamble XP, desired level set by sneak-use
  - src/main/java/com/richesfrombelow/items/custom/WishingStarItem.java
- Liquid Gold: radial block conversion over time
  - src/main/java/com/richesfrombelow/items/custom/LiquidGoldItem.java
- Plushie: data-component variant naming
  - src/main/java/com/richesfrombelow/items/custom/PlushieItem.java

## Loot + Odds
- Gacha tiers/weights + loot pools:
  - src/main/java/com/richesfrombelow/util/GatchaBallLootTableUtil.java
- Fortune cookie treasure loot table:
  - src/main/java/com/richesfrombelow/loot/ModLootTables.java
  - src/main/generated/data/richesfrombelow/loot_table/chests/fortune_cookie_treasure.json
- Grass loot modifier (Lucky Clover 1% with hoe):
  - src/main/java/com/richesfrombelow/util/ModLootTableModifiers.java

## Mixins
- VaultBlockMixin: convert vanilla Vault -> Personal Vault with K0BO coin
  - src/main/java/com/richesfrombelow/mixin/VaultBlockMixin.java
- ItemEntityMixin: auto-consume gold into Crown of Greed
- PlayerEntityDamageMixin: damage scaling based on Crown gold
- LivingEntityMixin: Collector Suitcase extra drops

## Commands (Dev)
- /testslot <r1> <r2> <r3> force slot results
  - src/main/java/com/richesfrombelow/command/TestSlotCommand.java
- /gatchaballloottest <count> spam loot rolls
  - src/main/java/com/richesfrombelow/command/TestLootCommand.java

## Client Rendering
- Slot machine animated BE renderer + model
  - src/main/java/com/richesfrombelow/block/entity/client/*
- Gacha machine BE renderer
  - src/main/java/com/richesfrombelow/block/entity/client/renderer/GachaMachineBlockEntityRenderer.java

## Datagen Providers
- Recipes: ModRecipeProvider
- Loot tables: ModLootTableProvider
- Models: ModModelProvider
- Language: ModLanguageProvider
- Tags: ModBlockTagProvider, ModItemTagProvider

## Assets
- Textures: src/main/resources/assets/richesfrombelow/textures
- Models: src/main/resources/assets/richesfrombelow/models
- Blockstates: src/main/resources/assets/richesfrombelow/blockstates
- Lang: src/main/generated/assets/richesfrombelow/lang/en_us.json (generated)

## Quick "Where To Change" Map
- Slot payouts: src/main/java/com/richesfrombelow/block/entity/SlotMachineBlockEntity.java
- Gacha odds/loot: src/main/java/com/richesfrombelow/util/GatchaBallLootTableUtil.java
- Vault lock logic: src/main/java/com/richesfrombelow/block/PersonalVaultBlock.java
- Fortune cookie logic: src/main/java/com/richesfrombelow/items/custom/FortuneCookieItem.java
- Deck of Fates effects: src/main/java/com/richesfrombelow/items/custom/DeckOfFatesItem.java

## Notes / Known Oddities
- Two ModModelLayers classes exist in different packages with SLOT_MACHINE.
- ModRegistryDataGenerator.getName() returns empty string.
