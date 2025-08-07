package com.richesfrombelow.networking.packet;

import com.richesfrombelow.RichesfromBelow;
import com.richesfrombelow.RichesfromBelowClient;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DrawCardS2CPacket(int cardId) implements CustomPayload {
    public static final CustomPayload.Id<DrawCardS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(RichesfromBelow.MOD_ID, "draw_card_s2c"));
    public static final PacketCodec<ByteBuf, DrawCardS2CPacket> CODEC = PacketCodec.of((value, buf) -> buf.writeInt(value.cardId), buf -> new DrawCardS2CPacket(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}