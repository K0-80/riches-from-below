package com.richesfrombelow.entities;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.entities.custom.GatchaBallEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<GatchaBallEntity> GATCHA_BALL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(RichesfromBelow.MOD_ID, "gatcha_ball"),
            EntityType.Builder.create(GatchaBallEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3F, 0.3F).build());


    public static void register() {
        FabricDefaultAttributeRegistry.register(ModEntities.GATCHA_BALL, GatchaBallEntity.createGatchaBallAttributes());
    }
}
