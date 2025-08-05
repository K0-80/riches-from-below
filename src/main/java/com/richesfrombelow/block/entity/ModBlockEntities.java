package com.richesfrombelow.block.entity;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<GachaMachineBlockEntity> GACHA_MACHINE_BLOCK_ENTITY_TYPE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(RichesfromBelow.MOD_ID, "gacha_machine_be"),
                    FabricBlockEntityTypeBuilder.create(GachaMachineBlockEntity::new,
                            ModBlocks.GACHA_MACHINE_BLOCK).build());


    public static void register() {
    }
}