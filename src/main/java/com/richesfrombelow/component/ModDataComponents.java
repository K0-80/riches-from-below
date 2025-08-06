package com.richesfrombelow.component;

import com.mojang.serialization.Codec;
import com.richesfrombelow.RichesfromBelow;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModDataComponents {

    public static final ComponentType<PlushieVariant> PLUSHIE_VARIANT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(RichesfromBelow.MOD_ID, "plushie_variant"),
            ComponentType.<PlushieVariant>builder().codec(PlushieVariant.CODEC).packetCodec(PlushieVariant.PACKET_CODEC).build()
    );


    public static void register() {
    }
}