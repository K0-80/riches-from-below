package com.richesfrombelow.networking;

import com.richesfrombelow.items.custom.DeckOfFatesItem;
import com.richesfrombelow.networking.packet.DrawCardS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPackets {
    public static void registerS2CPackets() {
        PayloadTypeRegistry.playS2C().register(DrawCardS2CPacket.ID, DrawCardS2CPacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(DrawCardS2CPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                DeckOfFatesItem.drawCardAnimation(payload.cardId());
            });
        });
    }
}