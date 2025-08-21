package com.richesfrombelow.block.entity.client;

import com.richesfrombelow.RichesfromBelow;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer SLOT_MACHINE =
            new EntityModelLayer(Identifier.of(RichesfromBelow.MOD_ID, "slot_machine"), "main");
}
