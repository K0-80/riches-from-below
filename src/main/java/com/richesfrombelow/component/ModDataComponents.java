package com.richesfrombelow.component;

import com.mojang.serialization.Codec;
import com.richesfrombelow.RichesfromBelow;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final ComponentType<PlushieVariant> PLUSHIE_VARIANT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(RichesfromBelow.MOD_ID, "plushie_variant"),
            ComponentType.<PlushieVariant>builder().codec(PlushieVariant.CODEC).packetCodec(PlushieVariant.PACKET_CODEC).build()
    );


    public static final ComponentType<Integer> GOLD_CONSUMED =
            register("gold_consumed", builder -> builder.codec(Codec.INT));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(RichesfromBelow.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }


    public static void register() {
    }
}