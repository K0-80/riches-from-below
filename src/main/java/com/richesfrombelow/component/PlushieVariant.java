package com.richesfrombelow.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record PlushieVariant(String id) {
    public static final Codec<PlushieVariant> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.fieldOf("id").forGetter(PlushieVariant::id)
                    )
                    .apply(instance, PlushieVariant::new)
    );
    public static final PacketCodec<ByteBuf, PlushieVariant> PACKET_CODEC = PacketCodecs.STRING.xmap(PlushieVariant::new, PlushieVariant::id);
}