package com.richesfrombelow;

import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.entities.client.GatchaBallModel;
import com.richesfrombelow.entities.client.GatchaBallRenderer;
import com.richesfrombelow.entities.client.ModModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RichesfromBelowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GATCHA_BALL, GatchaBallModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.GATCHA_BALL, GatchaBallRenderer::new);

    }
}
